package DAO;

import Modelo.Usuario;

public interface UsuarioDAO extends DAO<Usuario> {
    
    void eliminarTodos() throws Exception;

    boolean tienePrestamosActivos(Integer idUsuario);
    
    /**
     * Obtiene un usuario por su nombre.
     *
     * @param nombre el nombre del usuario
     * @return el usuario encontrado o null si no existe
     * @throws Exception si ocurre algún error en la consulta
     */
    Usuario obtenerUsuarioPorNombre(String nombre) throws Exception;
    
}