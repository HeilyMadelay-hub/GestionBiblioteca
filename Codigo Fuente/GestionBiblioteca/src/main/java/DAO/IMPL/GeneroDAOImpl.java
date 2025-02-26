package DAO.IMPL;

import DAO.GeneroDAO;
import Modelo.Genero;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAOImpl implements GeneroDAO {
    private static final String SQL_INSERT = "INSERT INTO generos (nombre) VALUES (?)";
    private static final String SQL_UPDATE = "UPDATE generos SET nombre = ? WHERE id_genero = ?";
    private static final String SQL_DELETE = "DELETE FROM generos WHERE id_genero = ?";
    private static final String SQL_SELECT = "SELECT * FROM generos WHERE id_genero = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM generos ORDER BY id_genero";
    private static final String SQL_SELECT_BY_NOMBRE = "SELECT * FROM generos WHERE nombre = ?";

    @Override
    public void insertar(Genero genero) throws Exception {
        // Verificar si ya existe un género con ese nombre
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(SQL_SELECT_BY_NOMBRE)) {
            
            checkStmt.setString(1, genero.getNombre());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe un género con el nombre: " + genero.getNombre());
                }
            }
        }

        // Si no existe, proceder con la inserción
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, genero.getNombre());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    genero.setIdGenero(rs.getInt(1));
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

            try (Statement stmt = conn.createStatement()) {
                // Desactivar restricciones de clave foránea
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

                // 1. Crear tabla temporal con los datos que queremos mantener
                stmt.execute("CREATE TEMPORARY TABLE temp_generos SELECT * FROM generos WHERE id_genero != " + id);

                // 2. Truncar la tabla original
                stmt.execute("TRUNCATE TABLE generos");

                // 3. Reinsertamos los datos desde la temporal
                stmt.execute("INSERT INTO generos (nombre) SELECT nombre FROM temp_generos");

                // 4. Limpieza
                stmt.execute("DROP TEMPORARY TABLE temp_generos");
                
                // 5. Reactivar restricciones de clave foránea
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
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
            throw new Exception("Error al eliminar género: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    throw new Exception("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void eliminarTodos() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                stmt.execute("TRUNCATE TABLE generos");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
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
            throw new Exception("Error al eliminar todos los géneros: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    throw new Exception("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public Genero obtenerPorId(Integer id) throws Exception {
        Genero genero = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    genero = mapearResultSetAGenero(rs);
                }
            }
        }
        return genero;
    }

    @Override
    public List<Genero> obtenerTodos() throws Exception {
        List<Genero> generos = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                generos.add(mapearResultSetAGenero(rs));
            }
        }
        return generos;
    }

    @Override
    public void actualizar(Genero genero) throws Exception {
        // Verificar si el nuevo nombre ya existe en otro género
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT * FROM generos WHERE nombre = ? AND id_genero != ?")) {
            
            checkStmt.setString(1, genero.getNombre());
            checkStmt.setInt(2, genero.getIdGenero());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe otro género con el nombre: " + genero.getNombre());
                }
            }
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, genero.getNombre());
            stmt.setInt(2, genero.getIdGenero());
            
            int filasActualizadas = stmt.executeUpdate();
            if (filasActualizadas == 0) {
                throw new Exception("No se encontró el género con ID: " + genero.getIdGenero());
            }
        }
    }

    private Genero mapearResultSetAGenero(ResultSet rs) throws SQLException {
        Genero genero = new Genero();
        genero.setIdGenero(rs.getInt("id_genero"));
        genero.setNombre(rs.getString("nombre"));
        return genero;
    }
}

