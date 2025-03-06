package Servicio;

import DAO.IMPL.LogDAOImpl;
import DAO.LogDAO;
import Modelo.Log;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class LogServicio {
    private final LogDAO logDAO;
    
    public LogServicio() {
        this.logDAO = new LogDAOImpl();
    }
    
    public void registrarLog(Integer idTipoLog, Integer idUsuario, String accion, String detalles) throws SQLException {
        Log log = new Log(idTipoLog, idUsuario, accion, detalles);
        logDAO.insertar(log);
    }
    
    public Log obtenerLog(Integer idLog) throws SQLException {
        return logDAO.obtenerPorId(idLog);
    }
    
    public List<Log> obtenerTodosLogs() throws SQLException {
        return logDAO.obtenerTodos();
    }
    
    public List<Log> obtenerLogsPorTipo(Integer idTipoLog) throws SQLException {
        return logDAO.obtenerPorTipo(idTipoLog);
    }
    
    public List<Log> obtenerLogsPorUsuario(Integer idUsuario) throws SQLException {
        return logDAO.obtenerPorUsuario(idUsuario);
    }
    
    public List<Log> obtenerLogsPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
        return logDAO.obtenerPorFecha(fechaInicio, fechaFin);
    }
    
    public void eliminarLog(Integer idLog) throws SQLException {
        logDAO.eliminar(idLog);
    }
    
    
    /**
 * Obtiene los logs de actividad de un usuario en un rango de fechas.
 * @param idUsuario ID del usuario.
 * @param fechaInicio Fecha de inicio del rango.
 * @param fechaFin Fecha de fin del rango.
 * @return Lista de logs del usuario en el rango de fechas.
 * @throws SQLException Si ocurre un error durante la consulta.
 */
public List<Log> obtenerLogsPorUsuarioEnRango(Integer idUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
    return logDAO.obtenerLogsPorUsuarioEnRango(idUsuario, fechaInicio, fechaFin);
}

/**
 * Obtiene un resumen de actividad de usuarios en un rango de fechas.
 * @param fechaInicio Fecha de inicio del rango.
 * @param fechaFin Fecha de fin del rango.
 * @return Un mapa donde la clave es el ID del usuario y el valor es la cantidad de acciones realizadas.
 * @throws SQLException Si ocurre un error durante la consulta.
 */
public Map<Integer, Integer> obtenerResumenActividadUsuarios(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
    Map<Integer, Integer> actividad = new HashMap<>();
    
    List<Log> logs = logDAO.obtenerPorFecha(fechaInicio, fechaFin);
    
    for (Log log : logs) {
        if (log.getIdUsuario() != null) {
            Integer idUsuario = log.getIdUsuario();
            actividad.put(idUsuario, actividad.getOrDefault(idUsuario, 0) + 1);
        }
    }
    
    return actividad;
}
}