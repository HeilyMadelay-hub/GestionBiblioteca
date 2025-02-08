
package DAO;

import java.util.List;


public interface DAO<T> {
    void insertar(T entidad) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(Integer id) throws Exception;
    T obtenerPorId(Integer id) throws Exception;
    List<T> obtenerTodos() throws Exception;
}