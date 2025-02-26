
package Observer;

import DAO.ReservaDAO;
import Modelo.Libro;
import Modelo.Usuario;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del observador para usuarios que han reservado libros.
 * Recibe notificaciones cuando un libro reservado está disponible.
 */
public class UsuarioNotificacion implements Observer {
    
    private static final Logger LOGGER = Logger.getLogger(UsuarioNotificacion.class.getName());
    
    private final Usuario usuario;
    private final ReservaDAO reservaDAO;
    
    /**
     * Constructor que recibe el usuario y el DAO para consultar reservas.
     * 
     * @param usuario El usuario que recibirá notificaciones
     * @param reservaDAO DAO para consultar reservas del usuario
     */
    public UsuarioNotificacion(Usuario usuario, ReservaDAO reservaDAO) {
        this.usuario = usuario;
        this.reservaDAO = reservaDAO;
    }
    
    /**
     * Obtiene el usuario asociado con esta notificación.
     * 
     * @return El usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }
    
    @Override
    public void update(Observable observable, Object data) {
    if (observable instanceof LibroNotificador && data instanceof Libro) {
        Libro libro = (Libro) data;
        try {
            // Verificar si reservaDAO es null (solo para pruebas)
            if (reservaDAO == null) {
                System.out.println("NOTIFICACIÓN para usuario " + usuario.getNombre() + 
                    ": El libro '" + libro.getTitulo() + "' (ID: " + libro.getIdLibro() + 
                    ") está ahora disponible para préstamo. [SIMULACIÓN DE PRUEBA]");
                return;
            }
            
            // Verificar si el usuario tiene reservas pendientes para este libro
            boolean tieneReserva = reservaDAO.tieneReservaActiva(
                    usuario.getIdUsuario(), libro.getIdLibro());
            
            if (tieneReserva) {
                // Si tiene una reserva activa, enviar notificación
                enviarNotificacion(libro);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al procesar notificación para usuario", ex);
        }
    }
}
    /**
     * Envía una notificación al usuario sobre la disponibilidad del libro.
     * Esta implementación registra la notificación en el log.
     * En una implementación real, podría enviar un email, SMS, etc.
     * 
     * @param libro El libro que está disponible
     */
    private void enviarNotificacion(Libro libro) {
        // Aquí se implementaría la lógica para enviar un email, SMS, etc.
        // Por ahora solo registramos en el log
        LOGGER.log(Level.INFO, "NOTIFICACIÓN para usuario {0}: El libro '{1}' (ID: {2}) " +
                "que reservaste está ahora disponible para préstamo.", 
                new Object[]{usuario.getNombre(), libro.getTitulo(), libro.getIdLibro()});
        
        // Si se implementara envío de email:
        // emailService.enviarEmail(usuario.getEmail(), 
        //     "Libro disponible: " + libro.getTitulo(),
        //     "El libro que reservaste está ahora disponible para préstamo.");
    }
}