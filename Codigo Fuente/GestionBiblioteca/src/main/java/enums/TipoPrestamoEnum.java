package enums;

public enum TipoPrestamoEnum {
    REGULAR(1, "regular", 30),
    EXPRESS(2, "express", 7);

    private final int id;
    private final String nombre;
    private final int diasMax;

    TipoPrestamoEnum(int id, String nombre, int diasMax) {
        this.id = id;
        this.nombre = nombre;
        this.diasMax = diasMax;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDiasMax() {
        return diasMax;
    }

    public static TipoPrestamoEnum getById(int id) {
        for (TipoPrestamoEnum tipo : values()) {
            if (tipo.getId() == id) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No existe tipo de préstamo con id: " + id);
    }
}
