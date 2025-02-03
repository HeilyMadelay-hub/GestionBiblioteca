
package enums;

public enum RolEnum {
    ADMINISTRADOR(1, "administrador"),
    USUARIO(2, "usuario");

    private final int id;
    private final String nombre;

    RolEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static RolEnum getById(int id) {
        for (RolEnum rol : values()) {
            if (rol.getId() == id) {
                return rol;
            }
        }
        throw new IllegalArgumentException("No existe rol con id: " + id);
    }
}
