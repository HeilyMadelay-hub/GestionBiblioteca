
package Modelo;

import java.util.Date;

public class Reserva {
    private Integer idReserva;
    private Integer idUsuario;
    private Integer idLibro;
    private Integer idEstadoReserva;
    private Date fechaReserva;
    private Date fechaVencimiento;
    
    // Para relaciones
    private Usuario usuario;
    private Libro libro;

    // Constructor
    public Reserva() {
        this.fechaReserva = new Date(); // Fecha actual por defecto
    }

    // Getters y Setters
    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public Integer getIdEstadoReserva() {
        return idEstadoReserva;
    }

    public void setIdEstadoReserva(Integer idEstadoReserva) {
        this.idEstadoReserva = idEstadoReserva;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", idUsuario=" + idUsuario +
                ", idLibro=" + idLibro +
                ", idEstadoReserva=" + idEstadoReserva +
                ", fechaReserva=" + fechaReserva +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}

