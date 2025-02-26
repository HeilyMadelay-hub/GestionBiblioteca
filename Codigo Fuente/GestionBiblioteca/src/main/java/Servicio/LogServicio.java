

package Servicio;

import DAO.IMPL.LogDAOImpl;
import DAO.LogDAO;
import Modelo.Log;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

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
}