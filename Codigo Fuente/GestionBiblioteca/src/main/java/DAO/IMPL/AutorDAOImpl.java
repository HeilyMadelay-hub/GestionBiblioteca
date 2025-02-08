
package DAO.IMPL;


import DAO.AutorDAO;
import Modelo.Autor;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AutorDAOImpl implements AutorDAO {
    
    private static final String SQL_INSERT = "INSERT INTO autores (nombre, nacionalidad) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE autores SET nombre = ?, nacionalidad = ? WHERE id_autor = ?";
    private static final String SQL_DELETE = "DELETE FROM autores WHERE id_autor = ?";
    private static final String SQL_SELECT = "SELECT * FROM autores WHERE id_autor = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM autores ORDER BY id_autor";
    private static final String SQL_SELECT_BY_NOMBRE = "SELECT * FROM autores WHERE nombre = ?";

    @Override
    public void insertar(Autor autor) throws Exception {
        // Verificar si ya existe un autor con ese nombre
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(SQL_SELECT_BY_NOMBRE)) {
            
            checkStmt.setString(1, autor.getNombre());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe un autor con el nombre: " + autor.getNombre());
                }
            }
        }

        // Si no existe, proceder con la inserción
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, autor.getNombre());
            stmt.setString(2, autor.getNacionalidad());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    autor.setIdAutor(rs.getInt(1));
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
                // Desactivar restricciones de clave foránea
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                
                // Truncar la tabla
                stmt.execute("TRUNCATE TABLE autores");
                
                // Reactivar restricciones de clave foránea
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
            throw new Exception("Error al eliminar todos los autores: " + e.getMessage());
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
    public void eliminar(Integer id) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // Desactivar restricciones de clave foránea
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

                // 1. Crear tabla temporal con los datos que queremos mantener (excepto el que vamos a eliminar)
                stmt.execute("CREATE TEMPORARY TABLE temp_autores SELECT * FROM autores WHERE id_autor != " + id);

                // 2. Truncar la tabla original
                stmt.execute("TRUNCATE TABLE autores");

                // 3. Reinsertamos los datos desde la temporal (ya sin el registro eliminado)
                stmt.execute("INSERT INTO autores (nombre, nacionalidad) " +
                           "SELECT nombre, nacionalidad FROM temp_autores");

                // 4. Limpieza
                stmt.execute("DROP TEMPORARY TABLE temp_autores");
                
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
            throw new Exception("Error al eliminar autor: " + e.getMessage());
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
    public Autor obtenerPorId(Integer id) throws Exception {
        Autor autor = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    autor = mapearResultSetAAutor(rs);
                }
            }
        }
        return autor;
    }

    @Override
    public List<Autor> obtenerTodos() throws Exception {
        List<Autor> autores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                autores.add(mapearResultSetAAutor(rs));
            }
        }
        return autores;
    }

    @Override
    public void actualizar(Autor autor) throws Exception {
        // Verificar si el nuevo nombre ya existe en otro autor
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT * FROM autores WHERE nombre = ? AND id_autor != ?")) {
            
            checkStmt.setString(1, autor.getNombre());
            checkStmt.setInt(2, autor.getIdAutor());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe otro autor con el nombre: " + autor.getNombre());
                }
            }
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, autor.getNombre());
            stmt.setString(2, autor.getNacionalidad());
            stmt.setInt(3, autor.getIdAutor());
            
            int filasActualizadas = stmt.executeUpdate();
            if (filasActualizadas == 0) {
                throw new Exception("No se encontró el autor con ID: " + autor.getIdAutor());
            }
        }
    }

    private Autor mapearResultSetAAutor(ResultSet rs) throws SQLException {
        Autor autor = new Autor();
        autor.setIdAutor(rs.getInt("id_autor"));
        autor.setNombre(rs.getString("nombre"));
        autor.setNacionalidad(rs.getString("nacionalidad"));
        return autor;
    }
}