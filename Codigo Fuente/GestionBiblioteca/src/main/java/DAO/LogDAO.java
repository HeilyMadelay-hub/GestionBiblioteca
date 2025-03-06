package DAO;

import Modelo.Log;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface LogDAO {
    void insertar(Log log) throws SQLException;
    Log obtenerPorId(Integer idLog) throws SQLException;
    List<Log> obtenerTodos() throws SQLException;
    List<Log> obtenerPorTipo(Integer idTipoLog) throws SQLException;
    List<Log> obtenerPorUsuario(Integer idUsuario) throws SQLException;
    List<Log> obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException;
    void eliminar(Integer idLog) throws SQLException;
    
    /**
    * Obtiene los logs para un usuario específico.
    * @param idUsuario ID del usuario.
    * @return Lista de logs del usuario.
    * @throws SQLException Si ocurre un error durante la consulta.
    */
   List<Log> obtenerLogsPorUsuarioEnRango(Integer idUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException;
}
