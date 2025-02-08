
package DAO;

import Modelo.Autor;

public interface AutorDAO extends DAO<Autor> {
    
    void eliminarTodos() throws Exception;
    
}