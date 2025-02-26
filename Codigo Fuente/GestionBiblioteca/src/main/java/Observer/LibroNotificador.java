
package Observer;

import java.util.ArrayList;
import java.util.List;
import Modelo.Libro;

/**
 * Implementación del observable para notificar cambios en el estado de los libros.
 * Notifica a los observadores cuando un libro cambia su estado a disponible.
 */
public class LibroNotificador implements Observable {
    
    private Libro libro;
    private final List<Observer> observers = new ArrayList<>();
    private boolean isDisponible = false;
    
    /**
     * Constructor que recibe el libro a monitorear.
     * 
     * @param libro El libro a monitorear
     */
    public LibroNotificador(Libro libro) {
        this.libro = libro;
    }
    
    /**
     * Obtiene el libro asociado con este notificador.
     * 
     * @return El libro monitoreado
     */
    public Libro getLibro() {
        return libro;
    }
    
    /**
     * Actualiza el libro asociado con este notificador.
     * 
     * @param libro El nuevo libro a monitorear
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
    }
    
    /**
     * Verifica si el libro está disponible.
     * 
     * @return true si el libro está disponible
     */
    public boolean isDisponible() {
        return isDisponible;
    }
    
    /**
     * Cambia el estado de disponibilidad del libro y notifica a los observadores si está disponible.
     * 
     * @param isDisponible El nuevo estado de disponibilidad
     */
    public void setDisponible(boolean isDisponible) {
        this.isDisponible = isDisponible;
        // Sólo notificamos cuando el libro pasa a estar disponible
        if (isDisponible) {
            notifyObservers();
        }
    }
    
    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this, libro);
        }
    }
}