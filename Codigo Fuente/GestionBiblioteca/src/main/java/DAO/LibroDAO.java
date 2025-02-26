package DAO;

import Modelo.Libro;
import Modelo.Autor;
import Modelo.Genero;
import Modelo.Categoria;
import java.util.List;

public interface LibroDAO extends DAO<Libro> {
    void eliminarTodos() throws Exception;
    
    // Métodos adicionales para manejar relaciones
    void agregarAutor(Integer idLibro, Integer idAutor) throws Exception;
    void eliminarAutor(Integer idLibro, Integer idAutor) throws Exception;
    List<Autor> obtenerAutores(Integer idLibro) throws Exception;
    
    void agregarGenero(Integer idLibro, Integer idGenero) throws Exception;
    void eliminarGenero(Integer idLibro, Integer idGenero) throws Exception;
    List<Genero> obtenerGeneros(Integer idLibro) throws Exception;
    
    void agregarCategoria(Integer idLibro, Integer idCategoria) throws Exception;
    void eliminarCategoria(Integer idLibro, Integer idCategoria) throws Exception;
    List<Categoria> obtenerCategorias(Integer idLibro) throws Exception;
    
    // Métodos de búsqueda
    List<Libro> buscarPorTitulo(String titulo) throws Exception;
    Libro buscarPorIsbn(String isbn) throws Exception;
    List<Libro> buscarPorAutor(Integer idAutor) throws Exception;
    List<Libro> buscarPorGenero(Integer idGenero) throws Exception;
    List<Libro> buscarPorCategoria(Integer idCategoria) throws Exception;
}
