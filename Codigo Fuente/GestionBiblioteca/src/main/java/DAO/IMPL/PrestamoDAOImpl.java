package DAO.IMPL;

import DAO.PrestamoDAO;
import Modelo.Prestamo;
import Util.DatabaseUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {
    
    private static final String SQL_INSERT = "INSERT INTO prestamos (id_usuario, id_libro, id_tipo_prestamo, fecha_prestamo, fecha_devolucion_esperada) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE prestamos SET id_usuario = ?, id_libro = ?, id_tipo_prestamo = ?, fecha_prestamo = ?, fecha_devolucion_esperada = ?, fecha_devolucion_real = ? WHERE id_prestamo = ?";
    private static final String SQL_DELETE = "DELETE FROM prestamos WHERE id_prestamo = ?";
    private static final String SQL_SELECT = "SELECT * FROM prestamos WHERE id_prestamo = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";

    
    
    @Override
    public void eliminarTodos() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE prestamos_express");
            stmt.execute("TRUNCATE TABLE prestamos");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Override
    public List<Prestamo> obtenerPrestamosActivos() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE fecha_devolucion_real IS NULL ORDER BY fecha_prestamo";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prestamos.add(mapearResultSetAPrestamo(rs));
            }
        }
        return prestamos;
    }

    @Override
    public List<Prestamo> obtenerPrestamosVencidos() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE fecha_devolucion_real IS NULL AND fecha_devolucion_esperada < CURRENT_DATE ORDER BY fecha_devolucion_esperada";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prestamos.add(mapearResultSetAPrestamo(rs));
            }
        }
        return prestamos;
    }
    
    @Override
    public List<Prestamo> obtenerPrestamosExpress() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, pe.dias_reales FROM prestamos p " +
                    "INNER JOIN prestamos_express pe ON p.id_prestamo = pe.id_prestamo " +
                    "ORDER BY p.fecha_prestamo";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = mapearResultSetAPrestamo(rs);
                prestamo.setDiasReales(rs.getInt("dias_reales"));
                prestamos.add(prestamo);
            }
        }
        return prestamos;
    }

    @Override
    public List<Prestamo> buscarPorUsuario(Integer idUsuario) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE id_usuario = ? ORDER BY fecha_prestamo DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearResultSetAPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public List<Prestamo> buscarPorLibro(Integer idLibro) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE id_libro = ? ORDER BY fecha_prestamo DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idLibro);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearResultSetAPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public List<Prestamo> buscarPorTipo(Integer idTipoPrestamo) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE id_tipo_prestamo = ? ORDER BY fecha_prestamo DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipoPrestamo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearResultSetAPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

   
    

    @Override
    public boolean tienePrestamoActivo(Integer idUsuario, Integer idLibro) throws Exception {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND id_libro = ? AND fecha_devolucion_real IS NULL";
        
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
    public int contarPrestamosActivos(Integer idUsuario) throws Exception {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND fecha_devolucion_real IS NULL";
        
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

    @Override
    public boolean esPrestamoExpress(Integer idPrestamo) throws Exception {
        String sql = "SELECT COUNT(*) FROM prestamos_express WHERE id_prestamo = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPrestamo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void actualizar(Prestamo prestamo) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setInt(2, prestamo.getIdLibro());
            stmt.setInt(3, prestamo.getIdTipoPrestamo());
            stmt.setTimestamp(4, new java.sql.Timestamp(prestamo.getFechaPrestamo().getTime()));
            stmt.setDate(5, new java.sql.Date(prestamo.getFechaDevolucionEsperada().getTime()));
            stmt.setTimestamp(6, prestamo.getFechaDevolucionReal() != null ? 
                    new java.sql.Timestamp(prestamo.getFechaDevolucionReal().getTime()) : null);
            stmt.setInt(7, prestamo.getIdPrestamo());
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró el préstamo con ID: " + prestamo.getIdPrestamo());
            }
        }
    }

    @Override
    public Prestamo obtenerPorId(Integer id) throws Exception {
        Prestamo prestamo = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prestamo = mapearResultSetAPrestamo(rs);
                    if (esPrestamoExpress(id)) {
                        prestamo.setDiasReales(obtenerDiasRealesExpress(id));
                    }
                }
            }
        }
        return prestamo;
    }

    @Override
    public List<Prestamo> obtenerTodos() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                Prestamo prestamo = mapearResultSetAPrestamo(rs);
                if (esPrestamoExpress(prestamo.getIdPrestamo())) {
                    prestamo.setDiasReales(obtenerDiasRealesExpress(prestamo.getIdPrestamo()));
                }
                prestamos.add(prestamo);
            }
        }
        return prestamos;
    }

    private Integer obtenerDiasRealesExpress(Integer idPrestamo) throws SQLException {
        String sql = "SELECT dias_reales FROM prestamos_express WHERE id_prestamo = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPrestamo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("dias_reales");
                }
            }
        }
        return null;
    }

   

private Prestamo mapearResultSetAPrestamo(ResultSet rs) throws SQLException {
    Prestamo prestamo = new Prestamo();
    prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
    prestamo.setIdUsuario(rs.getInt("id_usuario"));
    prestamo.setIdLibro(rs.getInt("id_libro"));
    prestamo.setIdTipoPrestamo(rs.getInt("id_tipo_prestamo"));
    prestamo.setFechaPrestamo(rs.getTimestamp("fecha_prestamo"));
    prestamo.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada"));
    Timestamp fechaDevolucionReal = rs.getTimestamp("fecha_devolucion_real");
    if (fechaDevolucionReal != null) {
        prestamo.setFechaDevolucionReal(new Date(fechaDevolucionReal.getTime()));
    }
    // Añadir esta línea para mapear el orden_prestamo
    prestamo.setOrdenPrestamo(rs.getInt("orden_prestamo"));
    return prestamo;
}

    

    public  void registrarPrestamoExpress(Integer idPrestamo, Integer diasReales, Connection conn) throws SQLException {
        String sql = "INSERT INTO prestamos_express (id_prestamo, dias_reales) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.setInt(2, diasReales);
            stmt.executeUpdate();
        }
    }
    
 

    @Override
    public List<Prestamo> buscarPorFechas(java.util.Date fechaInicio, java.util.Date fechaFin) throws Exception {
        
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE fecha_prestamo BETWEEN ? AND ? ORDER BY fecha_prestamo";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(fechaFin.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearResultSetAPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public void registrarDevolucion(Integer idPrestamo, java.util.Date fechaDevolucion) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Obtener información del préstamo
            Prestamo prestamo = obtenerPorId(idPrestamo);
            if (prestamo == null) {
                throw new Exception("No se encontró el préstamo");
            }

            // Registrar la devolución
            String sql = "UPDATE prestamos SET fecha_devolucion_real = ? WHERE id_prestamo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, new java.sql.Timestamp(fechaDevolucion.getTime()));
                stmt.setInt(2, idPrestamo);
                stmt.executeUpdate();
            }

            // Actualizar estado del libro a disponible
            try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE libros SET id_estado = (SELECT id_estado FROM estados_libro WHERE nombre = 'disponible') " +
                "WHERE id_libro = ?")) {
                stmt.setInt(1, prestamo.getIdLibro());
                stmt.executeUpdate();
            }

            // Verificar si es préstamo express
            long diasReales = (fechaDevolucion.getTime() - prestamo.getFechaPrestamo().getTime()) 
                / (1000 * 60 * 60 * 24);
            if (diasReales <= 7) {
                // Verificar si ya existe un registro express para este préstamo
                boolean existeExpress = false;
                try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 1 FROM prestamos_express WHERE id_prestamo = ?")) {
                    checkStmt.setInt(1, idPrestamo);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        existeExpress = rs.next();
                    }
                }
                // Solo insertar si no existe
                if (!existeExpress) {
                    String sqlExpress = "INSERT INTO prestamos_express (id_prestamo, dias_reales) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlExpress)) {
                        stmt.setInt(1, idPrestamo);
                        stmt.setInt(2, (int) diasReales);
                        stmt.executeUpdate();
                    }
                }
            }

            // Reducir el contador de préstamos activos del usuario
            try (PreparedStatement updateUsuario = conn.prepareStatement(
                "UPDATE usuarios SET prestamos_activos = prestamos_activos - 1 WHERE id_usuario = ?")) {
                updateUsuario.setInt(1, prestamo.getIdUsuario());
                updateUsuario.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new Exception("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }

    }


   
    @Override
public void insertar(Prestamo prestamo) throws Exception {
    Connection conn = null;
    try {
        conn = DatabaseUtil.getConnection();
        conn.setAutoCommit(false);

        // Verificar rol del usuario y número de préstamos totales (activos + históricos)
        String sqlRol = "SELECT r.nombre_rol, " +
                        "(SELECT COUNT(*) FROM prestamos p " +
                        "WHERE p.id_usuario = u.id_usuario AND p.fecha_devolucion_real IS NULL) as prestamos_activos, " +
                        "(SELECT COUNT(*) FROM prestamos p " +
                        "WHERE p.id_usuario = u.id_usuario) as total_prestamos " +
                        "FROM usuarios u " +
                        "JOIN roles r ON u.id_rol = r.id_rol " +
                        "WHERE u.id_usuario = ?";
        
        try (PreparedStatement stmtRol = conn.prepareStatement(sqlRol)) {
            stmtRol.setInt(1, prestamo.getIdUsuario());
            ResultSet rs = stmtRol.executeQuery();
            if (rs.next()) {
                String rol = rs.getString("nombre_rol");
                int prestamosActivos = rs.getInt("prestamos_activos");
                int totalPrestamos = rs.getInt("total_prestamos");

                if ("usuario".equals(rol) && prestamosActivos >= 3) {
                    throw new Exception("Usuario regular no puede tener más de 3 préstamos activos");
                }
            } else {
                throw new Exception("Usuario no encontrado");
            }
        }

        // Verificar disponibilidad del libro
        String sqlLibro = "SELECT el.nombre " +
                          "FROM libros l " +
                          "JOIN estados_libro el ON l.id_estado = el.id_estado " +
                          "WHERE l.id_libro = ?";
        try (PreparedStatement stmtLibro = conn.prepareStatement(sqlLibro)) {
            stmtLibro.setInt(1, prestamo.getIdLibro());
            ResultSet rs = stmtLibro.executeQuery();
            if (rs.next() && !"disponible".equals(rs.getString("nombre"))) {
                throw new Exception("El libro no está disponible para préstamo");
            }
        }

        // Verificar si el usuario ya tiene este libro prestado
        String sqlPrestamoActivo = "SELECT COUNT(*) FROM prestamos " +
                                  "WHERE id_usuario = ? AND id_libro = ? AND fecha_devolucion_real IS NULL";
        try (PreparedStatement stmtPrestamoActivo = conn.prepareStatement(sqlPrestamoActivo)) {
            stmtPrestamoActivo.setInt(1, prestamo.getIdUsuario());
            stmtPrestamoActivo.setInt(2, prestamo.getIdLibro());
            ResultSet rs = stmtPrestamoActivo.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new Exception("El usuario ya tiene este libro prestado");
            }
        }

        // Obtener el siguiente orden disponible
        String sqlOrden = "SELECT COALESCE(MIN(t.orden_prestamo), 1) " +
                         "FROM (SELECT ROW_NUMBER() OVER (ORDER BY orden_prestamo) AS rn, " +
                         "             orden_prestamo " +
                         "      FROM prestamos " +
                         "      WHERE id_usuario = ? AND fecha_devolucion_real IS NULL) t " +
                         "WHERE t.rn != t.orden_prestamo";
        
        int nuevoOrden;
        try (PreparedStatement stmtOrden = conn.prepareStatement(sqlOrden)) {
            stmtOrden.setInt(1, prestamo.getIdUsuario());
            ResultSet rs = stmtOrden.executeQuery();
            if (rs.next()) {
                nuevoOrden = rs.getInt(1);
            } else {
                String sqlMaxOrden = "SELECT COALESCE(MAX(orden_prestamo), 0) + 1 " +
                                   "FROM prestamos WHERE id_usuario = ? AND fecha_devolucion_real IS NULL";
                try (PreparedStatement stmtMaxOrden = conn.prepareStatement(sqlMaxOrden)) {
                    stmtMaxOrden.setInt(1, prestamo.getIdUsuario());
                    ResultSet rsMax = stmtMaxOrden.executeQuery();
                    rsMax.next();
                    nuevoOrden = rsMax.getInt(1);
                }
            }
        }

        // Insertar el préstamo
        String SQL_INSERT_WITH_ORDER = "INSERT INTO prestamos " +
        "(orden_prestamo, id_usuario, id_libro, id_tipo_prestamo, fecha_prestamo, fecha_devolucion_esperada) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_WITH_ORDER, Statement.RETURN_GENERATED_KEYS)) {
    stmt.setInt(1, nuevoOrden);
    stmt.setInt(2, prestamo.getIdUsuario());
    stmt.setInt(3, prestamo.getIdLibro());
    stmt.setInt(4, prestamo.getIdTipoPrestamo());
    stmt.setTimestamp(5, new java.sql.Timestamp(prestamo.getFechaPrestamo().getTime()));
    stmt.setDate(6, new java.sql.Date(prestamo.getFechaDevolucionEsperada().getTime()));

    int affectedRows = stmt.executeUpdate();
    if (affectedRows == 0) {
        throw new Exception("Error: No se insertó el préstamo");
    }

    // Obtener el ID generado
    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
            prestamo.setIdPrestamo(generatedKeys.getInt(1));  // Asignamos el ID generado al objeto
        } else {
            throw new Exception("Error: No se pudo obtener el ID del préstamo insertado");
        }
    }
}

   

        // Actualizar estado del libro a prestado
        String sqlUpdateLibro = "UPDATE libros SET id_estado = " +
                              "(SELECT id_estado FROM estados_libro WHERE nombre = 'prestado') " +
                              "WHERE id_libro = ?";
        try (PreparedStatement stmtUpdateLibro = conn.prepareStatement(sqlUpdateLibro)) {
            stmtUpdateLibro.setInt(1, prestamo.getIdLibro());
            stmtUpdateLibro.executeUpdate();
        }

        // Actualizar número de préstamos activos del usuario
        String sqlUpdateUsuario = "UPDATE usuarios SET prestamos_activos = " +
                                "(SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND fecha_devolucion_real IS NULL) " +
                                "WHERE id_usuario = ?";
        try (PreparedStatement updateUsuario = conn.prepareStatement(sqlUpdateUsuario)) {
            updateUsuario.setInt(1, prestamo.getIdUsuario());
            updateUsuario.setInt(2, prestamo.getIdUsuario());
            updateUsuario.executeUpdate();
        }

        conn.commit();
    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new Exception("Error al hacer rollback: " + ex.getMessage());
            }
        }
        throw new Exception("Error al insertar el préstamo: " + e.getMessage());
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                throw new Exception("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}

@Override
public void eliminar(Integer id) throws Exception {
    Connection conn = null;
    try {
        conn = DatabaseUtil.getConnection();
        conn.setAutoCommit(false);

        // 1. Obtener información del préstamo y su orden antes de eliminarlo
        Prestamo prestamo = null;
        Integer ordenActual = null;
        try (PreparedStatement stmtSelect = conn.prepareStatement(
                "SELECT p.*, COALESCE(orden_prestamo, 0) as orden_prestamo " +
                "FROM prestamos p WHERE p.id_prestamo = ?")) {
            stmtSelect.setInt(1, id);
            ResultSet rs = stmtSelect.executeQuery();
            if (rs.next()) {
                prestamo = mapearResultSetAPrestamo(rs);
                ordenActual = rs.getInt("orden_prestamo");
            }
        }

        if (prestamo == null) {
            throw new Exception("Préstamo no encontrado con ID: " + id);
        }

        // 2. Verificar si el préstamo está activo antes de actualizar contadores
        boolean prestamoActivo = false;
        try (PreparedStatement stmtCheck = conn.prepareStatement(
                "SELECT 1 FROM prestamos WHERE id_prestamo = ? AND fecha_devolucion_real IS NULL")) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            prestamoActivo = rs.next();
        }

        // 3. Actualizar el estado del libro a disponible
        try (PreparedStatement updateLibro = conn.prepareStatement(
                "UPDATE libros SET id_estado = (SELECT id_estado FROM estados_libro WHERE nombre = 'disponible') " +
                "WHERE id_libro = ?")) {
            updateLibro.setInt(1, prestamo.getIdLibro());
            updateLibro.executeUpdate();
        }

        // 4. Eliminar registros relacionados en prestamos_express si existen
        try (PreparedStatement deleteExpress = conn.prepareStatement(
                "DELETE FROM prestamos_express WHERE id_prestamo = ?")) {
            deleteExpress.setInt(1, id);
            deleteExpress.executeUpdate();
        }

        // 5. Eliminar el préstamo
        try (PreparedStatement deletePrestamo = conn.prepareStatement(
                "DELETE FROM prestamos WHERE id_prestamo = ?")) {
            deletePrestamo.setInt(1, id);
            if (deletePrestamo.executeUpdate() == 0) {
                throw new Exception("No se pudo eliminar el préstamo");
            }
        }

        // 6. Reordenar los préstamos restantes solo si el préstamo eliminado estaba activo
        if (prestamoActivo && ordenActual > 0) {
            try (PreparedStatement stmtReordenar = conn.prepareStatement(
                    "UPDATE prestamos SET orden_prestamo = orden_prestamo - 1 " +
                    "WHERE id_usuario = ? " +
                    "AND fecha_devolucion_real IS NULL " +
                    "AND orden_prestamo > ?")) {
                stmtReordenar.setInt(1, prestamo.getIdUsuario());
                stmtReordenar.setInt(2, ordenActual);
                stmtReordenar.executeUpdate();
            }

            // 7. Reducir el contador de préstamos activos del usuario
            try (PreparedStatement updateUsuario = conn.prepareStatement(
                    "UPDATE usuarios SET prestamos_activos = prestamos_activos - 1 " +
                    "WHERE id_usuario = ? AND prestamos_activos > 0")) {
                updateUsuario.setInt(1, prestamo.getIdUsuario());
                updateUsuario.executeUpdate();
            }
        }

        conn.commit();
    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new Exception("Error al hacer rollback: " + ex.getMessage());
            }
        }
        throw new Exception("Error al eliminar el préstamo: " + e.getMessage());
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                throw new Exception("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}}