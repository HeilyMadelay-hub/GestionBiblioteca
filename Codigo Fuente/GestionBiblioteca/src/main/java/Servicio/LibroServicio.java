
package Servicio;
import DAO.AutorDAO;
import DAO.CategoriaDAO;
import DAO.GeneroDAO;
import DAO.LibroDAO;
import DAO.LogDAO;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Modelo.Autor;
import Modelo.Categoria;
import Modelo.Genero;
import Modelo.Libro;
import Modelo.Log;
import Observer.LibroNotificador;
import org.json.JSONObject;

/**
 * Servicio que gestiona la lógica de negocio para los libros.
 * Coordina operaciones entre DAOs, validaciones y patrón observer.
 */
public class LibroServicio {
    
    private final LibroDAO libroDAO;
    private final AutorDAO autorDAO;
    private final GeneroDAO generoDAO;
    private final CategoriaDAO categoriaDAO;
    private final LogDAO logDAO;
    
    // Mapa para mantener notificadores de libros
    private final Map<Integer, LibroNotificador> notificadores = new HashMap<>();
    
    /**
     * Constructor con inyección de dependencias.
     */
    public LibroServicio(LibroDAO libroDAO, AutorDAO autorDAO, 
                        GeneroDAO generoDAO, CategoriaDAO categoriaDAO,
                        LogDAO logDAO) {
        this.libroDAO = libroDAO;
        this.autorDAO = autorDAO;
        this.generoDAO = generoDAO;
        this.categoriaDAO = categoriaDAO;
        this.logDAO = logDAO;
    }
    
    /**
     * Registra un nuevo libro con sus relaciones.
     * 
     * @param libro El libro a registrar
     * @param idUsuario ID del usuario que realiza la operación
     * @return El libro registrado con su ID
     * @throws Exception Si ocurre un error durante el registro
     */
    public Libro registrarLibro(Libro libro, Integer idUsuario) throws Exception {
        validarLibro(libro);
        
        // Registrar el libro en la base de datos
        libroDAO.insertar(libro);
        
        // Registrar relaciones con autores, géneros y categorías
        for (Autor autor : libro.getAutores()) {
            libroDAO.agregarAutor(libro.getIdLibro(), autor.getIdAutor());
        }
        
        for (Genero genero : libro.getGeneros()) {
            libroDAO.agregarGenero(libro.getIdLibro(), genero.getIdGenero());
        }
        
        for (Categoria categoria : libro.getCategorias()) {
            libroDAO.agregarCategoria(libro.getIdLibro(), categoria.getIdCategoria());
        }
        
        // Crear notificador para este libro
        LibroNotificador notificador = new LibroNotificador(libro);
        notificadores.put(libro.getIdLibro(), notificador);
        
        // Registrar en log
        registrarLog(idUsuario, "Registro de libro", libro);
        
        return libro;
    }
    
    /**
     * Actualiza la información de un libro existente.
     * 
     * @param libro El libro con los datos actualizados
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la actualización
     */
    public void actualizarLibro(Libro libro, Integer idUsuario) throws Exception {
        validarLibro(libro);
        
        // Obtener el libro actual para comparar cambios
        Libro libroActual = libroDAO.obtenerPorId(libro.getIdLibro());
        if (libroActual == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Actualizar el libro
        libroDAO.actualizar(libro);
        
        // Actualizar notificador si existe
        if (notificadores.containsKey(libro.getIdLibro())) {
            notificadores.get(libro.getIdLibro()).setLibro(libro);
        } else {
            notificadores.put(libro.getIdLibro(), new LibroNotificador(libro));
        }
        
        // Registrar en log
        registrarLog(idUsuario, "Actualización de libro", libro);
    }
    
    /**
     * Elimina un libro si no está prestado.
     * 
     * @param idLibro ID del libro a eliminar
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si el libro está prestado o no existe
     */
    public void eliminarLibro(Integer idLibro, Integer idUsuario) throws Exception {
        Libro libro = libroDAO.obtenerPorId(idLibro);
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Verificar que el libro no esté prestado (estado 2 = prestado)
        if (libro.getIdEstado() == 2) {
            throw new Exception("No se puede eliminar un libro que está prestado.");
        }
        
        // Eliminar libro
        libroDAO.eliminar(idLibro);
        
        // Eliminar notificador
        notificadores.remove(idLibro);
        
        // Registrar en log
        registrarLog(idUsuario, "Eliminación de libro", libro);
    }
    
    /**
     * Actualiza el estado de un libro y notifica si está disponible.
     * 
     * @param idLibro ID del libro
     * @param idEstado Nuevo estado del libro
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la actualización
     */
    public void actualizarEstadoLibro(Integer idLibro, Integer idEstado, Integer idUsuario) throws Exception {
        Libro libro = libroDAO.obtenerPorId(idLibro);
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        libro.setIdEstado(idEstado);
        libroDAO.actualizar(libro);
        
        // Si el libro pasa a estar disponible (estado 1), notificar a los observadores
        if (idEstado == 1) { // 1 = disponible
            LibroNotificador notificador = notificadores.get(idLibro);
            if (notificador != null) {
                notificador.setDisponible(true);
            }
        }
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idLibro", idLibro);
        detalles.put("nuevoEstado", idEstado);
        
        Log log = new Log();
        log.setIdTipoLog(2); // 2 = libro
        log.setIdUsuario(idUsuario);
        log.setAccion("Cambio de estado de libro");
        log.setDetalles(detalles.toString());
        // No necesitamos establecer la fecha, ya que el constructor de Log lo hace automáticamente
        
        logDAO.insertar(log);
    }
    
    /**
     * Obtiene el notificador para un libro específico.
     * 
     * @param idLibro ID del libro
     * @return El notificador del libro o null si no existe
     */
    public LibroNotificador getNotificador(Integer idLibro) {
        return notificadores.get(idLibro);
    }
    
    /**
     * Busca libros según diversos criterios.
     * 
     * @param titulo Título o parte del título (opcional)
     * @param idAutor ID del autor (opcional)
     * @param idGenero ID del género (opcional)
     * @param idCategoria ID de la categoría (opcional)
     * @return Lista de libros que coinciden con los criterios
     * @throws Exception Si ocurre un error durante la búsqueda
     */
    public List<Libro> buscarLibros(String titulo, Integer idAutor, Integer idGenero, Integer idCategoria) throws Exception {
        List<Libro> resultados;
        
        if (titulo != null && !titulo.isEmpty()) {
            resultados = libroDAO.buscarPorTitulo(titulo);
        } else if (idAutor != null) {
            resultados = libroDAO.buscarPorAutor(idAutor);
        } else if (idGenero != null) {
            resultados = libroDAO.buscarPorGenero(idGenero);
        } else if (idCategoria != null) {
            resultados = libroDAO.buscarPorCategoria(idCategoria);
        } else {
            resultados = libroDAO.obtenerTodos();
        }
        
        return resultados;
    }
    
    /**
     * Obtiene un libro por su ID.
     * 
     * @param idLibro ID del libro
     * @return El libro encontrado o null si no existe
     * @throws Exception Si ocurre un error durante la búsqueda
     */
    public Libro obtenerLibroPorId(Integer idLibro) throws Exception {
        return libroDAO.obtenerPorId(idLibro);
    }
    
    /**
     * Obtiene todos los libros.
     * 
     * @return Lista con todos los libros
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Libro> obtenerTodosLibros() throws Exception {
        return libroDAO.obtenerTodos();
    }
    
    /**
     * Busca un libro por ISBN.
     * 
     * @param isbn ISBN del libro
     * @return El libro encontrado o null si no existe
     * @throws Exception Si ocurre un error durante la búsqueda
     */
    public Libro buscarPorIsbn(String isbn) throws Exception {
        return libroDAO.buscarPorIsbn(isbn);
    }
    
    /**
     * Obtiene la lista de libros de un autor específico.
     * 
     * @param idAutor ID del autor
     * @return Lista de libros del autor
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Libro> obtenerLibrosPorAutor(Integer idAutor) throws Exception {
        return libroDAO.buscarPorAutor(idAutor);
    }
    
    /**
     * Obtiene la lista de libros de un género específico.
     * 
     * @param idGenero ID del género
     * @return Lista de libros del género
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Libro> obtenerLibrosPorGenero(Integer idGenero) throws Exception {
        return libroDAO.buscarPorGenero(idGenero);
    }
    
    /**
     * Obtiene la lista de libros de una categoría específica.
     * 
     * @param idCategoria ID de la categoría
     * @return Lista de libros de la categoría
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Libro> obtenerLibrosPorCategoria(Integer idCategoria) throws Exception {
        return libroDAO.buscarPorCategoria(idCategoria);
    }
    
    /**
     * Valida que el libro cumpla con las reglas de negocio.
     * 
     * @param libro El libro a validar
     * @throws Exception Si el libro no cumple con las validaciones
     */
    private void validarLibro(Libro libro) throws Exception {
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new Exception("El título del libro es obligatorio.");
        }
        
        if (libro.getIdEstado() == null) {
            throw new Exception("El estado del libro es obligatorio.");
        }
        
        if (libro.getFechaAdquisicion() == null) {
            throw new Exception("La fecha de adquisición es obligatoria.");
        }
        
        if (libro.getAutores().isEmpty()) {
            throw new Exception("El libro debe tener al menos un autor.");
        }
    }
    
    /**
     * Añade un autor a un libro.
     * 
     * @param idLibro ID del libro
     * @param idAutor ID del autor
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la operación
     */
    public void agregarAutor(Integer idLibro, Integer idAutor, Integer idUsuario) throws Exception {
        // Verificar que el libro exista
        Libro libro = libroDAO.obtenerPorId(idLibro);
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Verificar que el autor exista
        Autor autor = autorDAO.obtenerPorId(idAutor);
        if (autor == null) {
            throw new Exception("El autor no existe en la base de datos.");
        }
        
        // Agregar autor al libro
        libroDAO.agregarAutor(idLibro, idAutor);
        
        // Actualizar el libro en memoria
        libro.getAutores().add(autor);
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idLibro", idLibro);
        detalles.put("idAutor", idAutor);
        detalles.put("nombreAutor", autor.getNombre());
        
        Log log = new Log();
        log.setIdTipoLog(2); // 2 = libro
        log.setIdUsuario(idUsuario);
        log.setAccion("Agregar autor a libro");
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
    
    /**
     * Añade un género a un libro.
     * 
     * @param idLibro ID del libro
     * @param idGenero ID del género
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la operación
     */
    public void agregarGenero(Integer idLibro, Integer idGenero, Integer idUsuario) throws Exception {
        // Verificar que el libro exista
        Libro libro = libroDAO.obtenerPorId(idLibro);
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Verificar que el género exista
        Genero genero = generoDAO.obtenerPorId(idGenero);
        if (genero == null) {
            throw new Exception("El género no existe en la base de datos.");
        }
        
        // Agregar género al libro
        libroDAO.agregarGenero(idLibro, idGenero);
        
        // Actualizar el libro en memoria
        libro.getGeneros().add(genero);
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idLibro", idLibro);
        detalles.put("idGenero", idGenero);
        detalles.put("nombreGenero", genero.getNombre());
        
        Log log = new Log();
        log.setIdTipoLog(2); // 2 = libro
        log.setIdUsuario(idUsuario);
        log.setAccion("Agregar género a libro");
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
    
    /**
     * Añade una categoría a un libro.
     * 
     * @param idLibro ID del libro
     * @param idCategoria ID de la categoría
     * @param idUsuario ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la operación
     */
    public void agregarCategoria(Integer idLibro, Integer idCategoria, Integer idUsuario) throws Exception {
        // Verificar que el libro exista
        Libro libro = libroDAO.obtenerPorId(idLibro);
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Verificar que la categoría exista
        Categoria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null) {
            throw new Exception("La categoría no existe en la base de datos.");
        }
        
        // Agregar categoría al libro
        libroDAO.agregarCategoria(idLibro, idCategoria);
        
        // Actualizar el libro en memoria
        libro.getCategorias().add(categoria);
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idLibro", idLibro);
        detalles.put("idCategoria", idCategoria);
        detalles.put("nombreCategoria", categoria.getNombre());
        
        Log log = new Log();
        log.setIdTipoLog(2); // 2 = libro
        log.setIdUsuario(idUsuario);
        log.setAccion("Agregar categoría a libro");
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
    
    /**
     * Registra una operación en el log.
     * 
     * @param idUsuario ID del usuario que realiza la operación
     * @param accion Descripción de la acción
     * @param libro El libro involucrado
     * @throws Exception Si ocurre un error al registrar el log
     */
    private void registrarLog(Integer idUsuario, String accion, Libro libro) throws Exception {
        JSONObject detalles = new JSONObject();
        detalles.put("idLibro", libro.getIdLibro());
        detalles.put("titulo", libro.getTitulo());
        detalles.put("isbn", libro.getIsbn());
        detalles.put("estado", libro.getIdEstado());
        
        Log log = new Log();
        log.setIdTipoLog(2); // 2 = libro
        log.setIdUsuario(idUsuario);
        log.setAccion(accion);
        log.setDetalles(detalles.toString());
        // La fecha se establece automáticamente en el constructor de Log
        
        logDAO.insertar(log);
    }
    
    /**
    * Busca libros por título o parte del título.
    * 
    * @param titulo Título o parte del título a buscar
    * @return Lista de libros que coinciden con el criterio de búsqueda
    * @throws Exception Si ocurre un error durante la búsqueda
    */
   public List<Libro> buscarPorTitulo(String titulo) throws Exception {
       if (titulo == null || titulo.trim().isEmpty()) {
           throw new Exception("El título de búsqueda no puede estar vacío.");
       }
       return libroDAO.buscarPorTitulo(titulo);
   }
}