package DAO.IMPL;

import DAO.ReservaDAO;
import Modelo.Reserva;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ReservaDAOImpl implements ReservaDAO {
    private static final String SQL_INSERT = "INSERT INTO reservas (id_usuario, id_libro, id_estado_reserva, fecha_reserva, fecha_vencimiento) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE reservas SET id_usuario = ?, id_libro = ?, id_estado_reserva = ?, fecha_reserva = ?, fecha_vencimiento = ? WHERE id_reserva = ?";
    private static final String SQL_DELETE = "DELETE FROM reservas WHERE id_reserva = ?";
    private static final String SQL_SELECT = "SELECT * FROM reservas WHERE id_reserva = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reservas ORDER BY fecha_reserva DESC";

    @Override
    public void insertar(Reserva reserva) throws Exception {
        // Validar límite de reservas activas
        if (contarReservasActivas(reserva.getIdUsuario()) >= 3) {
            throw new Exception("El usuario ha alcanzado el límite de reservas activas");
        }
        
        // Validar que no tenga una reserva activa para el mismo libro
        if (tieneReservaActiva(reserva.getIdUsuario(), reserva.getIdLibro())) {
            throw new Exception("El usuario ya tiene una reserva activa para este libro");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reserva.getIdUsuario());
            stmt.setInt(2, reserva.getIdLibro());
            stmt.setInt(3, reserva.getIdEstadoReserva());
            stmt.setTimestamp(4, new java.sql.Timestamp(reserva.getFechaReserva().getTime()));
            stmt.setDate(5, new java.sql.Date(reserva.getFechaVencimiento().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reserva.setIdReserva(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Reserva reserva) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setInt(1, reserva.getIdUsuario());
            stmt.setInt(2, reserva.getIdLibro());
            stmt.setInt(3, reserva.getIdEstadoReserva());
            stmt.setTimestamp(4, new java.sql.Timestamp(reserva.getFechaReserva().getTime()));
            stmt.setDate(5, new java.sql.Date(reserva.getFechaVencimiento().getTime()));
            stmt.setInt(6, reserva.getIdReserva());
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró la reserva con ID: " + reserva.getIdReserva());
            }
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setInt(1, id);
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró la reserva con ID: " + id);
            }
        }
    }

    @Override
    public void eliminarTodos() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE reservas");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Override
    public Reserva obtenerPorId(Integer id) throws Exception {
        Reserva reserva = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva = mapearResultSetAReserva(rs);
                }
            }
        }
        return reserva;
    }

    @Override
    public List<Reserva> obtenerTodos() throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorUsuario(Integer idUsuario) throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_usuario = ? ORDER BY fecha_reserva DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearResultSetAReserva(rs));
                }
            }
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorLibro(Integer idLibro) throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_libro = ? ORDER BY fecha_reserva DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idLibro);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearResultSetAReserva(rs));
                }
            }
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorEstado(Integer idEstado) throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_estado_reserva = ? ORDER BY fecha_reserva DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEstado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearResultSetAReserva(rs));
                }
            }
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorFechas(Date fechaInicio, Date fechaFin) throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE fecha_reserva BETWEEN ? AND ? ORDER BY fecha_reserva DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(fechaFin.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearResultSetAReserva(rs));
                }
            }
        }
        return reservas;
    }

    @Override
    public void cambiarEstado(Integer idReserva, Integer nuevoEstado) throws Exception {
        String sql = "UPDATE reservas SET id_estado_reserva = ? WHERE id_reserva = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nuevoEstado);
            stmt.setInt(2, idReserva);
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró la reserva con ID: " + idReserva);
            }
        }
    }

    @Override
    public List<Reserva> obtenerReservasVencidas() throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE fecha_vencimiento < CURRENT_DATE AND id_estado_reserva = 1 ORDER BY fecha_vencimiento";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }
    
    @Override
    public List<Reserva> obtenerReservasPendientes() throws Exception {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_estado_reserva = 1 ORDER BY fecha_reserva";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    @Override
    public boolean tieneReservaActiva(Integer idUsuario, Integer idLibro) throws Exception {
        String sql = "SELECT COUNT(*) FROM reservas WHERE id_usuario = ? AND id_libro = ? AND id_estado_reserva = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idLibro);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public int contarReservasActivas(Integer idUsuario) throws Exception {
        String sql = "SELECT COUNT(*) FROM reservas WHERE id_usuario = ? AND id_estado_reserva = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Reserva mapearResultSetAReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setIdUsuario(rs.getInt("id_usuario"));
        reserva.setIdLibro(rs.getInt("id_libro"));
        reserva.setIdEstadoReserva(rs.getInt("id_estado_reserva"));
        reserva.setFechaReserva(rs.getTimestamp("fecha_reserva"));
        reserva.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        return reserva;
    }
}