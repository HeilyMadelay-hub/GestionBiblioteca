package Servicio;

import DAO.LibroDAO;
import DAO.LogDAO;
import DAO.ReservaDAO;
import DAO.UsuarioDAO;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import Modelo.Libro;
import Modelo.Log;
import Modelo.Reserva;
import Modelo.Usuario;
import Observer.LibroNotificador;
import Observer.UsuarioNotificacion;
import Util.DatabaseUtil;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// Otros imports necesarios

/**
 * Servicio que gestiona la lógica de negocio para las reservas.
 * Coordina operaciones entre DAOs, validaciones, y el patrón Observer.
 */
public class ReservaServicio {
    
    private final ReservaDAO reservaDAO;
    private final UsuarioDAO usuarioDAO;
    private final LibroDAO libroDAO;
    private final LogDAO logDAO;
    private final LibroServicio libroServicio;
    
    /**
     * Constructor con inyección de dependencias.
     */
    public ReservaServicio(ReservaDAO reservaDAO, UsuarioDAO usuarioDAO, 
                          LibroDAO libroDAO, LogDAO logDAO, LibroServicio libroServicio) {
        this.reservaDAO = reservaDAO;
        this.usuarioDAO = usuarioDAO;
        this.libroDAO = libroDAO;
        this.logDAO = logDAO;
        this.libroServicio = libroServicio;
    }
    
/**
 * Actualiza las reservas activas de un usuario para un libro específico marcándolas como completadas
 * 
 * @param idUsuario El ID del usuario
 * @param idLibro El ID del libro
 * @return Número de reservas actualizadas
 * @throws Exception Si ocurre un error durante la actualización
 */
public int completarReservasUsuarioLibro(Integer idUsuario, Integer idLibro) throws Exception {
    Connection conexion = null;
    PreparedStatement stmt = null;
    int reservasActualizadas = 0;
    
    try {
        // Usar DatabaseUtil en lugar de ConexionBD
        conexion = DatabaseUtil.getConnection();
        
        // Actualizar todas las reservas pendientes del usuario para este libro
        String sql = "UPDATE Reservas SET idEstadoReserva = 2 " + // 2 = Completada
                     "WHERE idUsuario = ? AND idLibro = ? AND idEstadoReserva = 1"; // 1 = Pendiente
        
        stmt = conexion.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        stmt.setInt(2, idLibro);
        
        reservasActualizadas = stmt.executeUpdate();
        
        return reservasActualizadas;
    } catch (Exception e) {
        throw new Exception("Error al completar reservas: " + e.getMessage());
    } finally {
        // Gestión correcta de recursos
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // Ignorar
            }
        }
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                // Ignorar
            }
        }
    }
}

    /**
     * Registra una nueva reserva y configura el observador correspondiente.
     * 
     * @param reserva La reserva a registrar
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @return La reserva registrada con su ID
     * @throws Exception Si ocurre un error durante el registro
     */
    public Reserva registrarReserva(Reserva reserva, Integer idUsuarioOperador) throws Exception {
        // Validar que el libro exista
        Libro libro = libroDAO.obtenerPorId(reserva.getIdLibro());
        if (libro == null) {
            throw new Exception("El libro no existe en la base de datos.");
        }
        
        // Validar que el usuario exista
        Usuario usuario = usuarioDAO.obtenerPorId(reserva.getIdUsuario());
        if (usuario == null) {
            throw new Exception("El usuario no existe en la base de datos.");
        }
        
        // Si no se especificó estado de reserva, usar el pendiente (1)
        if (reserva.getIdEstadoReserva() == null) {
            reserva.setIdEstadoReserva(1); // 1 = pendiente
        }
        
        // Si no se especificó fecha de reserva, usar la fecha actual
        if (reserva.getFechaReserva() == null) {
            reserva.setFechaReserva(new Date());
        }
        
        // Calcular fecha de vencimiento (7 días después por defecto)
        if (reserva.getFechaVencimiento() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reserva.getFechaReserva());
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            reserva.setFechaVencimiento(calendar.getTime());
        }
        
        // Registrar la reserva
        reservaDAO.insertar(reserva);
        
        // Configurar notificación cuando el libro esté disponible
        if (libro.getIdEstado() != 1) { // Si no está disponible
            LibroNotificador notificador = libroServicio.getNotificador(libro.getIdLibro());
            
            if (notificador == null) {
                notificador = new LibroNotificador(libro);
                // Si libroServicio tiene un método para guardar el notificador, usarlo aquí
            }
            
            UsuarioNotificacion usuarioNotificacion = new UsuarioNotificacion(usuario, reservaDAO);
            notificador.registerObserver(usuarioNotificacion);
        }
        
        // Registrar en log
        registrarLog(idUsuarioOperador, "Registro de reserva", reserva);
        
        return reserva;
    }
    
    /**
     * Actualiza el estado de una reserva.
     * 
     * @param idReserva ID de la reserva
     * @param nuevoEstado Nuevo estado para la reserva
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la actualización
     */
    public void cambiarEstadoReserva(Integer idReserva, Integer nuevoEstado, Integer idUsuarioOperador) throws Exception {
        Reserva reserva = reservaDAO.obtenerPorId(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva no existe en la base de datos.");
        }
        
        // Cambiar el estado
        reservaDAO.cambiarEstado(idReserva, nuevoEstado);
        
        // Si la reserva pasa a completada (2), actualizar libro si es necesario
        if (nuevoEstado == 2) { // 2 = completada
            Libro libro = libroDAO.obtenerPorId(reserva.getIdLibro());
            
            // Si la reserva se completa y el libro está disponible, marcarlo como reservado
            if (libro.getIdEstado() == 1) { // 1 = disponible
                libroServicio.actualizarEstadoLibro(libro.getIdLibro(), 3, idUsuarioOperador); // 3 = reservado
            }
        }
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idReserva", idReserva);
        detalles.put("estadoAnterior", reserva.getIdEstadoReserva());
        detalles.put("nuevoEstado", nuevoEstado);
        
        Log log = new Log();
        log.setIdTipoLog(4); // 4 = reserva
        log.setIdUsuario(idUsuarioOperador);
        log.setAccion("Cambio de estado de reserva");
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
    
    /**
     * Elimina una reserva.
     * 
     * @param idReserva ID de la reserva a eliminar
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @throws Exception Si ocurre un error durante la eliminación
     */
    public void eliminarReserva(Integer idReserva, Integer idUsuarioOperador) throws Exception {
        Reserva reserva = reservaDAO.obtenerPorId(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva no existe en la base de datos.");
        }
        
        // Eliminar la reserva
        reservaDAO.eliminar(idReserva);
        
        // Registrar en log
        JSONObject detalles = new JSONObject();
        detalles.put("idReserva", idReserva);
        detalles.put("idLibro", reserva.getIdLibro());
        detalles.put("idUsuario", reserva.getIdUsuario());
        
        Log log = new Log();
        log.setIdTipoLog(4); // 4 = reserva
        log.setIdUsuario(idUsuarioOperador);
        log.setAccion("Eliminación de reserva");
        log.setDetalles(detalles.toString());
        
        logDAO.insertar(log);
    }
    
    /**
     * Obtiene todas las reservas.
     * 
     * @return Lista con todas las reservas
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Reserva> obtenerTodasReservas() throws Exception {
        return reservaDAO.obtenerTodos();
    }
    
    /**
     * Obtiene las reservas pendientes.
     * 
     * @return Lista con las reservas pendientes
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Reserva> obtenerReservasPendientes() throws Exception {
        return reservaDAO.obtenerReservasPendientes();
    }
    
    /**
     * Obtiene las reservas vencidas.
     * 
     * @return Lista con las reservas vencidas
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Reserva> obtenerReservasVencidas() throws Exception {
        return reservaDAO.obtenerReservasVencidas();
    }
    
    /**
     * Obtiene las reservas de un usuario específico.
     * 
     * @param idUsuario ID del usuario
     * @return Lista con las reservas del usuario
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Reserva> obtenerReservasPorUsuario(Integer idUsuario) throws Exception {
        return reservaDAO.buscarPorUsuario(idUsuario);
    }
    
    /**
     * Obtiene las reservas para un libro específico.
     * 
     * @param idLibro ID del libro
     * @return Lista con las reservas del libro
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Reserva> obtenerReservasPorLibro(Integer idLibro) throws Exception {
        return reservaDAO.buscarPorLibro(idLibro);
    }
    
    /**
     * Verifica si un usuario tiene una reserva activa para un libro específico.
     * 
     * @param idUsuario ID del usuario
     * @param idLibro ID del libro
     * @return true si el usuario tiene una reserva activa para el libro
     * @throws Exception Si ocurre un error durante la consulta
     */
    public boolean tieneReservaActiva(Integer idUsuario, Integer idLibro) throws Exception {
        return reservaDAO.tieneReservaActiva(idUsuario, idLibro);
    }
    
    /**
     * Actualiza las reservas vencidas a estado "vencida".
     * 
     * @param idUsuarioOperador ID del usuario que realiza la operación
     * @return Número de reservas actualizadas
     * @throws Exception Si ocurre un error durante la actualización
     */
    public int actualizarReservasVencidas(Integer idUsuarioOperador) throws Exception {
        List<Reserva> reservasVencidas = reservaDAO.obtenerReservasVencidas();
        int count = 0;
        
        for (Reserva reserva : reservasVencidas) {
            // Sólo actualizar si está en estado pendiente (1)
            if (reserva.getIdEstadoReserva() == 1) {
                reservaDAO.cambiarEstado(reserva.getIdReserva(), 3); // 3 = vencida
                count++;
                
                // Registrar en log
                JSONObject detalles = new JSONObject();
                detalles.put("idReserva", reserva.getIdReserva());
                detalles.put("idLibro", reserva.getIdLibro());
                detalles.put("idUsuario", reserva.getIdUsuario());
                
                Log log = new Log();
                log.setIdTipoLog(4); // 4 = reserva
                log.setIdUsuario(idUsuarioOperador);
                log.setAccion("Reserva marcada como vencida");
                log.setDetalles(detalles.toString());
                
                logDAO.insertar(log);
            }
        }
        
        return count;
    }
    
    /**
     * Registra una operación en el log.
     * 
     * @param idUsuario ID del usuario que realiza la operación
     * @param accion Descripción de la acción
     * @param reserva La reserva involucrada
     * @throws Exception Si ocurre un error al registrar el log
     */
    private void registrarLog(Integer idUsuario, String accion, Reserva reserva) throws Exception {
        JSONObject detalles = new JSONObject();
        detalles.put("idReserva", reserva.getIdReserva());
        detalles.put("idLibro", reserva.getIdLibro());
        detalles.put("idUsuario", reserva.getIdUsuario());
        detalles.put("estado", reserva.getIdEstadoReserva());
        detalles.put("fechaReserva", reserva.getFechaReserva().toString());
        detalles.put("fechaVencimiento", reserva.getFechaVencimiento().toString());
        
        Log log = new Log();
        log.setIdTipoLog(4); // 4 = reserva
        log.setIdUsuario(idUsuario);
        log.setAccion(accion);
        log.setDetalles(detalles.toString());
        // La fecha se establece automáticamente en el constructor de Log
        
        logDAO.insertar(log);
    }
    
    /**
    * Obtiene una reserva específica por su ID.
    * 
    * @param id El ID de la reserva a buscar
    * @return La reserva encontrada o null si no existe
    * @throws Exception Si ocurre un error durante la consulta
    */
   public Reserva obtenerReservaPorId(Integer id) throws Exception {
       if (id == null) {
           throw new IllegalArgumentException("El ID de la reserva no puede ser nulo");
       }

       // Obtener la reserva usando el DAO
       Reserva reserva = reservaDAO.obtenerPorId(id);

       // Si la reserva existe, cargar datos relacionados como usuario y libro
       if (reserva != null) {
           try {
               Usuario usuario = usuarioDAO.obtenerPorId(reserva.getIdUsuario());
               reserva.setUsuario(usuario);

               Libro libro = libroDAO.obtenerPorId(reserva.getIdLibro());
               reserva.setLibro(libro);
           } catch (Exception e) {
               // Si hay error al cargar datos relacionados, solo log el error pero devolver la reserva
               System.err.println("Error al cargar datos relacionados para la reserva: " + e.getMessage());
           }
       }

       return reserva;
   }
}