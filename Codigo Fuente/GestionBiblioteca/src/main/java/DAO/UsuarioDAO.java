package DAO;

import Modelo.Usuario;

public interface UsuarioDAO extends DAO<Usuario> {
    
    void eliminarTodos() throws Exception;
    
}