package enums;


public enum EstadoLibroEnum {
    
    DISPONIBLE(1, "disponible"),
    PRESTADO(2, "prestado"),
    RESERVADO(3, "reservado"),
    NO_DISPONIBLE(4, "no_disponible");

    private final int id;
    private final String nombre;

    EstadoLibroEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoLibroEnum getById(int id) {
        for (EstadoLibroEnum estado : values()) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No existe estado de libro con id: " + id);
    }
}