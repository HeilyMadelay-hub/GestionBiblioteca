package enums;

public enum EstadoReservaEnum {
    PENDIENTE(1, "pendiente"),
    COMPLETADA(2, "completada"),
    VENCIDA(3, "vencida"),
    CANCELADA(4, "cancelada");

    private final int id;
    private final String nombre;

    EstadoReservaEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoReservaEnum getById(int id) {
        for (EstadoReservaEnum estado : values()) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No existe estado de reserva con id: " + id);
    }
}
