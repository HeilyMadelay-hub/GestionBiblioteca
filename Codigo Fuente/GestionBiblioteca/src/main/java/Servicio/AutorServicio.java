
package Servicio;

import DAO.AutorDAO;
import DAO.LogDAO;
import Modelo.Autor;
import Modelo.Log;
import java.util.List;
import org.json.JSONObject;

/**
 * Servicio para gestionar las operaciones relacionadas con los autores.
 */
public class AutorServicio {
    
    private final AutorDAO autorDAO;
    private final LogDAO logDAO;
    
    /**
     * Constructor con inyección de dependencias.
     * 
     * @param autorDAO DAO para acceder a los datos de autores
     * @param logDAO DAO para registrar logs
     */
    public AutorServicio(AutorDAO autorDAO, LogDAO logDAO) {
        this.autorDAO = autorDAO;
        this.logDAO = logDAO;
    }
    
    /**
     * Registra un nuevo autor en el sistema.
     * 
     * @param autor Autor a registrar
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @return El autor registrado con su ID asignado
     * @throws Exception Si ocurre un error durante el registro
     */
    public Autor registrarAutor(Autor autor, Integer idUsuarioOperador) throws Exception {
        // Validar datos del autor
        validarDatosAutor(autor);
        
        // Insertar el autor en la base de datos
        autorDAO.insertar(autor);
        
        // Registrar en log
        registrarLog(idUsuarioOperador, "Registro de autor", autor);
        
        return autor;
    }
    
    /**
     * Actualiza los datos de un autor existente.
     * 
     * @param autor Autor con los datos actualizados
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la actualización
     */
    public void actualizarAutor(Autor autor, Integer idUsuarioOperador) throws Exception {
        // Validar datos del autor
        validarDatosAutor(autor);
        
        // Verificar que el autor existe
        Autor autorExistente = autorDAO.obtenerPorId(autor.getIdAutor());
        if (autorExistente == null) {
            throw new Exception("El autor no existe en la base de datos.");
        }
        
        // Actualizar el autor
        autorDAO.actualizar(autor);
        
        // Registrar en log
        registrarLog(idUsuarioOperador, "Actualización de autor", autor);
    }
    
    /**
     * Elimina un autor del sistema.
     * 
     * @param idAutor ID del autor a eliminar
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la eliminación
     */
    public void eliminarAutor(Integer idAutor, Integer idUsuarioOperador) throws Exception {
        // Verificar que el autor existe
        Autor autor = autorDAO.obtenerPorId(idAutor);
        if (autor == null) {
            throw new Exception("El autor no existe en la base de datos.");
        }
        
        // Eliminar el autor
        autorDAO.eliminar(idAutor);
        
        // Registrar en log
        registrarLog(idUsuarioOperador, "Eliminación de autor", autor);
    }
    
    /**
     * Obtiene un autor por su ID.
     * 
     * @param idAutor ID del autor
     * @return El autor encontrado o null si no existe
     * @throws Exception Si ocurre un error durante la consulta
     */
    public Autor obtenerAutorPorId(Integer idAutor) throws Exception {
        return autorDAO.obtenerPorId(idAutor);
    }
    
    /**
     * Obtiene todos los autores registrados.
     * 
     * @return Lista con todos los autores
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Autor> obtenerTodosAutores() throws Exception {
        return autorDAO.obtenerTodos();
    }
    
    /**
     * Valida los datos del autor.
     * 
     * @param autor Autor a validar
     * @throws Exception Si los datos no son válidos
     */
    private void validarDatosAutor(Autor autor) throws Exception {
        if (autor.getNombre() == null || autor.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del autor es obligatorio.");
        }
        
        if (autor.getNombre().length() > 100) {
            throw new Exception("El nombre del autor no puede exceder los 100 caracteres.");
        }
        
        if (autor.getNacionalidad() != null && autor.getNacionalidad().length() > 50) {
            throw new Exception("La nacionalidad no puede exceder los 50 caracteres.");
        }
    }
    
    /**
     * Registra una operación en el log.
     * 
     * @param idUsuario ID del usuario que realiza la operación
     * @param accion Descripción de la acción
     * @param autor Autor involucrado en la operación
     * @throws Exception Si ocurre un error al registrar el log
     */
    private void registrarLog(Integer idUsuario, String accion, Autor autor) throws Exception {
        JSONObject detalles = new JSONObject();
        detalles.put("idAutor", autor.getIdAutor());
        detalles.put("nombre", autor.getNombre());
        detalles.put("nacionalidad", autor.getNacionalidad());
        
        Log log = new Log();
        log.setIdTipoLog(5); // 5 = operación de autor (asumiendo este tipo)
        log.setIdUsuario(idUsuario);
        log.setAccion(accion);
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
}