package DAO.IMPL;

import DAO.UsuarioDAO;
import Modelo.Usuario;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {
   
    private static final String SQL_INSERT = "INSERT INTO usuarios (nombre, password, id_rol, multa, activo) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE usuarios SET nombre = ?, password = ?, id_rol = ?, multa = ?, activo = ? WHERE id_usuario = ?";
    private static final String SQL_DELETE = "DELETE FROM usuarios WHERE id_usuario = ?";
    private static final String SQL_SELECT = "SELECT * FROM usuarios WHERE id_usuario = ? AND activo = true";
    private static final String SQL_SELECT_ALL = "SELECT * FROM usuarios WHERE activo = true ORDER BY id_usuario";
    private static final String SQL_SELECT_BY_NOMBRE = "SELECT * FROM usuarios WHERE nombre = ? AND activo = true";
    
    @Override
    public void eliminarTodos() throws Exception {
        DatabaseUtil.resetearTabla("usuarios");
    }

    @Override
    public void insertar(Usuario usuario) throws Exception {
        // Verificar si ya existe un usuario con ese nombre
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(SQL_SELECT_BY_NOMBRE)) {
            
            checkStmt.setString(1, usuario.getNombre());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Ya existe un usuario con el nombre: " + usuario.getNombre());
                }
            }
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            stmt.setInt(3, usuario.getIdRol());
            stmt.setDouble(4, usuario.getMulta());
            stmt.setBoolean(5, usuario.getActivo());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            stmt.setInt(3, usuario.getIdRol());
            stmt.setDouble(4, usuario.getMulta());
            stmt.setBoolean(5, usuario.getActivo());
            stmt.setInt(6, usuario.getIdUsuario());
            
            stmt.executeUpdate();
        }
    }

    
    public void eliminar(Integer id) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // Desactivar restricciones de clave foránea
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

                // 1. Crear tabla temporal con los datos que queremos mantener (excepto el que vamos a eliminar)
                stmt.execute("CREATE TEMPORARY TABLE temp_usuarios SELECT * FROM usuarios WHERE id_usuario != " + id);

                // 2. Truncar la tabla original
                stmt.execute("TRUNCATE TABLE usuarios");

                // 3. Reinsertamos los datos desde la temporal (ya sin el registro eliminado)
                stmt.execute("INSERT INTO usuarios (nombre, password, id_rol, multa, activo) " +
                           "SELECT nombre, password, id_rol, multa, activo FROM temp_usuarios");

                // 4. Limpieza
                stmt.execute("DROP TEMPORARY TABLE temp_usuarios");
                
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
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
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
    public Usuario obtenerPorId(Integer id) throws Exception {
        Usuario usuario = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearResultSetAUsuario(rs);
                }
            }
        }
        return usuario;
    }

    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                usuarios.add(mapearResultSetAUsuario(rs));
            }
        }
        return usuarios;
    }

    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setPassword(rs.getString("password"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setMulta(rs.getDouble("multa"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}