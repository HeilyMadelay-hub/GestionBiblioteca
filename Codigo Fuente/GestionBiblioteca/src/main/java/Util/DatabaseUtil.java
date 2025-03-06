package Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

public class DatabaseUtil {
    private static Properties properties = new Properties();
    
    static {
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo de configuración", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        
        return DriverManager.getConnection(url, user, password);
    }

    public static void resetearTabla(String tableName) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Desactivar restricciones de clave foránea temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            // Truncar la tabla para reiniciar completamente
            stmt.execute("TRUNCATE TABLE " + tableName);
            // Reactivar restricciones de clave foránea
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
    
    public static void reordenarIds(String tableName, String idColumnName) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Crear tabla temporal
            stmt.execute("CREATE TEMPORARY TABLE temp_" + tableName + " LIKE " + tableName);
            
            // Copiar datos ordenados a la tabla temporal
            stmt.execute("INSERT INTO temp_" + tableName + 
                        " SELECT * FROM " + tableName + 
                        " WHERE activo = true ORDER BY " + idColumnName);
            
            // Limpiar tabla original
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE " + tableName);
            
            // Reinsertar datos con nuevos IDs
            stmt.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
            stmt.execute("INSERT INTO " + tableName + 
                        " SELECT * FROM temp_" + tableName);
            
            // Limpiar
            stmt.execute("DROP TEMPORARY TABLE IF EXISTS temp_" + tableName);
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }}
        
        public static boolean probarConexion() {
            try (Connection conn = getConnection()) {
                return conn != null && !conn.isClosed();
            } catch (SQLException e) {
                System.err.println("Error al conectar a la base de datos: " + e.getMessage());
                return false;
            }
        }
 
}