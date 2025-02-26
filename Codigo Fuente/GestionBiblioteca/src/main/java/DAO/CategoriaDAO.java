package DAO;

import Modelo.Categoria;

public interface CategoriaDAO extends DAO<Categoria> {
    void eliminarTodos() throws Exception;
}