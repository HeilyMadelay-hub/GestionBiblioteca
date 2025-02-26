package DAO;

import Modelo.Reserva;
import java.util.List;
import java.util.Date;

public interface ReservaDAO extends DAO<Reserva> {
    void eliminarTodos() throws Exception;
    
    // Búsquedas específicas
    List<Reserva> buscarPorUsuario(Integer idUsuario) throws Exception;
    List<Reserva> buscarPorLibro(Integer idLibro) throws Exception;
    List<Reserva> buscarPorEstado(Integer idEstado) throws Exception;
    List<Reserva> buscarPorFechas(Date fechaInicio, Date fechaFin) throws Exception;
    
    // Operaciones de estado
    void cambiarEstado(Integer idReserva, Integer nuevoEstado) throws Exception;
    List<Reserva> obtenerReservasVencidas() throws Exception;
    List<Reserva> obtenerReservasPendientes() throws Exception;
    
    // Validaciones
    boolean tieneReservaActiva(Integer idUsuario, Integer idLibro) throws Exception;
    int contarReservasActivas(Integer idUsuario) throws Exception;
}