
package Servicio;

import DAO.PrestamoDAO;
import Modelo.Libro;
import Modelo.Prestamo;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PrestamoServicio {

    private PrestamoDAO prestamoDAO;

    /**
     * Constructor que inyecta la dependencia del DAO asociado a los préstamos.
     * @param prestamoDAO El DAO para acceder a los datos de los préstamos.
     */
    public PrestamoServicio(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    /**
     * Registra un nuevo préstamo en el sistema.
     * @param prestamo El préstamo a registrar.
     * @return El préstamo registrado, con el ID asignado.
     * @throws Exception Si ocurre un error durante el registro o si la validación falla.
     */
    public Prestamo registrarPrestamo(Prestamo prestamo) throws Exception {
        // Validar que la fecha de devolución esperada sea posterior a la fecha de préstamo.
        if (prestamo.getFechaDevolucionEsperada() == null || !prestamo.getFechaDevolucionEsperada().after(prestamo.getFechaPrestamo())) {
            throw new Exception("La fecha de devolución esperada debe ser posterior a la fecha de préstamo.");
        }
        // Otras validaciones podrían incluir verificar la existencia del usuario y del libro, entre otros.
        prestamoDAO.insertar(prestamo);
        return prestamo;
    }

    /**
     * Registra la devolución de un préstamo.
     * @param idPrestamo El ID del préstamo a devolver.
     * @param fechaDevolucion La fecha en que se realizó la devolución.
     * @throws Exception Si ocurre un error durante la actualización del préstamo.
     */
    public void registrarDevolucion(Integer idPrestamo, Date fechaDevolucion) throws Exception {
        prestamoDAO.registrarDevolucion(idPrestamo, fechaDevolucion);
    }

    /**
     * Recupera un préstamo por su ID.
     * @param idPrestamo El ID del préstamo.
     * @return El préstamo encontrado o null si no existe.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public Prestamo obtenerPrestamoPorId(Integer idPrestamo) throws Exception {
        return prestamoDAO.obtenerPorId(idPrestamo);
    }

    /**
     * Obtiene todos los préstamos activos (aquellos sin fecha de devolución registrada).
     * @return Lista de préstamos activos.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public List<Prestamo> obtenerPrestamosActivos() throws Exception {
        return prestamoDAO.obtenerPrestamosActivos();
    }

    /**
     * Obtiene todos los préstamos registrados en el sistema.
     * @return Lista con todos los préstamos.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public List<Prestamo> obtenerTodosPrestamos() throws Exception {
        return prestamoDAO.obtenerTodos();
    }
    
    /**
    * Obtiene los libros más prestados con su frecuencia de préstamo.
    * @param limite Cantidad máxima de libros a recuperar.
    * @return Una lista de objetos con el libro y la cantidad de préstamos.
    * @throws Exception Si ocurre un error durante la consulta.
    */
   public List<Map.Entry<Libro, Integer>> obtenerLibrosMasPrestados(int limite) throws Exception {
       return prestamoDAO.obtenerLibrosMasPrestados(limite);
   }

    
   /**
 * Obtiene todos los préstamos asociados a un usuario específico.
 * @param idUsuario El ID del usuario cuyos préstamos se desean consultar.
 * @return Lista de préstamos del usuario.
 * @throws Exception Si ocurre un error durante la consulta.
 */
public List<Prestamo> obtenerPrestamosPorUsuario(Integer idUsuario) throws Exception {
    if (idUsuario == null) {
        throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
    }
    
    return prestamoDAO.buscarPorUsuario(idUsuario);
}

/**
 * Obtiene todos los préstamos asociados a un libro específico.
 * @param idLibro El ID del libro cuyos préstamos se desean consultar.
 * @return Lista de préstamos del libro.
 * @throws Exception Si ocurre un error durante la consulta.
 */
public List<Prestamo> obtenerPrestamosPorLibro(Integer idLibro) throws Exception {
    if (idLibro == null) {
        throw new IllegalArgumentException("El ID del libro no puede ser nulo.");
    }
    
    return prestamoDAO.buscarPorLibro(idLibro);
}
    
    
    
}
