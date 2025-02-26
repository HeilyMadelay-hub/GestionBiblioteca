
package Observer;

/**
 * Interfaz que define un observador en el patrón Observer.
 * Los observadores reciben notificaciones de los objetos observables.
 */
public interface Observer {
    
    /**
     * Método que será llamado cuando el objeto observado cambie de estado.
     * 
     * @param object El objeto observable que ha cambiado
     * @param data Datos adicionales sobre el cambio (puede ser null)
     */
    void update(Observable object, Object data);
}