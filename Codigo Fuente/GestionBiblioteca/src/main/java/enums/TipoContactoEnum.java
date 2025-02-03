package enums;

public enum TipoContactoEnum {
    EMAIL(1, "email"),
    TELEFONO(2, "telefono");

    private final int id;
    private final String nombre;

    TipoContactoEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoContactoEnum getById(int id) {
        for (TipoContactoEnum tipo : values()) {
            if (tipo.getId() == id) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No existe tipo de contacto con id: " + id);
    }
}
