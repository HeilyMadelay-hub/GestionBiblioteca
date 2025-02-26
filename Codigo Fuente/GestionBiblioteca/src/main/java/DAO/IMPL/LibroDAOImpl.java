package DAO.IMPL;


import DAO.LibroDAO;
import DAO.AutorDAO;
import DAO.GeneroDAO;
import DAO.CategoriaDAO;
import Modelo.Libro;
import Modelo.Autor;
import Modelo.Genero;
import Modelo.Categoria;
import Util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {
    private static final String SQL_INSERT = "INSERT INTO libros (titulo, isbn, fecha_adquisicion, id_estado) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE libros SET titulo = ?, isbn = ?, fecha_adquisicion = ?, id_estado = ? WHERE id_libro = ?";
    private static final String SQL_DELETE = "DELETE FROM libros WHERE id_libro = ?";
    private static final String SQL_SELECT = "SELECT * FROM libros WHERE id_libro = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM libros ORDER BY id_libro";
    private static final String SQL_SELECT_BY_ISBN = "SELECT * FROM libros WHERE isbn = ?";

    // Consultas para relaciones
    private static final String SQL_ADD_AUTOR = "INSERT INTO libros_autores (id_libro, id_autor) VALUES (?, ?)";
    private static final String SQL_REMOVE_AUTOR = "DELETE FROM libros_autores WHERE id_libro = ? AND id_autor = ?";
    private static final String SQL_GET_AUTORES = "SELECT a.* FROM autores a INNER JOIN libros_autores la ON a.id_autor = la.id_autor WHERE la.id_libro = ?";

    private static final String SQL_ADD_GENERO = "INSERT INTO libros_generos (id_libro, id_genero) VALUES (?, ?)";
    private static final String SQL_REMOVE_GENERO = "DELETE FROM libros_generos WHERE id_libro = ? AND id_genero = ?";
    private static final String SQL_GET_GENEROS = "SELECT g.* FROM generos g INNER JOIN libros_generos lg ON g.id_genero = lg.id_genero WHERE lg.id_libro = ?";

    private static final String SQL_ADD_CATEGORIA = "INSERT INTO libros_categorias (id_libro, id_categoria) VALUES (?, ?)";
    private static final String SQL_REMOVE_CATEGORIA = "DELETE FROM libros_categorias WHERE id_libro = ? AND id_categoria = ?";
    private static final String SQL_GET_CATEGORIAS = "SELECT c.* FROM categorias c INNER JOIN libros_categorias lc ON c.id_categoria = lc.id_categoria WHERE lc.id_libro = ?";

    // DAOs relacionados
    private final AutorDAO autorDAO;
    private final GeneroDAO generoDAO;
    private final CategoriaDAO categoriaDAO;

    public LibroDAOImpl() {
        this.autorDAO = new AutorDAOImpl();
        this.generoDAO = new GeneroDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl();
    }

     @Override
    public void insertar(Libro libro) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Primero insertamos el libro
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, libro.getTitulo());
                stmt.setString(2, libro.getIsbn());
                stmt.setDate(3, new java.sql.Date(libro.getFechaAdquisicion().getTime()));
                stmt.setInt(4, libro.getIdEstado());
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        libro.setIdLibro(rs.getInt(1));
                    }
                }
            }

            // Luego insertamos las relaciones
            String sqlAutor = "INSERT INTO libros_autores (id_libro, id_autor) VALUES (?, ?)";
            String sqlGenero = "INSERT INTO libros_generos (id_libro, id_genero) VALUES (?, ?)";
            String sqlCategoria = "INSERT INTO libros_categorias (id_libro, id_categoria) VALUES (?, ?)";

            // Insertar autores
            for (Autor autor : libro.getAutores()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlAutor)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, autor.getIdAutor());
                    stmt.executeUpdate();
                }
            }

            // Insertar géneros
            for (Genero genero : libro.getGeneros()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlGenero)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, genero.getIdGenero());
                    stmt.executeUpdate();
                }
            }

            // Insertar categorías
            for (Categoria categoria : libro.getCategorias()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlCategoria)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, categoria.getIdCategoria());
                    stmt.executeUpdate();
                }
            }
        }
    }
   
    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Desactivar verificación de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Guardar los datos actuales (excepto el que vamos a eliminar)
            List<LibroTemp> librosTemp = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(
                "SELECT l.id_libro, " +
                "GROUP_CONCAT(DISTINCT la.id_autor) as autores, " +
                "GROUP_CONCAT(DISTINCT lg.id_genero) as generos, " +
                "GROUP_CONCAT(DISTINCT lc.id_categoria) as categorias " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id_libro = la.id_libro " +
                "LEFT JOIN libros_generos lg ON l.id_libro = lg.id_libro " +
                "LEFT JOIN libros_categorias lc ON l.id_libro = lc.id_libro " +
                "WHERE l.id_libro != " + id + " " +
                "GROUP BY l.id_libro")) {
                
                while (rs.next()) {
                    LibroTemp temp = new LibroTemp();
                    temp.idLibro = rs.getInt("id_libro");
                    temp.autores = rs.getString("autores");
                    temp.generos = rs.getString("generos");
                    temp.categorias = rs.getString("categorias");
                    librosTemp.add(temp);
                }
            }

            // Crear tabla temporal
            stmt.execute("CREATE TEMPORARY TABLE temp_libros SELECT * FROM libros WHERE id_libro != " + id);

            // Eliminar datos originales
            stmt.execute("DELETE FROM libros_autores WHERE id_libro IN (SELECT id_libro FROM libros)");
            stmt.execute("DELETE FROM libros_generos WHERE id_libro IN (SELECT id_libro FROM libros)");
            stmt.execute("DELETE FROM libros_categorias WHERE id_libro IN (SELECT id_libro FROM libros)");
            stmt.execute("DELETE FROM libros");

            // Restablecer el auto_increment
            stmt.execute("ALTER TABLE libros AUTO_INCREMENT = 1");

            // Reinsertar los datos desde la tabla temporal
            stmt.execute("INSERT INTO libros (titulo, isbn, fecha_adquisicion, id_estado) " +
                       "SELECT titulo, isbn, fecha_adquisicion, id_estado FROM temp_libros");

            // Reinsertar las relaciones
            for (LibroTemp libro : librosTemp) {
                int nuevoId = libro.idLibro;
                if (nuevoId > id) {
                    nuevoId--; // Ajustar el ID si era mayor que el eliminado
                }

                // Reinsertar autores
                if (libro.autores != null) {
                    for (String idAutor : libro.autores.split(",")) {
                        stmt.execute("INSERT INTO libros_autores (id_libro, id_autor) VALUES (" + 
                                   nuevoId + ", " + idAutor.trim() + ")");
                    }
                }

                // Reinsertar géneros
                if (libro.generos != null) {
                    for (String idGenero : libro.generos.split(",")) {
                        stmt.execute("INSERT INTO libros_generos (id_libro, id_genero) VALUES (" + 
                                   nuevoId + ", " + idGenero.trim() + ")");
                    }
                }

                // Reinsertar categorías
                if (libro.categorias != null) {
                    for (String idCategoria : libro.categorias.split(",")) {
                        stmt.execute("INSERT INTO libros_categorias (id_libro, id_categoria) VALUES (" + 
                                   nuevoId + ", " + idCategoria.trim() + ")");
                    }
                }
            }

            // Limpiar
            stmt.execute("DROP TEMPORARY TABLE IF EXISTS temp_libros");

            // Reactivar verificación de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    // Clase auxiliar para almacenar temporalmente los datos
    private static class LibroTemp {
        int idLibro;
        String autores;
        String generos;
        String categorias;
    }
    
    @Override
    public void eliminarTodos() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                stmt.execute("TRUNCATE TABLE libros_autores");
                stmt.execute("TRUNCATE TABLE libros_generos");
                stmt.execute("TRUNCATE TABLE libros_categorias");
                stmt.execute("TRUNCATE TABLE libros");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Error al eliminar todos los libros: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    throw new Exception("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public Libro obtenerPorId(Integer id) throws Exception {
        Libro libro = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(id));
                    libro.setGeneros(obtenerGeneros(id));
                    libro.setCategorias(obtenerCategorias(id));
                }
            }
        }
        return libro;
    }

    @Override
    public List<Libro> obtenerTodos() throws Exception {
        List<Libro> libros = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                Libro libro = mapearResultSetALibro(rs);
                libro.setAutores(obtenerAutores(libro.getIdLibro()));
                libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                libros.add(libro);
            }
        }
        return libros;
    }

    @Override
    public void actualizar(Libro libro) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. Actualizar datos básicos del libro
            String sqlUpdate = "UPDATE libros SET titulo = ?, isbn = ?, fecha_adquisicion = ?, id_estado = ? WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, libro.getTitulo());
                stmt.setString(2, libro.getIsbn());
                stmt.setDate(3, new java.sql.Date(libro.getFechaAdquisicion().getTime()));
                stmt.setInt(4, libro.getIdEstado());
                stmt.setInt(5, libro.getIdLibro());
                stmt.executeUpdate();
            }

            // 2. Eliminar relaciones existentes
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM libros_autores WHERE id_libro = " + libro.getIdLibro());
                stmt.executeUpdate("DELETE FROM libros_generos WHERE id_libro = " + libro.getIdLibro());
                stmt.executeUpdate("DELETE FROM libros_categorias WHERE id_libro = " + libro.getIdLibro());
            }

            // 3. Insertar nuevas relaciones
            // Autores
            String sqlAutor = "INSERT INTO libros_autores (id_libro, id_autor) VALUES (?, ?)";
            for (Autor autor : libro.getAutores()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlAutor)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, autor.getIdAutor());
                    stmt.executeUpdate();
                }
            }

            // Géneros
            String sqlGenero = "INSERT INTO libros_generos (id_libro, id_genero) VALUES (?, ?)";
            for (Genero genero : libro.getGeneros()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlGenero)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, genero.getIdGenero());
                    stmt.executeUpdate();
                }
            }

            // Categorías
            String sqlCategoria = "INSERT INTO libros_categorias (id_libro, id_categoria) VALUES (?, ?)";
            for (Categoria categoria : libro.getCategorias()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlCategoria)) {
                    stmt.setInt(1, libro.getIdLibro());
                    stmt.setInt(2, categoria.getIdCategoria());
                    stmt.executeUpdate();
                }
            }
        }
    }
   
    // Implementación de métodos para manejar relaciones
    @Override
    public void agregarAutor(Integer idLibro, Integer idAutor) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ADD_AUTOR)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idAutor);
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminarAutor(Integer idLibro, Integer idAutor) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_REMOVE_AUTOR)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idAutor);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Autor> obtenerAutores(Integer idLibro) throws Exception {
        List<Autor> autores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_AUTORES)) {
            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Autor autor = new Autor();
                    autor.setIdAutor(rs.getInt("id_autor"));
                    autor.setNombre(rs.getString("nombre"));
                    autor.setNacionalidad(rs.getString("nacionalidad"));
                    autores.add(autor);
                }
            }
        }
        return autores;
    }

    @Override
    public void agregarGenero(Integer idLibro, Integer idGenero) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ADD_GENERO)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idGenero);
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminarGenero(Integer idLibro, Integer idGenero) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_REMOVE_GENERO)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idGenero);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Genero> obtenerGeneros(Integer idLibro) throws Exception {
        List<Genero> generos = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_GENEROS)) {
            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Genero genero = new Genero();
                    genero.setIdGenero(rs.getInt("id_genero"));
                    genero.setNombre(rs.getString("nombre"));
                    generos.add(genero);
                }
            }
        }
        return generos;
    }

    @Override
    public void agregarCategoria(Integer idLibro, Integer idCategoria) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ADD_CATEGORIA)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idCategoria);
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminarCategoria(Integer idLibro, Integer idCategoria) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_REMOVE_CATEGORIA)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idCategoria);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Categoria> obtenerCategorias(Integer idLibro) throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CATEGORIAS)) {
            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(rs.getInt("id_categoria"));
                    categoria.setNombre(rs.getString("nombre"));
                    categorias.add(categoria);
                }
            }
        }
        return categorias;
    }

    // Implementación de métodos de búsqueda
    @Override
    public List<Libro> buscarPorTitulo(String titulo) throws Exception {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(libro.getIdLibro()));
                    libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                    libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                    libros.add(libro);
                }
            }
        }
        return libros;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) throws Exception {
        Libro libro = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ISBN)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(libro.getIdLibro()));
                    libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                    libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                }
            }
        }
        return libro;
    }

    @Override
    public List<Libro> buscarPorAutor(Integer idAutor) throws Exception {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.* FROM libros l " +
                    "INNER JOIN libros_autores la ON l.id_libro = la.id_libro " +
                    "WHERE la.id_autor = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAutor);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(libro.getIdLibro()));
                    libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                    libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                    libros.add(libro);
                }
            }
        }
        return libros;
    }

    @Override
    public List<Libro> buscarPorGenero(Integer idGenero) throws Exception {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.* FROM libros l " +
                    "INNER JOIN libros_generos lg ON l.id_libro = lg.id_libro " +
                    "WHERE lg.id_genero = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idGenero);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(libro.getIdLibro()));
                    libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                    libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                    libros.add(libro);
                }
            }
        }
        return libros;
    }

    @Override
    public List<Libro> buscarPorCategoria(Integer idCategoria) throws Exception {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.* FROM libros l " +
                    "INNER JOIN libros_categorias lc ON l.id_libro = lc.id_libro " +
                    "WHERE lc.id_categoria = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapearResultSetALibro(rs);
                    libro.setAutores(obtenerAutores(libro.getIdLibro()));
                    libro.setGeneros(obtenerGeneros(libro.getIdLibro()));
                    libro.setCategorias(obtenerCategorias(libro.getIdLibro()));
                    libros.add(libro);
                }
            }
        }
        return libros;
    }

    private Libro mapearResultSetALibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setFechaAdquisicion(rs.getDate("fecha_adquisicion"));
        libro.setIdEstado(rs.getInt("id_estado"));
        return libro;
    }
}