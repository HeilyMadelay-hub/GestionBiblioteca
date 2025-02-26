
package DAO.IMPL;

import DAO.CategoriaDAO;
import Modelo.Categoria;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {
    private static final String SQL_INSERT = "INSERT INTO categorias (nombre) VALUES (?)";
    private static final String SQL_UPDATE = "UPDATE categorias SET nombre = ? WHERE id_categoria = ?";
    private static final String SQL_DELETE = "DELETE FROM categorias WHERE id_categoria = ?";
    private static final String SQL_SELECT = "SELECT * FROM categorias WHERE id_categoria = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM categorias ORDER BY id_categoria";
    private static final String SQL_SELECT_BY_NOMBRE = "SELECT * FROM categorias WHERE nombre = ?";

    @Override
    public void insertar(Categoria categoria) throws Exception {
        // Verificar si ya existe una categoría con ese nombre
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(SQL_SELECT_BY_NOMBRE)) {
            
            checkStmt.setString(1, categoria.getNombre());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe una categoría con el nombre: " + categoria.getNombre());
                }
            }
        }

        // Si no existe, proceder con la inserción
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNombre());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
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
                stmt.execute("CREATE TEMPORARY TABLE temp_categorias SELECT * FROM categorias WHERE id_categoria != " + id);

                // 2. Truncar la tabla original
                stmt.execute("TRUNCATE TABLE categorias");

                // 3. Reinsertamos los datos desde la temporal
                stmt.execute("INSERT INTO categorias (nombre) SELECT nombre FROM temp_categorias");

                // 4. Limpieza
                stmt.execute("DROP TEMPORARY TABLE temp_categorias");
                
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
            throw new Exception("Error al eliminar categoría: " + e.getMessage());
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
                stmt.execute("TRUNCATE TABLE categorias");
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
            throw new Exception("Error al eliminar todas las categorías: " + e.getMessage());
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
    public Categoria obtenerPorId(Integer id) throws Exception {
        Categoria categoria = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoria = mapearResultSetACategoria(rs);
                }
            }
        }
        return categoria;
    }

    @Override
    public List<Categoria> obtenerTodos() throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                categorias.add(mapearResultSetACategoria(rs));
            }
        }
        return categorias;
    }

    @Override
    public void actualizar(Categoria categoria) throws Exception {
        // Verificar si el nuevo nombre ya existe en otra categoría
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT * FROM categorias WHERE nombre = ? AND id_categoria != ?")) {
            
            checkStmt.setString(1, categoria.getNombre());
            checkStmt.setInt(2, categoria.getIdCategoria());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe otra categoría con el nombre: " + categoria.getNombre());
                }
            }
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setInt(2, categoria.getIdCategoria());
            
            int filasActualizadas = stmt.executeUpdate();
            if (filasActualizadas == 0) {
                throw new Exception("No se encontró la categoría con ID: " + categoria.getIdCategoria());
            }
        }
    }

    private Categoria mapearResultSetACategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        return categoria;
    }
}