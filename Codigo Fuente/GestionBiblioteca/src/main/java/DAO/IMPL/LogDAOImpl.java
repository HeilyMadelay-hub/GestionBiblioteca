package DAO.IMPL;

import DAO.LogDAO;
import Modelo.Log;
import Util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDAOImpl implements LogDAO {
    private static final String SQL_INSERT = 
        "INSERT INTO logs (id_tipo_log, id_usuario, accion, detalles, fecha) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = 
        "SELECT * FROM logs WHERE id_log = ?";
    private static final String SQL_SELECT_ALL = 
        "SELECT * FROM logs ORDER BY fecha DESC";
    private static final String SQL_SELECT_BY_TIPO = 
        "SELECT * FROM logs WHERE id_tipo_log = ? ORDER BY fecha DESC";
    private static final String SQL_SELECT_BY_USUARIO = 
        "SELECT * FROM logs WHERE id_usuario = ? ORDER BY fecha DESC";
    private static final String SQL_SELECT_BY_FECHA = 
        "SELECT * FROM logs WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
    private static final String SQL_DELETE = 
        "DELETE FROM logs WHERE id_log = ?";

    @Override
    public void insertar(Log log) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, log.getIdTipoLog());
            stmt.setObject(2, log.getIdUsuario());
            stmt.setString(3, log.getAccion());
            stmt.setString(4, log.getDetalles());
            stmt.setTimestamp(5, Timestamp.valueOf(log.getFecha()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setIdLog(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Log obtenerPorId(Integer idLog) throws SQLException {
        Log log = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            stmt.setInt(1, idLog);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    log = mapearLog(rs);
                }
            }
        }
        return log;
    }

    @Override
    public List<Log> obtenerTodos() throws SQLException {
        List<Log> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                logs.add(mapearLog(rs));
            }
        }
        return logs;
    }

    @Override
    public List<Log> obtenerPorTipo(Integer idTipoLog) throws SQLException {
        List<Log> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_TIPO)) {
            
            stmt.setInt(1, idTipoLog);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapearLog(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public List<Log> obtenerPorUsuario(Integer idUsuario) throws SQLException {
        List<Log> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_USUARIO)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapearLog(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public List<Log> obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
        List<Log> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_FECHA)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapearLog(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public void eliminar(Integer idLog) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setInt(1, idLog);
            stmt.executeUpdate();
        }
    }

    private Log mapearLog(ResultSet rs) throws SQLException {
        Log log = new Log();
        log.setIdLog(rs.getInt("id_log"));
        log.setIdTipoLog(rs.getInt("id_tipo_log"));
        log.setIdUsuario(rs.getObject("id_usuario", Integer.class));
        log.setAccion(rs.getString("accion"));
        log.setDetalles(rs.getString("detalles"));
        log.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        return log;
    }
}