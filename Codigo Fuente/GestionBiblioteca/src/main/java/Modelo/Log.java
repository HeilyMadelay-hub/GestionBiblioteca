package Modelo;

import java.time.LocalDateTime;

public class Log {
    private Integer idLog;
    private Integer idTipoLog;
    private Integer idUsuario;
    private String accion;
    private String detalles;
    private LocalDateTime fecha;
    
    public Log() {
        this.fecha = LocalDateTime.now();
    }
    
    public Log(Integer idTipoLog, Integer idUsuario, String accion, String detalles) {
        this.idTipoLog = idTipoLog;
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.detalles = detalles;
        this.fecha = LocalDateTime.now();
    }
    
    // Getters y setters
    public Integer getIdLog() { return idLog; }
    public void setIdLog(Integer idLog) { this.idLog = idLog; }
    public Integer getIdTipoLog() { return idTipoLog; }
    public void setIdTipoLog(Integer idTipoLog) { this.idTipoLog = idTipoLog; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}