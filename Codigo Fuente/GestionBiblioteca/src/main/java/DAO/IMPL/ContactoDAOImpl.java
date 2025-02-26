
package DAO.IMPL;


import DAO.ContactoDAO;
import Modelo.Contacto;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactoDAOImpl implements ContactoDAO {
    private static final String SQL_INSERT = "INSERT INTO contactos_usuario (id_usuario, id_tipo_contacto, valor, verificado) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE contactos_usuario SET id_usuario = ?, id_tipo_contacto = ?, valor = ?, verificado = ? WHERE id_contacto = ?";
    private static final String SQL_DELETE = "DELETE FROM contactos_usuario WHERE id_contacto = ?";
    private static final String SQL_SELECT = "SELECT * FROM contactos_usuario WHERE id_contacto = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM contactos_usuario ORDER BY id_contacto";
    private static final String SQL_SELECT_BY_VALOR = "SELECT * FROM contactos_usuario WHERE valor = ?";

    @Override
    public void insertar(Contacto contacto) throws Exception {
        // Primero verificamos si ya existe un contacto con el mismo tipo y valor
        String sqlCheck = "SELECT COUNT(*) FROM contactos_usuario WHERE id_tipo_contacto = ? AND valor = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
            
            checkStmt.setInt(1, contacto.getIdTipoContacto());
            checkStmt.setString(2, contacto.getValor());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("Ya existe un contacto con el mismo tipo y valor");
                }
            }
            
            // Si no existe, procedemos con la inserción
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, contacto.getIdUsuario());
                stmt.setInt(2, contacto.getIdTipoContacto());
                stmt.setString(3, contacto.getValor());
                stmt.setBoolean(4, contacto.getVerificado());
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        contacto.setIdContacto(rs.getInt(1));
                    }
                }
            }
        }
    }

    @Override
    public void actualizar(Contacto contacto) throws Exception {
        // Verificar si el nuevo valor+tipo ya existe para otro contacto
        String sqlCheck = "SELECT COUNT(*) FROM contactos_usuario WHERE id_tipo_contacto = ? AND valor = ? AND id_contacto != ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
            
            checkStmt.setInt(1, contacto.getIdTipoContacto());
            checkStmt.setString(2, contacto.getValor());
            checkStmt.setInt(3, contacto.getIdContacto());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("Ya existe otro contacto con el mismo tipo y valor");
                }
            }
            
            // Si no existe, procedemos con la actualización
            try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
                stmt.setInt(1, contacto.getIdUsuario());
                stmt.setInt(2, contacto.getIdTipoContacto());
                stmt.setString(3, contacto.getValor());
                stmt.setBoolean(4, contacto.getVerificado());
                stmt.setInt(5, contacto.getIdContacto());
                
                if (stmt.executeUpdate() == 0) {
                    throw new Exception("No se encontró el contacto con ID: " + contacto.getIdContacto());
                }
            }
        }
    }
    
    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setInt(1, id);
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró el contacto con ID: " + id);
            }
        }
    }

    @Override
    public void eliminarTodos() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE contactos_usuario");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Override
    public Contacto obtenerPorId(Integer id) throws Exception {
        Contacto contacto = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    contacto = mapearResultSetAContacto(rs);
                }
            }
        }
        return contacto;
    }

    @Override
    public List<Contacto> obtenerTodos() throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                contactos.add(mapearResultSetAContacto(rs));
            }
        }
        return contactos;
    }

    @Override
    public List<Contacto> buscarPorUsuario(Integer idUsuario) throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos_usuario WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contactos.add(mapearResultSetAContacto(rs));
                }
            }
        }
        return contactos;
    }

    @Override
    public List<Contacto> buscarPorTipo(Integer idTipoContacto) throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos_usuario WHERE id_tipo_contacto = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipoContacto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contactos.add(mapearResultSetAContacto(rs));
                }
            }
        }
        return contactos;
    }

    @Override
    public Contacto buscarPorValor(String valor) throws Exception {
        Contacto contacto = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_VALOR)) {
            
            stmt.setString(1, valor);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    contacto = mapearResultSetAContacto(rs);
                }
            }
        }
        return contacto;
    }

    @Override
    public void verificarContacto(Integer idContacto) throws Exception {
        String sql = "UPDATE contactos_usuario SET verificado = true WHERE id_contacto = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idContacto);
            
            if (stmt.executeUpdate() == 0) {
                throw new Exception("No se encontró el contacto con ID: " + idContacto);
            }
        }
    }

    @Override
    public List<Contacto> obtenerContactosVerificados() throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos_usuario WHERE verificado = true ORDER BY id_contacto";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contactos.add(mapearResultSetAContacto(rs));
            }
        }
        return contactos;
    }

    @Override
    public List<Contacto> obtenerContactosPendientes() throws Exception {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos_usuario WHERE verificado = false ORDER BY id_contacto";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contactos.add(mapearResultSetAContacto(rs));
            }
        }
        return contactos;
    }

    private Contacto mapearResultSetAContacto(ResultSet rs) throws SQLException {
        Contacto contacto = new Contacto();
        contacto.setIdContacto(rs.getInt("id_contacto"));
        contacto.setIdUsuario(rs.getInt("id_usuario"));
        contacto.setIdTipoContacto(rs.getInt("id_tipo_contacto"));
        contacto.setValor(rs.getString("valor"));
        contacto.setVerificado(rs.getBoolean("verificado"));
        return contacto;
    }
}