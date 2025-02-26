package DAO;

import Modelo.Prestamo;
import java.util.List;
import java.util.Date;

public interface PrestamoDAO extends DAO<Prestamo> {
    void eliminarTodos() throws Exception;
    
    // Métodos específicos para préstamos
    void registrarDevolucion(Integer idPrestamo, Date fechaDevolucion) throws Exception;
    List<Prestamo> obtenerPrestamosActivos() throws Exception;
    List<Prestamo> obtenerPrestamosVencidos() throws Exception;
    List<Prestamo> obtenerPrestamosExpress() throws Exception;
    
    // Búsquedas específicas
    List<Prestamo> buscarPorUsuario(Integer idUsuario) throws Exception;
    List<Prestamo> buscarPorLibro(Integer idLibro) throws Exception;
    List<Prestamo> buscarPorTipo(Integer idTipoPrestamo) throws Exception;
    List<Prestamo> buscarPorFechas(Date fechaInicio, Date fechaFin) throws Exception;
    
    // Validaciones
    boolean tienePrestamoActivo(Integer idUsuario, Integer idLibro) throws Exception;
    int contarPrestamosActivos(Integer idUsuario) throws Exception;
    boolean esPrestamoExpress(Integer idPrestamo) throws Exception;
}
