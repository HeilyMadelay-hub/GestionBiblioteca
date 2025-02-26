package Observer;

/**
 * Interfaz que define un objeto observable en el patrón Observer.
 * Los objetos observables pueden registrar, eliminar y notificar a los observadores.
 */
public interface Observable {
    
    /**
     * Registra un nuevo observador para recibir notificaciones.
     * 
     * @param observer El observador a registrar
     */
    void registerObserver(Observer observer);
    
    /**
     * Elimina un observador para que ya no reciba notificaciones.
     * 
     * @param observer El observador a eliminar
     */
    void removeObserver(Observer observer);
    
    /**
     * Notifica a todos los observadores registrados sobre un cambio.
     */
    void notifyObservers();
}