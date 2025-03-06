
package Servicio;

import DAO.GeneroDAO;
import Modelo.Genero;
import java.util.List;


public class GeneroServicio {

    private GeneroDAO generoDAO;

    /**
     * Constructor que inyecta la dependencia del DAO asociado a los géneros.
     * @param generoDAO El DAO para acceder a los datos de los géneros.
     */
    public GeneroServicio(GeneroDAO generoDAO) {
        this.generoDAO = generoDAO;
    }

    /**
     * Registra un nuevo género en el sistema.
     * @param genero El género a registrar.
     * @throws Exception Si ocurre un error durante el registro o si la validación falla.
     */
    public void registrarGenero(Genero genero) throws Exception {
        validarGenero(genero);
        generoDAO.insertar(genero);
        System.out.println("Género registrado con éxito: " + genero);
    }

    /**
     * Actualiza la información de un género existente.
     * @param genero El género con la información actualizada.
     * @throws Exception Si ocurre un error durante la actualización o si la validación falla.
     */
    public void actualizarGenero(Genero genero) throws Exception {
        validarGenero(genero);
        generoDAO.actualizar(genero);
        System.out.println("Género actualizado con éxito: " + genero);
    }

    /**
     * Elimina un género del sistema.
     * @param idGenero El ID del género a eliminar.
     * @throws Exception Si ocurre un error durante la eliminación.
     */
    public void eliminarGenero(int idGenero) throws Exception {
        generoDAO.eliminar(idGenero);
        System.out.println("Género eliminado con éxito");
    }

    /**
     * Recupera un género por su ID.
     * @param idGenero El ID del género a recuperar.
     * @return El género encontrado o null si no existe.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public Genero obtenerGeneroPorId(int idGenero) throws Exception {
        Genero genero = generoDAO.obtenerPorId(idGenero);
        if (genero == null) {
            System.out.println("No se encontró el género con ID: " + idGenero);
        }
        return genero;
    }

    /**
     * Obtiene y muestra todos los géneros registrados.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public void obtenerTodosGeneros() throws Exception {
        List<Genero> generos = generoDAO.obtenerTodos();
        generos.forEach(System.out::println);
    }

    /**
     * Valida los datos del género.
     * @param genero El género a validar.
     * @throws Exception Si el nombre del género es nulo o vacío, o excede la longitud permitida.
     */
    private void validarGenero(Genero genero) throws Exception {
        if (genero.getNombre() == null || genero.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del género es obligatorio.");
        }
        if (genero.getNombre().length() > 50) {
            throw new Exception("El nombre del género no puede exceder 50 caracteres.");
        }
    }
}
