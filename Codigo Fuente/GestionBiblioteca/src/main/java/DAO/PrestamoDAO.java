package DAO;

import Modelo.Libro;
import Modelo.Prestamo;
import java.util.List;
import java.util.Date;
import java.util.Map;

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
    
    /**
    * Obtiene un mapa con la cantidad de préstamos por libro, ordenado de mayor a menor frecuencia.
    * @return Un mapa donde la clave es el ID del libro y el valor es la cantidad de préstamos.
    * @throws Exception Si ocurre un error durante la consulta.
    */
   Map<Integer, Integer> obtenerFrecuenciaPrestamosLibros() throws Exception;

   /**
    * Obtiene los libros más prestados con su frecuencia de préstamo.
    * @param limite Cantidad máxima de libros a recuperar.
    * @return Una lista de objetos con el libro y la cantidad de préstamos.
    * @throws Exception Si ocurre un error durante la consulta.
    */
   List<Map.Entry<Libro, Integer>> obtenerLibrosMasPrestados(int limite) throws Exception;
}
