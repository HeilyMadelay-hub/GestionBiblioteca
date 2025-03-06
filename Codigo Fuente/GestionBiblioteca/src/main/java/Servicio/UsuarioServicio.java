
package Servicio;

import DAO.UsuarioDAO;
import Modelo.Usuario;
import Util.SecurityUtil;
import java.util.List;
public class UsuarioServicio {

    private UsuarioDAO usuarioDAO;

    /**
     * Constructor que inyecta la dependencia del DAO asociado a los usuarios.
     * @param usuarioDAO El DAO para acceder a los datos de los usuarios.
     */
    public UsuarioServicio(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario El usuario a registrar.
     * @throws Exception Si ocurre un error durante el registro o si la validación falla.
     */
    public void registrarUsuario(Usuario usuario) throws Exception {
        validarDatosUsuario(usuario);
        
        usuario.setPassword(SecurityUtil.hashPassword(usuario.getPassword()));
        usuarioDAO.insertar(usuario);
    }

    /**
     * Actualiza la información de un usuario existente.
     * @param usuario El usuario con la información actualizada.
     * @throws Exception Si ocurre un error durante la actualización o si la validación falla.
     */
    public void actualizarUsuario(Usuario usuario) throws Exception {
        validarDatosUsuario(usuario);
        usuarioDAO.actualizar(usuario);
    }

    /**
     * Elimina un usuario del sistema.
     * @param idUsuario El ID del usuario a eliminar.
     * @throws Exception Si el usuario no puede ser eliminado debido a restricciones de negocio.
     */
    public void eliminarUsuario(Integer idUsuario) throws Exception {
        if (usuarioDAO.tienePrestamosActivos(idUsuario)) {
            throw new Exception("El usuario no puede ser eliminado porque tiene préstamos activos.");
        }
        usuarioDAO.eliminar(idUsuario);
    }

    /**
     * Recupera un usuario por su ID.
     * @param idUsuario El ID del usuario a recuperar.
     * @return Usuario El usuario recuperado.
     * @throws Exception Si el usuario no existe.
     */
    public Usuario obtenerUsuarioPorId(Integer idUsuario) throws Exception {
        return usuarioDAO.obtenerPorId(idUsuario);
    }

    /**
     * Valida los datos del usuario.
     * @param usuario El usuario a validar.
     * @throws Exception Si los datos del usuario no son válidos.
     */
    private void validarDatosUsuario(Usuario usuario) throws Exception {
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            throw new Exception("El nombre del usuario es obligatorio.");
        }
        // Otras validaciones pueden agregarse según sea necesario.
    }
    
    /**
     * Recupera un usuario por su nombre.
     * @param nombre El nombre del usuario a buscar.
     * @return El usuario encontrado o null si no existe.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    public Usuario obtenerUsuarioPorNombre(String nombre) throws Exception {
        return usuarioDAO.obtenerUsuarioPorNombre(nombre);
    }
    
    public List<Usuario> obtenerTodos() throws Exception {
        return usuarioDAO.obtenerTodos();
    }
}
