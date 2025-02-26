package Util;

import Modelo.Log;
import Servicio.LogServicio;
import java.sql.SQLException;
import org.json.JSONObject;

public class LogUtil {
    private static final LogServicio logServicio = new LogServicio();
    
    // Constantes para los tipos de log según la base de datos
    public static final int LOG_TIPO_USUARIO = 1;    // usuario
    public static final int LOG_TIPO_LIBRO = 2;      // libro
    public static final int LOG_TIPO_PRESTAMO = 3;   // prestamo
    public static final int LOG_TIPO_RESERVA = 4;    // reserva
    public static final int LOG_TIPO_SISTEMA = 5;    // sistema
    
    /**
     * Registra un log de usuario (login, logout, actualización de perfil, etc.)
     */
    public static void logUsuario(Integer idUsuario, String accion, JSONObject detalles) {
        try {
            logServicio.registrarLog(LOG_TIPO_USUARIO, idUsuario, accion, detalles.toString());
        } catch (SQLException e) {
            System.err.println("Error al registrar log de usuario: " + e.getMessage());
        }
    }
    
    /**
     * Registra un log de libro (creación, actualización, cambio de estado)
     */
    public static void logLibro(Integer idUsuario, String accion, JSONObject detalles) {
        try {
            logServicio.registrarLog(LOG_TIPO_LIBRO, idUsuario, accion, detalles.toString());
        } catch (SQLException e) {
            System.err.println("Error al registrar log de libro: " + e.getMessage());
        }
    }
    
    /**
     * Registra un log de préstamo
     */
    public static void logPrestamo(Integer idUsuario, String accion, JSONObject detalles) {
        try {
            logServicio.registrarLog(LOG_TIPO_PRESTAMO, idUsuario, accion, detalles.toString());
        } catch (SQLException e) {
            System.err.println("Error al registrar log de préstamo: " + e.getMessage());
        }
    }
    
    /**
     * Registra un log de reserva
     */
    public static void logReserva(Integer idUsuario, String accion, JSONObject detalles) {
        try {
            logServicio.registrarLog(LOG_TIPO_RESERVA, idUsuario, accion, detalles.toString());
        } catch (SQLException e) {
            System.err.println("Error al registrar log de reserva: " + e.getMessage());
        }
    }
    
    /**
     * Registra un log de sistema (puede ser null el idUsuario)
     */
    public static void logSistema(String accion, JSONObject detalles) {
        try {
            logServicio.registrarLog(LOG_TIPO_SISTEMA, null, accion, detalles.toString());
        } catch (SQLException e) {
            System.err.println("Error al registrar log de sistema: " + e.getMessage());
        }
    }
    
    /**
     * Crea detalles para log de inicio de sesión
     */
    public static JSONObject crearDetallesLogin(String ip, String navegador, String version) {
        JSONObject detalles = new JSONObject();
        detalles.put("ip", ip);
        detalles.put("navegador", navegador);
        detalles.put("version", version);
        return detalles;
    }
    
    /**
     * Crea detalles para log de préstamo
     */
    public static JSONObject crearDetallesPrestamo(Integer idLibro, String titulo, Integer diasPrestamo) {
        JSONObject detalles = new JSONObject();
        detalles.put("id_libro", idLibro);
        detalles.put("titulo", titulo);
        detalles.put("dias_prestamo", diasPrestamo);
        return detalles;
    }
    
    /**
     * Crea detalles para log de reserva
     */
    public static JSONObject crearDetallesReserva(Integer idLibro, String titulo, String fechaVencimiento) {
        JSONObject detalles = new JSONObject();
        detalles.put("id_libro", idLibro);
        detalles.put("titulo", titulo);
        detalles.put("fecha_vencimiento", fechaVencimiento);
        return detalles;
    }
    
    /**
     * Crea detalles para log de actualización de libro
     */
    public static JSONObject crearDetallesActualizacionLibro(String campo, String valorAnterior, String valorNuevo) {
        JSONObject detalles = new JSONObject();
        detalles.put("campo", campo);
        detalles.put("valor_anterior", valorAnterior);
        detalles.put("valor_nuevo", valorNuevo);
        return detalles;
    }
    
    /**
     * Crea detalles para log de error del sistema
     */
    public static JSONObject crearDetallesError(String tipo, String mensaje, String stackTrace) {
        JSONObject detalles = new JSONObject();
        detalles.put("tipo_error", tipo);
        detalles.put("mensaje", mensaje);
        detalles.put("stack_trace", stackTrace);
        return detalles;
    }
}