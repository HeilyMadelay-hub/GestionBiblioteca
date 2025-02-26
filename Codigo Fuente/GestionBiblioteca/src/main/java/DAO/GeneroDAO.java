package DAO;

import Modelo.Genero;

public interface GeneroDAO extends DAO<Genero> {
    void eliminarTodos() throws Exception;
}