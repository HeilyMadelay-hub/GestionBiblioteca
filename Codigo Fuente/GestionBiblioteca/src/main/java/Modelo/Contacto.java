package Modelo;

public class Contacto {
    private Integer idContacto;
    private Integer idUsuario;
    private Integer idTipoContacto;
    private String valor;
    private Boolean verificado;

    // Constructor
    public Contacto() {
        this.verificado = false;
    }

    // Getters y Setters
    public Integer getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Integer idContacto) {
        this.idContacto = idContacto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdTipoContacto() {
        return idTipoContacto;
    }

    public void setIdTipoContacto(Integer idTipoContacto) {
        this.idTipoContacto = idTipoContacto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "idContacto=" + idContacto +
                ", idUsuario=" + idUsuario +
                ", idTipoContacto=" + idTipoContacto +
                ", valor='" + valor + '\'' +
                ", verificado=" + verificado +
                '}';
    }
}