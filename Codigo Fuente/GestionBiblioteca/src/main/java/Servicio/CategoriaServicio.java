package Servicio;

import DAO.CategoriaDAO;
import DAO.IMPL.CategoriaDAOImpl;
import Modelo.Categoria;
import Modelo.Libro;
import java.util.*;
public class CategoriaServicio {
    private CategoriaDAO categoriaDAO;
    private LibroServicio libroServicio; 

    public CategoriaServicio() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    
    /**
     * Constructor con inyección de dependencias.
     * @param categoriaDAO DAO para acceder a los datos de categorías.
     * @param libroServicio Servicio para operaciones con libros.
     */
    public CategoriaServicio(CategoriaDAO categoriaDAO, LibroServicio libroServicio) {
        this.categoriaDAO = categoriaDAO;
        this.libroServicio = libroServicio;
    }
    
    /**
     * Establece el servicio de libros para esta instancia.
     * @param libroServicio El servicio de libros a utilizar.
     */
    public void setLibroServicio(LibroServicio libroServicio) {
        this.libroServicio = libroServicio;
    }
    
    public List<Categoria> obtenerTodos() {
        try {
            return categoriaDAO.obtenerTodos();
        } catch (Exception e) {
            System.err.println("Error al obtener las categorías: " + e.getMessage());
            return new ArrayList<>(); // Retorna lista vacía en caso de error
        }
    }
    
    public void agregarCategoria(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new Exception("El nombre de la categoría no puede estar vacío.");
            }
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoriaDAO.insertar(categoria);
            System.out.println("Categoría agregada con éxito: " + categoria);
        } catch (Exception e) {
            System.err.println("Error al agregar la categoría: " + e.getMessage());
        }
    }

    public void eliminarCategoria(int id) {
        try {
            categoriaDAO.eliminar(id);
            System.out.println("Categoría eliminada con éxito");
        } catch (Exception e) {
            System.err.println("Error al eliminar la categoría: " + e.getMessage());
        }
    }

    public void obtenerYCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.obtenerTodos();
            categorias.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error al obtener las categorías: " + e.getMessage());
        }
    }

    public void actualizarCategoria(int id, String nuevoNombre) {
        try {
            if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                throw new Exception("El nombre de la categoría no puede estar vacío.");
            }
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(id);
            categoria.setNombre(nuevoNombre);
            categoriaDAO.actualizar(categoria);
            System.out.println("Categoría actualizada con éxito: " + categoria);
        } catch (Exception e) {
            System.err.println("Error al actualizar la categoría: " + e.getMessage());
        }
    }

    public void eliminarTodasCategorias() {
        try {
            categoriaDAO.eliminarTodos();
            System.out.println("Todas las categorías han sido eliminadas");
        } catch (Exception e) {
            System.err.println("Error al eliminar todas las categorías: " + e.getMessage());
        }
    }

    public void obtenerCategoriaPorId(int id) {
        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            if (categoria != null) {
                System.out.println("Categoría encontrada: " + categoria);
            } else {
                System.out.println("No se encontró la categoría con ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener la categoría: " + e.getMessage());
        }
    }
    
    public Categoria obtenerPorId(int id) {
        try {
            return categoriaDAO.obtenerPorId(id);
        } catch (Exception e) {
            System.err.println("Error al obtener la categoría: " + e.getMessage());
            return null;
        }
    }
    
    
    /**
     * Obtiene todas las categorías con sus libros asociados.
     * @return Un mapa donde la clave es la categoría y el valor es la lista de libros de esa categoría.
     * @throws Exception Si ocurre un error durante la consulta o si libroServicio no está inicializado.
     */
    public Map<Categoria, List<Libro>> obtenerCategoriasConLibros() throws Exception {
        if (libroServicio == null) {
            throw new Exception("LibroServicio no ha sido inicializado. Use el constructor con parámetros o setLibroServicio().");
        }
        
        Map<Categoria, List<Libro>> resultado = new HashMap<>();
        
        // Obtener todas las categorías
        List<Categoria> categorias = categoriaDAO.obtenerTodos();
        
        // Para cada categoría, obtener sus libros
        for (Categoria categoria : categorias) {
            List<Libro> libros = libroServicio.obtenerLibrosPorCategoria(categoria.getIdCategoria());
            resultado.put(categoria, libros);
        }
        
        return resultado;
    }
}
