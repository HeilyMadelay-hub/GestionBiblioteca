
package Servicio;

import DAO.ContactoDAO;
import Modelo.Contacto;
import java.util.List;

public class ContactoServicio {

    private ContactoDAO contactoDAO;

    /**
     * Constructor que inyecta la dependencia del DAO asociado a los contactos.
     * @param contactoDAO El DAO para acceder a los datos de los contactos.
     */
    public ContactoServicio(ContactoDAO contactoDAO) {
        this.contactoDAO = contactoDAO;
    }

    /**
     * Registra un nuevo contacto en el sistema.
     * @param contacto El contacto a registrar.
     * @throws Exception Si ocurre un error durante el registro.
     */
    public void registrarContacto(Contacto contacto) throws Exception {
        if (contacto == null || contacto.getValor().isEmpty()) {
            throw new IllegalArgumentException("La información del contacto no puede estar vacía.");
        }
        contactoDAO.insertar(contacto);
    }

    /**
     * Actualiza la información de un contacto existente.
     * @param contacto El contacto con la información actualizada.
     * @throws Exception Si ocurre un error durante la actualización.
     */
    public void actualizarContacto(Contacto contacto) throws Exception {
        if (contacto == null || contacto.getIdContacto() == null) {
            throw new IllegalArgumentException("El contacto debe tener un ID válido para actualizar.");
        }
        contactoDAO.actualizar(contacto);
    }

    /**
     * Elimina un contacto del sistema.
     * @param idContacto El ID del contacto a eliminar.
     * @throws Exception Si ocurre un error durante la eliminación.
     */
    public void eliminarContacto(Integer idContacto) throws Exception {
        if (idContacto == null) {
            throw new IllegalArgumentException("El ID del contacto no puede estar vacío.");
        }
        contactoDAO.eliminar(idContacto);
    }

    /**
     * Obtiene un contacto por su ID.
     * @param idContacto El ID del contacto a obtener.
     * @return El contacto, o null si no se encuentra.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public Contacto obtenerContactoPorId(Integer idContacto) throws Exception {
        return contactoDAO.obtenerPorId(idContacto);
    }

    /**
     * Obtiene todos los contactos registrados en el sistema.
     * @return Una lista de todos los contactos.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public List<Contacto> obtenerTodosLosContactos() throws Exception {
        return contactoDAO.obtenerTodos();
    }
}
