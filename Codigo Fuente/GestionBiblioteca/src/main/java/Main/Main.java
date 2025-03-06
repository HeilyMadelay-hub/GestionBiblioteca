package Main;

// DAO: Interfaces
import DAO.UsuarioDAO;
import DAO.AutorDAO;
import DAO.GeneroDAO;
import DAO.CategoriaDAO;
import DAO.LibroDAO;
import DAO.PrestamoDAO;
import DAO.ContactoDAO;
import DAO.ReservaDAO;
import DAO.LogDAO;

// DAO: Implementaciones
import DAO.IMPL.UsuarioDAOImpl;
import DAO.IMPL.AutorDAOImpl;
import DAO.IMPL.GeneroDAOImpl;
import DAO.IMPL.CategoriaDAOImpl;
import DAO.IMPL.LibroDAOImpl;
import DAO.IMPL.PrestamoDAOImpl;
import DAO.IMPL.ContactoDAOImpl;
import DAO.IMPL.ReservaDAOImpl;
import DAO.IMPL.LogDAOImpl;

// Modelo (Entidades)
import Modelo.Usuario;
import Modelo.Autor;
import Modelo.Genero;
import Modelo.Categoria;
import Modelo.Libro;
import Modelo.Prestamo;
import Modelo.Contacto;
import Modelo.Reserva;
import Modelo.Log;

// Observer
import Observer.LibroNotificador;
import Observer.UsuarioNotificacion;
import Observer.Observer;
import Observer.Observable;

// Servicios
import Servicio.LogServicio;
import Servicio.AutorServicio;
import Servicio.CategoriaServicio;
import Servicio.ContactoServicio;
import Servicio.ReservaServicio;
import Servicio.LibroServicio;
import Servicio.PrestamoServicio;
import Servicio.GeneroServicio;
import Servicio.UsuarioServicio;


// Utilidades
import Util.SecurityUtil;
import Util.DatabaseUtil;
import Util.LogUtil;

// Librerías Java estándar
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

import java.util.Scanner;

import java.util.Scanner;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

public class Main {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioActual = null;
    
    // Servicios
    private static final UsuarioServicio usuarioServicio = new UsuarioServicio(new UsuarioDAOImpl());
    private static final LibroServicio libroServicio;
    private static final AutorServicio autorServicio;
    private static final GeneroServicio generoServicio;
    private static final CategoriaServicio categoriaServicio;
    private static final PrestamoServicio prestamoServicio;
    private static final ReservaServicio reservaServicio;
    
    // Inicialización de servicios
    static {
        // Inicializar DAOs
        LibroDAO libroDAO = new LibroDAOImpl();
        AutorDAO autorDAO = new AutorDAOImpl();
        GeneroDAO generoDAO = new GeneroDAOImpl();
        CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
        LogDAO logDAO = new LogDAOImpl();
        PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
        ReservaDAO reservaDAO = new ReservaDAOImpl();
        
        // Inicializar servicios
        libroServicio = new LibroServicio(libroDAO, autorDAO, generoDAO, categoriaDAO, logDAO);
        autorServicio = new AutorServicio(autorDAO, logDAO);
        generoServicio = new GeneroServicio(generoDAO);
        categoriaServicio = new CategoriaServicio(categoriaDAO, libroServicio); // Inyectar libroServicio
        prestamoServicio = new PrestamoServicio(prestamoDAO);
        reservaServicio = new ReservaServicio(reservaDAO, new UsuarioDAOImpl(), libroDAO, logDAO, libroServicio);
    }
    
    public static void main(String[] args) {
        mostrarPantallaLogin();
    }
    
    private static void mostrarPantallaLogin() {
        while (true) {
            System.out.println("==================================");
            System.out.println("      SISTEMA DE BIBLIOTECA");
            System.out.println("==================================");

            System.out.print("Ingrese su nombre de usuario: ");
            String nombre = scanner.nextLine();
            System.out.print("Ingrese su contraseña: ");
            String password = scanner.nextLine();

            Usuario usuario = null;
            try {
                usuario = usuarioServicio.obtenerUsuarioPorNombre(nombre);
            } catch (Exception e) {
                System.out.println("Error al buscar usuario: " + e.getMessage());
            }

            // Si el usuario no existe o la contraseña no coincide, ofrecer opciones
            if (usuario == null || !SecurityUtil.verifyPassword(password, usuario.getPassword())) {
                System.out.println("Usuario no encontrado o contraseña incorrecta.");
                System.out.print("¿Desea registrarse (R) o volver a intentarlo (VI)?: ");
                String respuesta = scanner.nextLine();

                if (respuesta.equalsIgnoreCase("R")) {
                    registrarNuevoUsuario(nombre, password);
                } else if (respuesta.equalsIgnoreCase("VI")) {
                    System.out.println("Volviendo a la pantalla de inicio...");
                    continue; // Vuelve a pedir usuario y contraseña
                } else {
                    System.out.println("Opción no válida. Saliendo del sistema.");
                    break;
                }
            } else {
                usuarioActual = usuario;
                System.out.println("Bienvenido, " + usuario.getNombre());
                if (usuario.getIdRol() == 1) {
                    mostrarMenuAdministrador();
                } else {
                    mostrarMenuUsuarioRegular();
                }
            }
        }
    }
    
    private static void registrarNuevoUsuario(String nombre, String password) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setPassword(password); 
            nuevoUsuario.setIdRol(2); // Usuario regular
            
            usuarioServicio.registrarUsuario(nuevoUsuario);
            System.out.println("Usuario registrado exitosamente. Inicie sesión nuevamente.");
        } catch (Exception ex) {
            System.out.println("Error registrando usuario: " + ex.getMessage());
        }
    }
    
    private static void mostrarMenuAdministrador() {
        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("           MENÚ ADMINISTRADOR");
            System.out.println("-----------------------------------------");
            System.out.println("1. Gestión de Usuarios");
            System.out.println("2. Gestión de Libros");
            System.out.println("3. Gestión de Préstamos");
            System.out.println("4. Gestión de Reservas");
            System.out.println("5. Gestión de Categorías y Géneros");
            System.out.println("6. Gestión de Autores");
            System.out.println("7. Generación de Reportes");
            System.out.println("8. Cerrar sesión y volver al inicio");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    gestionarUsuarios();
                    break;
                case 2:
                    gestionarLibros();
                    break;
                case 3:
                    gestionarPrestamos();
                    break;
                case 4:
                    gestionarReservas();
                    break;
                case 5:
                    gestionarCategoriasYGeneros();
                    break;
                case 6:
                    gestionarAutores();
                    break;
                case 7:
                    generarReportes();
                    break;
                case 8:
                    System.out.println("Cerrando sesión y volviendo al inicio...\n");
                    usuarioActual = null;
                    return; // Sale del menú y regresa al login
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    private static void mostrarMenuUsuarioRegular() {
        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("         MENÚ USUARIO REGULAR");
            System.out.println("-----------------------------------------");
            System.out.println("1. Buscar Libros");
            System.out.println("2. Realizar Reserva");
            System.out.println("3. Consultar Mis Reservas");
            System.out.println("4. Realizar Préstamo");
            System.out.println("5. Consultar Mis Préstamos");
            System.out.println("6. Devolver Libro");
            System.out.println("7. Mi Perfil");
            System.out.println("8. Cerrar sesión y volver al inicio");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    buscarLibros();
                    break;
                case 2:
                    realizarReserva();
                    break;
                case 3:
                    consultarMisReservas();
                    break;
                case 4:
                    realizarPrestamo();
                    break;
                case 5:
                    consultarMisPrestamos();
                    break;
                case 6:
                    devolverLibro();
                    break;
                case 7:
                    gestionarMiPerfil();
                    break;
                case 8:
                    System.out.println("Cerrando sesión y volviendo al inicio...\n");
                    usuarioActual = null;
                    return; // Sale del menú y regresa al login
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    /* 
     * FUNCIONES DE GESTIÓN PARA ADMINISTRADOR
     */
    
    private static void gestionarUsuarios() {
        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("           GESTIÓN DE USUARIOS");
            System.out.println("-----------------------------------------");
            System.out.println("1. Listar todos los usuarios");
            System.out.println("2. Buscar usuario por ID");
            System.out.println("3. Buscar usuario por nombre");
            System.out.println("4. Registrar nuevo usuario");
            System.out.println("5. Actualizar usuario");
            System.out.println("6. Eliminar usuario");
            System.out.println("7. Volver al menú anterior");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    buscarUsuarioPorId();
                    break;
                case 3:
                    buscarUsuarioPorNombre();
                    break;
                case 4:
                    registrarUsuario();
                    break;
                case 5:
                    actualizarUsuario();
                    break;
                case 6:
                    eliminarUsuario();
                    break;
                case 7:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    // 1. Gestión de Libros
    private static void gestionarLibros() {
        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("           GESTIÓN DE LIBROS");
            System.out.println("-----------------------------------------");
            System.out.println("1. Listar todos los libros");
            System.out.println("2. Buscar libro por ID");
            System.out.println("3. Buscar libros por título");
            System.out.println("4. Buscar libros por autor");
            System.out.println("5. Buscar libros por género");
            System.out.println("6. Registrar nuevo libro");
            System.out.println("7. Actualizar libro");
            System.out.println("8. Eliminar libro");
            System.out.println("9. Volver al menú anterior");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    listarLibros();
                    break;
                case 2:
                    buscarLibroPorId();
                    break;
                case 3:
                    buscarLibrosPorTitulo();
                    break;
                case 4:
                    buscarLibrosPorAutor();
                    break;
                case 5:
                    buscarLibrosPorGenero();
                    break;
                case 6:
                    registrarLibro();
                    break;
                case 7:
                    actualizarLibro();
                    break;
                case 8:
                    eliminarLibro();
                    break;
                case 9:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    private static void listarLibros() {
        try {
            List<Libro> libros = libroServicio.obtenerTodosLibros();
            mostrarLibrosEnTabla(libros);
        } catch (Exception e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }

        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    private static void buscarLibroPorId() {
        System.out.print("Ingrese el ID del libro: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Libro libro = libroServicio.obtenerLibroPorId(id);
            if (libro != null) {
                mostrarDetallesLibro(libro);
            } else {
                System.out.println("No se encontró un libro con el ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error al buscar libro: " + e.getMessage());
        }

        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }



private static void buscarLibrosPorTitulo() {
    System.out.print("Ingrese el título o parte del título: ");
    String titulo = scanner.nextLine();
    try {
        List<Libro> libros = libroServicio.buscarPorTitulo(titulo);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros con el título: " + titulo);
        } else {
            System.out.println("\nLibros encontrados:");
            mostrarLibrosEnTabla(libros);
        }
    } catch (Exception e) {
        System.out.println("Error al buscar libros: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarLibrosPorAutor() {
    System.out.print("Ingrese el ID del autor: ");
    try {
        int idAutor = Integer.parseInt(scanner.nextLine());
        List<Libro> libros = libroServicio.obtenerLibrosPorAutor(idAutor);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros del autor con ID: " + idAutor);
        } else {
            System.out.println("\nLibros del autor:");
            mostrarLibrosEnTabla(libros);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar libros: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarLibrosPorGenero() {
    System.out.print("Ingrese el ID del género: ");
    try {
        int idGenero = Integer.parseInt(scanner.nextLine());
        List<Libro> libros = libroServicio.obtenerLibrosPorGenero(idGenero);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros del género con ID: " + idGenero);
        } else {
            System.out.println("\nLibros del género:");
            mostrarLibrosEnTabla(libros);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar libros: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void registrarLibro() {
    System.out.println("\nRegistrar nuevo libro:");
    System.out.println("--------------------------------------------");
    
    Libro nuevoLibro = new Libro();
    
    System.out.print("Título: ");
    nuevoLibro.setTitulo(scanner.nextLine());
    
    System.out.print("ISBN: ");
    nuevoLibro.setIsbn(scanner.nextLine());
    
    System.out.print("Estado (1=Disponible, 2=Prestado, 3=Reservado, 4=En reparación): ");
    try {
        int estado = Integer.parseInt(scanner.nextLine());
        nuevoLibro.setIdEstado(estado);
    } catch (NumberFormatException e) {
        System.out.println("Valor no válido. Se establecerá como Disponible (1).");
        nuevoLibro.setIdEstado(1);
    }
    
    // Establecer fecha de adquisición como la fecha actual
    nuevoLibro.setFechaAdquisicion(new Date());
    
    // Agregar autores
    List<Autor> autores = new ArrayList<>();
    System.out.println("\nAgregar autores (ingrese 0 para terminar):");
    while (true) {
        System.out.print("ID del autor: ");
        try {
            int idAutor = Integer.parseInt(scanner.nextLine());
            if (idAutor == 0) break;
            
            Autor autor = autorServicio.obtenerAutorPorId(idAutor);
            if (autor != null) {
                autores.add(autor);
                System.out.println("Autor agregado: " + autor.getNombre());
            } else {
                System.out.println("No se encontró el autor con ID: " + idAutor);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error al buscar autor: " + e.getMessage());
        }
    }
    nuevoLibro.setAutores(autores);
    
    // Agregar géneros
    List<Genero> generos = new ArrayList<>();
    System.out.println("\nAgregar géneros (ingrese 0 para terminar):");
    while (true) {
        System.out.print("ID del género: ");
        try {
            int idGenero = Integer.parseInt(scanner.nextLine());
            if (idGenero == 0) break;
            
            Genero genero = generoServicio.obtenerGeneroPorId(idGenero);
            if (genero != null) {
                generos.add(genero);
                System.out.println("Género agregado: " + genero.getNombre());
            } else {
                System.out.println("No se encontró el género con ID: " + idGenero);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error al buscar género: " + e.getMessage());
        }
    }
    nuevoLibro.setGeneros(generos);
    
    // Agregar categorías
    List<Categoria> categorias = new ArrayList<>();
    System.out.println("\nAgregar categorías (ingrese 0 para terminar):");
    while (true) {
        System.out.print("ID de la categoría: ");
        try {
            int idCategoria = Integer.parseInt(scanner.nextLine());
            if (idCategoria == 0) break;
            
            Categoria categoria = categoriaServicio.obtenerPorId(idCategoria);
            if (categoria != null) {
                categorias.add(categoria);
                System.out.println("Categoría agregada: " + categoria.getNombre());
            } else {
                System.out.println("No se encontró la categoría con ID: " + idCategoria);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error al buscar categoría: " + e.getMessage());
        }
    }
    nuevoLibro.setCategorias(categorias);
    
    try {
        libroServicio.registrarLibro(nuevoLibro, usuarioActual.getIdUsuario());
        System.out.println("Libro registrado exitosamente.");
        mostrarDetallesLibro(nuevoLibro);
    } catch (Exception e) {
        System.out.println("Error al registrar libro: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void actualizarLibro() {
    System.out.print("Ingrese el ID del libro a actualizar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Libro libro = libroServicio.obtenerLibroPorId(id);
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        mostrarDetallesLibro(libro);
        System.out.println("\nActualizar libro (deje en blanco para mantener el valor actual):");
        
        System.out.print("Nuevo título [" + libro.getTitulo() + "]: ");
        String titulo = scanner.nextLine();
        if (!titulo.isEmpty()) {
            libro.setTitulo(titulo);
        }
        
        System.out.print("Nuevo ISBN [" + libro.getIsbn() + "]: ");
        String isbn = scanner.nextLine();
        if (!isbn.isEmpty()) {
            libro.setIsbn(isbn);
        }
        
        System.out.print("Nuevo estado (1=Disponible, 2=Prestado, 3=Reservado, 4=En reparación) [" + libro.getIdEstado() + "]: ");
        String estadoStr = scanner.nextLine();
        if (!estadoStr.isEmpty()) {
            try {
                int estado = Integer.parseInt(estadoStr);
                libro.setIdEstado(estado);
            } catch (NumberFormatException e) {
                System.out.println("Valor no válido. Se mantiene el estado actual.");
            }
        }
        
        // Actualizar el libro
        libroServicio.actualizarLibro(libro, usuarioActual.getIdUsuario());
        System.out.println("Libro actualizado exitosamente.");
        mostrarDetallesLibro(libro);
        
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al actualizar libro: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void eliminarLibro() {
    System.out.print("Ingrese el ID del libro a eliminar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Libro libro = libroServicio.obtenerLibroPorId(id);
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        mostrarDetallesLibro(libro);
        System.out.print("¿Está seguro de que desea eliminar este libro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            libroServicio.eliminarLibro(id, usuarioActual.getIdUsuario());
            System.out.println("Libro eliminado exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al eliminar libro: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void mostrarDetallesLibro(Libro libro) {
    System.out.println("\nDetalles del libro:");
    System.out.println("--------------------------------------------");
    System.out.println("ID: " + libro.getIdLibro());
    System.out.println("Título: " + libro.getTitulo());
    System.out.println("ISBN: " + libro.getIsbn());
    System.out.println("Estado: " + obtenerNombreEstadoLibro(libro.getIdEstado()));
    
    // Fecha de adquisición
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    System.out.println("Fecha de adquisición: " + sdf.format(libro.getFechaAdquisicion()));
    
    // Mostrar autores
    System.out.println("\nAutores:");
    if (libro.getAutores() == null || libro.getAutores().isEmpty()) {
        System.out.println("No hay autores asociados.");
    } else {
        for (Autor autor : libro.getAutores()) {
            System.out.println("- " + autor.getNombre() + " (ID: " + autor.getIdAutor() + ")");
        }
    }
    
    // Mostrar géneros
    System.out.println("\nGéneros:");
    if (libro.getGeneros() == null || libro.getGeneros().isEmpty()) {
        System.out.println("No hay géneros asociados.");
    } else {
        for (Genero genero : libro.getGeneros()) {
            System.out.println("- " + genero.getNombre() + " (ID: " + genero.getIdGenero() + ")");
        }
    }
    
    // Mostrar categorías
    System.out.println("\nCategorías:");
    if (libro.getCategorias() == null || libro.getCategorias().isEmpty()) {
        System.out.println("No hay categorías asociadas.");
    } else {
        for (Categoria categoria : libro.getCategorias()) {
            System.out.println("- " + categoria.getNombre() + " (ID: " + categoria.getIdCategoria() + ")");
        }
    }
    
    System.out.println("--------------------------------------------");
}

private static void mostrarLibrosEnTabla(List<Libro> libros) {
    if (libros.isEmpty()) {
        System.out.println("No hay libros para mostrar.");
        return;
    }
    
    System.out.println("\nLista de libros:");
    System.out.println("--------------------------------------------");
    System.out.printf("%-5s | %-30s | %-15s | %-15s\n", "ID", "Título", "ISBN", "Estado");
    System.out.println("--------------------------------------------");
    
    for (Libro libro : libros) {
        System.out.printf("%-5d | %-30s | %-15s | %-15s\n",
                libro.getIdLibro(),
                libro.getTitulo(),
                libro.getIsbn(),
                obtenerNombreEstadoLibro(libro.getIdEstado()));
    }
    
    System.out.println("--------------------------------------------");
    System.out.println("Total: " + libros.size() + " libros");
}

// 2. Gestión de Préstamos
private static void gestionarPrestamos() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           GESTIÓN DE PRÉSTAMOS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Listar todos los préstamos");
        System.out.println("2. Listar préstamos activos");
        System.out.println("3. Buscar préstamo por ID");
        System.out.println("4. Buscar préstamos por usuario");
        System.out.println("5. Buscar préstamos por libro");
        System.out.println("6. Registrar nuevo préstamo");
        System.out.println("7. Registrar devolución");
        System.out.println("8. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                listarTodosPrestamos();
                break;
            case 2:
                listarPrestamosActivos();
                break;
            case 3:
                buscarPrestamoPorId();
                break;
            case 4:
                buscarPrestamosPorUsuario();
                break;
            case 5:
                buscarPrestamosPorLibro();
                break;
            case 6:
                registrarPrestamo();
                break;
            case 7:
                registrarDevolucion();
                break;
            case 8:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void listarTodosPrestamos() {
    try {
        List<Prestamo> prestamos = prestamoServicio.obtenerTodosPrestamos();
        mostrarPrestamosEnTabla(prestamos);
    } catch (Exception e) {
        System.out.println("Error al listar préstamos: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void listarPrestamosActivos() {
    try {
        List<Prestamo> prestamosActivos = prestamoServicio.obtenerPrestamosActivos();
        mostrarPrestamosEnTabla(prestamosActivos);
    } catch (Exception e) {
        System.out.println("Error al listar préstamos activos: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarPrestamoPorId() {
    System.out.print("Ingrese el ID del préstamo: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Prestamo prestamo = prestamoServicio.obtenerPrestamoPorId(id);
        if (prestamo != null) {
            mostrarDetallesPrestamo(prestamo);
        } else {
            System.out.println("No se encontró un préstamo con el ID: " + id);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar préstamo: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}



private static void registrarPrestamo() {
    Prestamo nuevoPrestamo = new Prestamo();
    
    // Usuario
    System.out.print("ID del usuario: ");
    try {
        int idUsuario = Integer.parseInt(scanner.nextLine());
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
        if (usuario == null) {
            System.out.println("No se encontró un usuario con el ID: " + idUsuario);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        nuevoPrestamo.setIdUsuario(idUsuario);
        nuevoPrestamo.setUsuario(usuario);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    } catch (Exception e) {
        System.out.println("Error al buscar usuario: " + e.getMessage());
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    // Libro
    System.out.print("ID del libro: ");
    try {
        int idLibro = Integer.parseInt(scanner.nextLine());
        Libro libro = libroServicio.obtenerLibroPorId(idLibro);
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + idLibro);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        // Verificar que el libro esté disponible
        if (libro.getIdEstado() != 1) {
            System.out.println("El libro no está disponible para préstamo.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        nuevoPrestamo.setIdLibro(idLibro);
        nuevoPrestamo.setLibro(libro);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    } catch (Exception e) {
        System.out.println("Error al buscar libro: " + e.getMessage());
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    // Tipo de préstamo
    System.out.print("Tipo de préstamo (1=Normal, 2=Express): ");
    try {
        int tipoPrestamo = Integer.parseInt(scanner.nextLine());
        nuevoPrestamo.setIdTipoPrestamo(tipoPrestamo);
    } catch (NumberFormatException e) {
        System.out.println("Valor no válido. Se establecerá como Normal (1).");
        nuevoPrestamo.setIdTipoPrestamo(1);
    }
    
    // Fecha de préstamo (hoy)
    nuevoPrestamo.setFechaPrestamo(new Date());
    
    // Fecha de devolución esperada
    System.out.print("Días para devolución: ");
    try {
        int dias = Integer.parseInt(scanner.nextLine());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nuevoPrestamo.getFechaPrestamo());
        calendar.add(Calendar.DAY_OF_MONTH, dias);
        nuevoPrestamo.setFechaDevolucionEsperada(calendar.getTime());
    } catch (NumberFormatException e) {
        System.out.println("Valor no válido. Se establecerá en 7 días.");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nuevoPrestamo.getFechaPrestamo());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        nuevoPrestamo.setFechaDevolucionEsperada(calendar.getTime());
    }
    
    try {
        prestamoServicio.registrarPrestamo(nuevoPrestamo);
        // Cambiar estado del libro a prestado (2)
        Libro libro = nuevoPrestamo.getLibro();
        libro.setIdEstado(2);
        libroServicio.actualizarEstadoLibro(libro.getIdLibro(), 2, usuarioActual.getIdUsuario());
        System.out.println("Préstamo registrado exitosamente.");
        mostrarDetallesPrestamo(nuevoPrestamo);
    } catch (Exception e) {
        System.out.println("Error al registrar préstamo: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void registrarDevolucion() {
    System.out.print("Ingrese el ID del préstamo a devolver: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Prestamo prestamo = prestamoServicio.obtenerPrestamoPorId(id);
        if (prestamo == null) {
            System.out.println("No se encontró un préstamo con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        if (prestamo.getFechaDevolucionReal() != null) {
            System.out.println("Este préstamo ya ha sido devuelto.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        mostrarDetallesPrestamo(prestamo);
        System.out.print("¿Confirmar devolución del libro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            Date fechaDevolucion = new Date(); // Fecha actual
            prestamoServicio.registrarDevolucion(id, fechaDevolucion);
            
            // Cambiar estado del libro a disponible (1)
            libroServicio.actualizarEstadoLibro(prestamo.getIdLibro(), 1, usuarioActual.getIdUsuario());
            
            System.out.println("Devolución registrada exitosamente.");
            
            // Verificar si es un préstamo express (menos de 7 días)
            long diferenciaDias = (fechaDevolucion.getTime() - prestamo.getFechaPrestamo().getTime()) / (1000 * 60 * 60 * 24);
            if (diferenciaDias < 7) {
                System.out.println("¡Préstamo Express! Devuelto en menos de 7 días.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al registrar devolución: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void mostrarDetallesPrestamo(Prestamo prestamo) {
    System.out.println("\nDetalles del préstamo:");
    System.out.println("--------------------------------------------");
    System.out.println("ID: " + prestamo.getIdPrestamo());
    
    // Usuario
    String nombreUsuario = "ID: " + prestamo.getIdUsuario();
    if (prestamo.getUsuario() != null) {
        nombreUsuario = prestamo.getUsuario().getNombre();
    }
    System.out.println("Usuario: " + nombreUsuario);
    
    // Libro
    String tituloLibro = "ID: " + prestamo.getIdLibro();
    if (prestamo.getLibro() != null) {
        tituloLibro = prestamo.getLibro().getTitulo();
    }
    System.out.println("Libro: " + tituloLibro);
    
    // Tipo de préstamo
    System.out.println("Tipo: " + (prestamo.getIdTipoPrestamo() == 1 ? "Normal" : "Express"));
    
    // Fechas
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    System.out.println("Fecha de préstamo: " + sdf.format(prestamo.getFechaPrestamo()));
    System.out.println("Fecha de devolución esperada: " + sdf.format(prestamo.getFechaDevolucionEsperada()));
    
    if (prestamo.getFechaDevolucionReal() != null) {
        System.out.println("Fecha de devolución real: " + sdf.format(prestamo.getFechaDevolucionReal()));
        
        // Calcular días reales
        long diferenciaDias = (prestamo.getFechaDevolucionReal().getTime() - prestamo.getFechaPrestamo().getTime()) / (1000 * 60 * 60 * 24);
        System.out.println("Días de préstamo: " + diferenciaDias);
        
        // Verificar si fue entregado a tiempo
        if (prestamo.getFechaDevolucionReal().after(prestamo.getFechaDevolucionEsperada())) {
            System.out.println("Estado: Devuelto con retraso");
        } else {
            System.out.println("Estado: Devuelto a tiempo");
        }
    } else {
        System.out.println("Estado: Préstamo activo");
        
        // Verificar si está vencido
        Date hoy = new Date();
        if (hoy.after(prestamo.getFechaDevolucionEsperada())) {
            long diasRetraso = (hoy.getTime() - prestamo.getFechaDevolucionEsperada().getTime()) / (1000 * 60 * 60 * 24);
            System.out.println("¡ATENCIÓN! Préstamo vencido por " + diasRetraso + " días.");
        }
    }
    
    System.out.println("--------------------------------------------");
}

private static void mostrarPrestamosEnTabla(List<Prestamo> prestamos) {
    if (prestamos.isEmpty()) {
        System.out.println("No hay préstamos para mostrar.");
        return;
    }
    
    System.out.println("\nLista de préstamos:");
    System.out.println("-------------------------------------------------------------------------------------");
    System.out.printf("%-5s | %-20s | %-30s | %-15s | %-15s | %-10s\n", 
            "ID", "Usuario", "Libro", "Fecha Préstamo", "Fecha Devolución", "Estado");
    System.out.println("-------------------------------------------------------------------------------------");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date hoy = new Date();
    
    for (Prestamo prestamo : prestamos) {
        String nombreUsuario = "ID: " + prestamo.getIdUsuario();
        if (prestamo.getUsuario() != null) {
            nombreUsuario = prestamo.getUsuario().getNombre();
        }
        
        String tituloLibro = "ID: " + prestamo.getIdLibro();
        if (prestamo.getLibro() != null) {
            tituloLibro = prestamo.getLibro().getTitulo();
        }
        
        String estado;
        if (prestamo.getFechaDevolucionReal() != null) {
            if (prestamo.getFechaDevolucionReal().after(prestamo.getFechaDevolucionEsperada())) {
                estado = "Retraso";
            } else {
                estado = "Devuelto";
            }
        } else if (hoy.after(prestamo.getFechaDevolucionEsperada())) {
            estado = "Vencido";
        } else {
            estado = "Activo";
        }
        
        System.out.printf("%-5d | %-20s | %-30s | %-15s | %-15s | %-10s\n",
                prestamo.getIdPrestamo(),
                nombreUsuario,
                tituloLibro,
                sdf.format(prestamo.getFechaPrestamo()),
                sdf.format(prestamo.getFechaDevolucionEsperada()),
                estado);
    }
    
    System.out.println("-------------------------------------------------------------------------------------");
    System.out.println("Total: " + prestamos.size() + " préstamos");
}

// 3. Gestión de Reservas
private static void gestionarReservas() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           GESTIÓN DE RESERVAS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Listar todas las reservas");
        System.out.println("2. Listar reservas pendientes");
        System.out.println("3. Listar reservas vencidas");
        System.out.println("4. Buscar reserva por ID");
        System.out.println("5. Buscar reservas por usuario");
        System.out.println("6. Buscar reservas por libro");
        System.out.println("7. Registrar nueva reserva");
        System.out.println("8. Cambiar estado de reserva");
        System.out.println("9. Cancelar reserva");
        System.out.println("10. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                listarTodasReservas();
                break;
            case 2:
                listarReservasPendientes();
                break;
            case 3:
                listarReservasVencidas();
                break;
            case 4:
                buscarReservaPorId();
                break;
            case 5:
                buscarReservasPorUsuario();
                break;
            case 6:
                buscarReservasPorLibro();
                break;
            case 7:
                registrarReserva();
                break;
            case 8:
                cambiarEstadoReserva();
                break;
            case 9:
                cancelarReserva();
                break;
            case 10:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void listarTodasReservas() {
    try {
        List<Reserva> reservas = reservaServicio.obtenerTodasReservas();
        mostrarReservasEnTabla(reservas);
    } catch (Exception e) {
        System.out.println("Error al listar reservas: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void listarReservasPendientes() {
    try {
        List<Reserva> reservasPendientes = reservaServicio.obtenerReservasPendientes();
        mostrarReservasEnTabla(reservasPendientes);
    } catch (Exception e) {
        System.out.println("Error al listar reservas pendientes: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void listarReservasVencidas() {
    try {
        List<Reserva> reservasVencidas = reservaServicio.obtenerReservasVencidas();
        mostrarReservasEnTabla(reservasVencidas);
    } catch (Exception e) {
        System.out.println("Error al listar reservas vencidas: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}


private static void buscarReservasPorUsuario() {
    System.out.print("Ingrese el ID del usuario: ");
    try {
        int idUsuario = Integer.parseInt(scanner.nextLine());
        List<Reserva> reservasUsuario = reservaServicio.obtenerReservasPorUsuario(idUsuario);
        mostrarReservasEnTabla(reservasUsuario);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar reservas del usuario: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarReservasPorLibro() {
    System.out.print("Ingrese el ID del libro: ");
    try {
        int idLibro = Integer.parseInt(scanner.nextLine());
        List<Reserva> reservasLibro = reservaServicio.obtenerReservasPorLibro(idLibro);
        mostrarReservasEnTabla(reservasLibro);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar reservas del libro: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void registrarReserva() {
    Reserva nuevaReserva = new Reserva();
    
    // Usuario
    System.out.print("ID del usuario: ");
    try {
        int idUsuario = Integer.parseInt(scanner.nextLine());
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
        if (usuario == null) {
            System.out.println("No se encontró un usuario con el ID: " + idUsuario);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        nuevaReserva.setIdUsuario(idUsuario);
        nuevaReserva.setUsuario(usuario);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    } catch (Exception e) {
        System.out.println("Error al buscar usuario: " + e.getMessage());
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    // Libro
    System.out.print("ID del libro: ");
    try {
        int idLibro = Integer.parseInt(scanner.nextLine());
        Libro libro = libroServicio.obtenerLibroPorId(idLibro);
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + idLibro);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Verificar si el usuario ya tiene una reserva activa para este libro
        if (reservaServicio.tieneReservaActiva(nuevaReserva.getIdUsuario(), idLibro)) {
            System.out.println("El usuario ya tiene una reserva activa para este libro.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        nuevaReserva.setIdLibro(idLibro);
        nuevaReserva.setLibro(libro);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    } catch (Exception e) {
        System.out.println("Error al buscar libro: " + e.getMessage());
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    // Estado de reserva (por defecto 1 = pendiente)
    nuevaReserva.setIdEstadoReserva(1);
    
    // Fecha de reserva (hoy)
    nuevaReserva.setFechaReserva(new Date());
    
    // Fecha de vencimiento (7 días después)
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(nuevaReserva.getFechaReserva());
    calendar.add(Calendar.DAY_OF_MONTH, 7);
    nuevaReserva.setFechaVencimiento(calendar.getTime());
    
    try {
        reservaServicio.registrarReserva(nuevaReserva, usuarioActual.getIdUsuario());
        System.out.println("Reserva registrada exitosamente.");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("\nDetalles de la reserva:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + nuevaReserva.getIdReserva());
        System.out.println("Usuario: " + nuevaReserva.getUsuario().getNombre());
        System.out.println("Libro: " + nuevaReserva.getLibro().getTitulo());
        System.out.println("Estado: Pendiente");
        System.out.println("Fecha de reserva: " + sdf.format(nuevaReserva.getFechaReserva()));
        System.out.println("Fecha de vencimiento: " + sdf.format(nuevaReserva.getFechaVencimiento()));
        System.out.println("--------------------------------------------");
    } catch (Exception e) {
        System.out.println("Error al registrar reserva: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void cambiarEstadoReserva() {
    System.out.print("Ingrese el ID de la reserva: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        
        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. Pendiente");
        System.out.println("2. Completada");
        System.out.println("3. Vencida");
        System.out.println("4. Cancelada");
        System.out.print("Opción: ");
        
        int nuevoEstado = Integer.parseInt(scanner.nextLine());
        if (nuevoEstado < 1 || nuevoEstado > 4) {
            System.out.println("Estado no válido.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        reservaServicio.cambiarEstadoReserva(id, nuevoEstado, usuarioActual.getIdUsuario());
        System.out.println("Estado de reserva actualizado exitosamente.");
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al cambiar estado de reserva: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void cancelarReserva() {
    System.out.print("Ingrese el ID de la reserva a cancelar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        
        System.out.print("¿Está seguro de que desea cancelar esta reserva? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            reservaServicio.eliminarReserva(id, usuarioActual.getIdUsuario());
            System.out.println("Reserva cancelada exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al cancelar reserva: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void mostrarReservasEnTabla(List<Reserva> reservas) {
    if (reservas.isEmpty()) {
        System.out.println("No hay reservas para mostrar.");
        return;
    }
    
    System.out.println("\nLista de reservas:");
    System.out.println("-------------------------------------------------------------------------------------");
    System.out.printf("%-5s | %-20s | %-30s | %-15s | %-15s | %-10s\n", 
            "ID", "Usuario", "Libro", "Fecha Reserva", "Vencimiento", "Estado");
    System.out.println("-------------------------------------------------------------------------------------");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    for (Reserva reserva : reservas) {
        String nombreUsuario = "ID: " + reserva.getIdUsuario();
        if (reserva.getUsuario() != null) {
            nombreUsuario = reserva.getUsuario().getNombre();
        }
        
        String tituloLibro = "ID: " + reserva.getIdLibro();
        if (reserva.getLibro() != null) {
            tituloLibro = reserva.getLibro().getTitulo();
        }
        
        String estado;
        switch (reserva.getIdEstadoReserva()) {
            case 1: estado = "Pendiente"; break;
            case 2: estado = "Completada"; break;
            case 3: estado = "Vencida"; break;
            case 4: estado = "Cancelada"; break;
            default: estado = "Desconocido";
        }
        
        System.out.printf("%-5d | %-20s | %-30s | %-15s | %-15s | %-10s\n",
                reserva.getIdReserva(),
                nombreUsuario,
                tituloLibro,
                sdf.format(reserva.getFechaReserva()),
                sdf.format(reserva.getFechaVencimiento()),
                estado);
    }
    
    System.out.println("-------------------------------------------------------------------------------------");
    System.out.println("Total: " + reservas.size() + " reservas");
}

// 4. Gestión de Categorías y Géneros
private static void gestionarCategoriasYGeneros() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("      GESTIÓN DE CATEGORÍAS Y GÉNEROS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Gestionar Categorías");
        System.out.println("2. Gestionar Géneros");
        System.out.println("3. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                gestionarCategorias();
                break;
            case 2:
                gestionarGeneros();
                break;
            case 3:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void gestionarCategorias() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           GESTIÓN DE CATEGORÍAS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Listar todas las categorías");
        System.out.println("2. Buscar categoría por ID");
        System.out.println("3. Agregar nueva categoría");
        System.out.println("4. Actualizar categoría");
        System.out.println("5. Eliminar categoría");
        System.out.println("6. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                listarCategorias();
                break;
            case 2:
                buscarCategoriaPorId();
                break;
            case 3:
                agregarCategoria();
                break;
            case 4:
                actualizarCategoria();
                break;
            case 5:
                eliminarCategoria();
                break;
            case 6:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void listarCategorias() {
    try {
        List<Categoria> categorias = categoriaServicio.obtenerTodos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías para mostrar.");
        } else {
            System.out.println("\nLista de categorías:");
            System.out.println("--------------------------------------------");
            System.out.printf("%-5s | %-30s\n", "ID", "Nombre");
            System.out.println("--------------------------------------------");
            
            for (Categoria categoria : categorias) {
                System.out.printf("%-5d | %-30s\n",
                        categoria.getIdCategoria(),
                        categoria.getNombre());
            }
            
            System.out.println("--------------------------------------------");
            System.out.println("Total: " + categorias.size() + " categorías");
        }
    } catch (Exception e) {
        System.out.println("Error al listar categorías: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarCategoriaPorId() {
    System.out.print("Ingrese el ID de la categoría: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Categoria categoria = categoriaServicio.obtenerPorId(id);
        if (categoria != null) {
            System.out.println("\nDetalles de la categoría:");
            System.out.println("--------------------------------------------");
            System.out.println("ID: " + categoria.getIdCategoria());
            System.out.println("Nombre: " + categoria.getNombre());
            System.out.println("--------------------------------------------");
        } else {
            System.out.println("No se encontró una categoría con el ID: " + id);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar categoría: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void agregarCategoria() {
    System.out.print("Ingrese el nombre de la nueva categoría: ");
    String nombre = scanner.nextLine();
    
    if (nombre.trim().isEmpty()) {
        System.out.println("El nombre no puede estar vacío.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    categoriaServicio.agregarCategoria(nombre);
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void actualizarCategoria() {
    System.out.print("Ingrese el ID de la categoría a actualizar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Categoria categoria = categoriaServicio.obtenerPorId(id);
        if (categoria == null) {
            System.out.println("No se encontró una categoría con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Categoría actual: " + categoria.getNombre());
        System.out.print("Ingrese el nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();
        
        if (nuevoNombre.trim().isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        categoriaServicio.actualizarCategoria(id, nuevoNombre);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void eliminarCategoria() {
    System.out.print("Ingrese el ID de la categoría a eliminar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Categoria categoria = categoriaServicio.obtenerPorId(id);
        if (categoria == null) {
            System.out.println("No se encontró una categoría con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Categoría a eliminar: " + categoria.getNombre());
        System.out.print("¿Está seguro de que desea eliminar esta categoría? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            categoriaServicio.eliminarCategoria(id);
            System.out.println("Categoría eliminada exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void gestionarGeneros() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           GESTIÓN DE GÉNEROS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Listar todos los géneros");
        System.out.println("2. Buscar género por ID");
        System.out.println("3. Registrar nuevo género");
        System.out.println("4. Actualizar género");
        System.out.println("5. Eliminar género");
        System.out.println("6. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                listarGeneros();
                break;
            case 2:
                buscarGeneroPorId();
                break;
            case 3:
                registrarGenero();
                break;
            case 4:
                actualizarGenero();
                break;
            case 5:
                eliminarGenero();
                break;
            case 6:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void listarGeneros() {
    try {
        generoServicio.obtenerTodosGeneros();
    } catch (Exception e) {
        System.out.println("Error al listar géneros: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarGeneroPorId() {
    System.out.print("Ingrese el ID del género: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Genero genero = generoServicio.obtenerGeneroPorId(id);
        if (genero != null) {
            System.out.println("\nDetalles del género:");
            System.out.println("--------------------------------------------");
            System.out.println("ID: " + genero.getIdGenero());
            System.out.println("Nombre: " + genero.getNombre());
            System.out.println("--------------------------------------------");
        } else {
            System.out.println("No se encontró un género con el ID: " + id);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar género: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void registrarGenero() {
    System.out.print("Ingrese el nombre del nuevo género: ");
    String nombre = scanner.nextLine();
    
    if (nombre.trim().isEmpty()) {
        System.out.println("El nombre no puede estar vacío.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    Genero nuevoGenero = new Genero();
    nuevoGenero.setNombre(nombre);
    
    try {
        generoServicio.registrarGenero(nuevoGenero);
    } catch (Exception e) {
        System.out.println("Error al registrar género: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void actualizarGenero() {
    System.out.print("Ingrese el ID del género a actualizar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Genero genero = generoServicio.obtenerGeneroPorId(id);
        if (genero == null) {
            System.out.println("No se encontró un género con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Género actual: " + genero.getNombre());
        System.out.print("Ingrese el nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();
        
        if (nuevoNombre.trim().isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        genero.setNombre(nuevoNombre);
        generoServicio.actualizarGenero(genero);
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al actualizar género: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void eliminarGenero() {
    System.out.print("Ingrese el ID del género a eliminar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Genero genero = generoServicio.obtenerGeneroPorId(id);
        if (genero == null) {
            System.out.println("No se encontró un género con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Género a eliminar: " + genero.getNombre());
        System.out.print("¿Está seguro de que desea eliminar este género? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            generoServicio.eliminarGenero(id);
            System.out.println("Género eliminado exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al eliminar género: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 5. Gestión de Autores
private static void gestionarAutores() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           GESTIÓN DE AUTORES");
        System.out.println("-----------------------------------------");
        System.out.println("1. Listar todos los autores");
        System.out.println("2. Buscar autor por ID");
        System.out.println("3. Registrar nuevo autor");
        System.out.println("4. Actualizar autor");
        System.out.println("5. Eliminar autor");
        System.out.println("6. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            continue;
        }

        switch (opcion) {
            case 1:
                listarAutores();
                break;
            case 2:
                buscarAutorPorId();
                break;
            case 3:
                registrarAutor();
                break;
            case 4:
                actualizarAutor();
                break;
            case 5:
                eliminarAutor();
                break;
            case 6:
                return; // Volver al menú anterior
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
        }
    }
}

private static void listarAutores() {
    try {
        List<Autor> autores = autorServicio.obtenerTodosAutores();
        if (autores.isEmpty()) {
            System.out.println("No hay autores para mostrar.");
        } else {
            System.out.println("\nLista de autores:");
            System.out.println("--------------------------------------------");
            System.out.printf("%-5s | %-30s | %-20s\n", "ID", "Nombre", "Nacionalidad");
            System.out.println("--------------------------------------------");
            
            for (Autor autor : autores) {
                System.out.printf("%-5d | %-30s | %-20s\n",
                        autor.getIdAutor(),
                        autor.getNombre(),
                        autor.getNacionalidad() == null ? "-" : autor.getNacionalidad());
            }
            
            System.out.println("--------------------------------------------");
            System.out.println("Total: " + autores.size() + " autores");
        }
    } catch (Exception e) {
        System.out.println("Error al listar autores: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void buscarAutorPorId() {
    System.out.print("Ingrese el ID del autor: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Autor autor = autorServicio.obtenerAutorPorId(id);
        if (autor != null) {
            System.out.println("\nDetalles del autor:");
            System.out.println("--------------------------------------------");
            System.out.println("ID: " + autor.getIdAutor());
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Nacionalidad: " + (autor.getNacionalidad() == null ? "-" : autor.getNacionalidad()));
            System.out.println("--------------------------------------------");
        } else {
            System.out.println("No se encontró un autor con el ID: " + id);
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar autor: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void registrarAutor() {
    System.out.println("\nRegistrar nuevo autor:");
    System.out.println("--------------------------------------------");
    
    Autor nuevoAutor = new Autor();
    
    System.out.print("Nombre: ");
    String nombre = scanner.nextLine();
    if (nombre.trim().isEmpty()) {
        System.out.println("El nombre no puede estar vacío.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    nuevoAutor.setNombre(nombre);
    
    System.out.print("Nacionalidad (opcional): ");
    String nacionalidad = scanner.nextLine();
    if (!nacionalidad.trim().isEmpty()) {
        nuevoAutor.setNacionalidad(nacionalidad);
    }
    
    try {
        autorServicio.registrarAutor(nuevoAutor, usuarioActual.getIdUsuario());
        System.out.println("Autor registrado exitosamente.");
        System.out.println("\nDetalles del autor:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + nuevoAutor.getIdAutor());
        System.out.println("Nombre: " + nuevoAutor.getNombre());
        System.out.println("Nacionalidad: " + (nuevoAutor.getNacionalidad() == null ? "-" : nuevoAutor.getNacionalidad()));
        System.out.println("--------------------------------------------");
    } catch (Exception e) {
        System.out.println("Error al registrar autor: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void actualizarAutor() {
    System.out.print("Ingrese el ID del autor a actualizar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Autor autor = autorServicio.obtenerAutorPorId(id);
        if (autor == null) {
            System.out.println("No se encontró un autor con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nActualizar autor (deje en blanco para mantener el valor actual):");
        System.out.println("Autor actual: " + autor.getNombre());
        System.out.println("Nacionalidad actual: " + (autor.getNacionalidad() == null ? "-" : autor.getNacionalidad()));
        
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();
        if (!nuevoNombre.trim().isEmpty()) {
            autor.setNombre(nuevoNombre);
        }
        
        System.out.print("Nueva nacionalidad: ");
        String nuevaNacionalidad = scanner.nextLine();
        if (!nuevaNacionalidad.trim().isEmpty()) {
            autor.setNacionalidad(nuevaNacionalidad);
        }
        
        autorServicio.actualizarAutor(autor, usuarioActual.getIdUsuario());
        System.out.println("Autor actualizado exitosamente.");
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al actualizar autor: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void eliminarAutor() {
    System.out.print("Ingrese el ID del autor a eliminar: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Autor autor = autorServicio.obtenerAutorPorId(id);
        if (autor == null) {
            System.out.println("No se encontró un autor con el ID: " + id);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Autor a eliminar: " + autor.getNombre());
        System.out.print("¿Está seguro de que desea eliminar este autor? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            autorServicio.eliminarAutor(id, usuarioActual.getIdUsuario());
            System.out.println("Autor eliminado exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al eliminar autor: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}
    
    
    
      private static void generarReportes() {
        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("          GENERACIÓN DE REPORTES");
            System.out.println("-----------------------------------------");
            System.out.println("1. Reporte de Préstamos Activos");
            System.out.println("2. Reporte de Reservas Pendientes");
            System.out.println("3. Reporte de Libros Más Prestados");
            System.out.println("4. Reporte de Usuarios con Multas");
            System.out.println("5. Reporte de Libros por Categoría");
            System.out.println("6. Reporte de Actividad de Usuarios");
            System.out.println("7. Reporte de Logs del Sistema");
            System.out.println("8. Volver al menú anterior");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    generarReportePrestamosActivos();
                    break;
                case 2:
                    generarReporteReservasPendientes();
                    break;
                case 3:
                    generarReporteLibrosMasPrestados();
                    break;
                case 4:
                    generarReporteUsuariosConMultas();
                    break;
                case 5:
                    generarReporteLibrosPorCategoria();
                    break;
                case 6:
                    generarReporteActividadUsuarios();
                    break;
                case 7:
                    generarReporteLogsSistema();
                    break;
                case 8:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    /**
     * Genera un reporte de todos los préstamos activos en el sistema.
     */
    private static void generarReportePrestamosActivos() {
        System.out.println("\n===========================================");
        System.out.println("      REPORTE DE PRÉSTAMOS ACTIVOS");
        System.out.println("===========================================");
        
        try {
            List<Prestamo> prestamosActivos = prestamoServicio.obtenerPrestamosActivos();
            
            if (prestamosActivos.isEmpty()) {
                System.out.println("No hay préstamos activos en el sistema.");
                return;
            }
            
            System.out.printf("%-5s | %-20s | %-30s | %-15s | %-15s\n", 
                    "ID", "Usuario", "Libro", "Fecha Préstamo", "Fecha Devolución");
            System.out.println("-------------------------------------------------------------------------------------");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Prestamo prestamo : prestamosActivos) {
                String nombreUsuario = "ID: " + prestamo.getIdUsuario();
                String tituloLibro = "ID: " + prestamo.getIdLibro();
                
                // Si el préstamo tiene objetos Usuario y Libro cargados
                if (prestamo.getUsuario() != null) {
                    nombreUsuario = prestamo.getUsuario().getNombre();
                }
                
                if (prestamo.getLibro() != null) {
                    tituloLibro = prestamo.getLibro().getTitulo();
                }
                
                System.out.printf("%-5d | %-20s | %-30s | %-15s | %-15s\n",
                        prestamo.getIdPrestamo(),
                        nombreUsuario,
                        tituloLibro,
                        sdf.format(prestamo.getFechaPrestamo()),
                        sdf.format(prestamo.getFechaDevolucionEsperada()));
            }
            
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Total de préstamos activos: " + prestamosActivos.size());
            
        } catch (Exception e) {
            System.out.println("Error al generar el reporte de préstamos activos: " + e.getMessage());
        }
        
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Genera un reporte de todas las reservas pendientes en el sistema.
     */
    private static void generarReporteReservasPendientes() {
        System.out.println("\n===========================================");
        System.out.println("      REPORTE DE RESERVAS PENDIENTES");
        System.out.println("===========================================");
        
        try {
            List<Reserva> reservasPendientes = reservaServicio.obtenerReservasPendientes();
            
            if (reservasPendientes.isEmpty()) {
                System.out.println("No hay reservas pendientes en el sistema.");
                return;
            }
            
            System.out.printf("%-5s | %-20s | %-30s | %-15s | %-15s\n", 
                    "ID", "Usuario", "Libro", "Fecha Reserva", "Vencimiento");
            System.out.println("-------------------------------------------------------------------------------------");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Reserva reserva : reservasPendientes) {
                String nombreUsuario = "ID: " + reserva.getIdUsuario();
                String tituloLibro = "ID: " + reserva.getIdLibro();
                
                // Si la reserva tiene objetos Usuario y Libro cargados
                if (reserva.getUsuario() != null) {
                    nombreUsuario = reserva.getUsuario().getNombre();
                }
                
                if (reserva.getLibro() != null) {
                    tituloLibro = reserva.getLibro().getTitulo();
                }
                
                System.out.printf("%-5d | %-20s | %-30s | %-15s | %-15s\n",
                        reserva.getIdReserva(),
                        nombreUsuario,
                        tituloLibro,
                        sdf.format(reserva.getFechaReserva()),
                        sdf.format(reserva.getFechaVencimiento()));
            }
            
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Total de reservas pendientes: " + reservasPendientes.size());
            
        } catch (Exception e) {
            System.out.println("Error al generar el reporte de reservas pendientes: " + e.getMessage());
        }
        
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
   
    
    /**
     * Genera un reporte de usuarios con multas.
     */
    private static void generarReporteUsuariosConMultas() {
        System.out.println("\n===========================================");
        System.out.println("      REPORTE DE USUARIOS CON MULTAS");
        System.out.println("===========================================");
        
        try {
            List<Usuario> todosUsuarios = usuarioServicio.obtenerTodos();
            List<Usuario> usuariosConMultas = new ArrayList<>();
            
            // Filtrar usuarios con multas > 0
            for (Usuario usuario : todosUsuarios) {
                if (usuario.getMulta() > 0) {
                    usuariosConMultas.add(usuario);
                }
            }
            
            if (usuariosConMultas.isEmpty()) {
                System.out.println("No hay usuarios con multas pendientes.");
                System.out.print("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }
            
            // Ordenar por monto de multa (de mayor a menor)
            usuariosConMultas.sort((u1, u2) -> Double.compare(u2.getMulta(), u1.getMulta()));
            
            System.out.printf("%-5s | %-20s | %-15s\n", "ID", "Nombre", "Monto Multa");
            System.out.println("--------------------------------------------------");
            
            double totalMultas = 0;
            
            for (Usuario usuario : usuariosConMultas) {
                System.out.printf("%-5d | %-20s | $%-14.2f\n",
                        usuario.getIdUsuario(),
                        usuario.getNombre(),
                        usuario.getMulta());
                
                totalMultas += usuario.getMulta();
            }
            
            System.out.println("--------------------------------------------------");
            System.out.printf("Total de usuarios con multas: %d\n", usuariosConMultas.size());
            System.out.printf("Suma total de multas: $%.2f\n", totalMultas);
            
        } catch (Exception e) {
            System.out.println("Error al generar el reporte de usuarios con multas: " + e.getMessage());
        }
        
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Genera un reporte de libros por categoría.
     */
    private static void generarReporteLibrosPorCategoria() {
    System.out.println("\n===========================================");
    System.out.println("      REPORTE DE LIBROS POR CATEGORÍA");
    System.out.println("===========================================");
    
    try {
        Map<Categoria, List<Libro>> categoriasConLibros = categoriaServicio.obtenerCategoriasConLibros();
        
        if (categoriasConLibros.isEmpty()) {
            System.out.println("No hay categorías con libros en el sistema.");
            return;
        }
        
        int totalLibros = 0;
        
        for (Map.Entry<Categoria, List<Libro>> entry : categoriasConLibros.entrySet()) {
            Categoria categoria = entry.getKey();
            List<Libro> libros = entry.getValue();
            
            System.out.println("\nCATEGORÍA: " + categoria.getNombre() + " (ID: " + categoria.getIdCategoria() + ")");
            System.out.println("-------------------------------------------------------------------------");
            
            if (libros.isEmpty()) {
                System.out.println("No hay libros en esta categoría.");
                continue;
            }
            
            System.out.printf("%-5s | %-30s | %-15s | %-10s\n", 
                    "ID", "Título", "ISBN", "Estado");
            System.out.println("-------------------------------------------------------------------------");
            
            for (Libro libro : libros) {
                String estado = obtenerNombreEstadoLibro(libro.getIdEstado());
                
                System.out.printf("%-5d | %-30s | %-15s | %-10s\n",
                        libro.getIdLibro(),
                        libro.getTitulo(),
                        libro.getIsbn(),
                        estado);
            }
            
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Total en esta categoría: " + libros.size());
            
            totalLibros += libros.size();
        }
        
        System.out.println("\nTotal de libros en todas las categorías: " + totalLibros);
        
    } catch (Exception e) {
        System.out.println("Error al generar el reporte de libros por categoría: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static String obtenerNombreEstadoLibro(Integer idEstado) {
    switch (idEstado) {
        case 1: return "Disponible";
        case 2: return "Prestado";
        case 3: return "Reservado";
        case 4: return "En reparación";
        default: return "Desconocido";
    }
    
}
    
private static void generarReporteActividadUsuarios() {
    System.out.println("\n===========================================");
    System.out.println("      REPORTE DE ACTIVIDAD DE USUARIOS");
    System.out.println("===========================================");
    
    try {
        System.out.println("Seleccione el tipo de reporte:");
        System.out.println("1. Actividad de un usuario específico");
        System.out.println("2. Resumen de actividad de todos los usuarios");
        System.out.print("Opción: ");
        
        int opcion = Integer.parseInt(scanner.nextLine());
        
        // Solicitar rango de fechas
        System.out.println("\nIngrese el rango de fechas (formato dd/MM/yyyy):");
        System.out.print("Fecha de inicio: ");
        String fechaInicioStr = scanner.nextLine();
        System.out.print("Fecha de fin: ");
        String fechaFinStr = scanner.nextLine();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime fechaInicio = LocalDate.parse(fechaInicioStr, formatter).atStartOfDay();
        LocalDateTime fechaFin = LocalDate.parse(fechaFinStr, formatter).atTime(23, 59, 59);
        
        LogServicio logServicio = new LogServicio();
        
        if (opcion == 1) {
            // Actividad de un usuario específico
            System.out.print("Ingrese el ID del usuario: ");
            int idUsuario = Integer.parseInt(scanner.nextLine());
            
            List<Log> logsUsuario = logServicio.obtenerLogsPorUsuarioEnRango(idUsuario, fechaInicio, fechaFin);
            
            if (logsUsuario.isEmpty()) {
                System.out.println("No hay registros de actividad para este usuario en el rango de fechas seleccionado.");
                return;
            }
            
            // Obtener nombre del usuario
            Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
            String nombreUsuario = (usuario != null) ? usuario.getNombre() : "ID: " + idUsuario;
            
            System.out.println("\nActividad del usuario: " + nombreUsuario);
            System.out.println("Período: " + fechaInicioStr + " al " + fechaFinStr);
            System.out.println("-------------------------------------------------------------------------");
            
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            System.out.printf("%-5s | %-12s | %-20s | %-30s\n", 
                    "ID", "Tipo", "Acción", "Fecha");
            System.out.println("-------------------------------------------------------------------------");
            
            for (Log log : logsUsuario) {
                String tipoLog = obtenerNombreTipoLog(log.getIdTipoLog());
                
                System.out.printf("%-5d | %-12s | %-20s | %-30s\n",
                        log.getIdLog(),
                        tipoLog,
                        log.getAccion(),
                        log.getFecha().format(dtFormatter));
            }
            
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Total de actividades: " + logsUsuario.size());
            
        } else if (opcion == 2) {
            // Resumen de actividad de todos los usuarios
            Map<Integer, Integer> actividadUsuarios = logServicio.obtenerResumenActividadUsuarios(fechaInicio, fechaFin);
            
            if (actividadUsuarios.isEmpty()) {
                System.out.println("No hay registros de actividad en el rango de fechas seleccionado.");
                return;
            }
            
            // Convertir a lista para ordenar
            List<Map.Entry<Integer, Integer>> listaActividad = new ArrayList<>(actividadUsuarios.entrySet());
            
            // Ordenar por cantidad de actividades (mayor a menor)
            listaActividad.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            System.out.println("\nResumen de actividad de usuarios");
            System.out.println("Período: " + fechaInicioStr + " al " + fechaFinStr);
            System.out.println("-------------------------------------------------------------------------");
            
            System.out.printf("%-5s | %-20s | %-15s\n", 
                    "ID", "Usuario", "Actividades");
            System.out.println("-------------------------------------------------------------------------");
            
            for (Map.Entry<Integer, Integer> entry : listaActividad) {
                Integer idUsuario = entry.getKey();
                Integer cantidadActividades = entry.getValue();
                
                String nombreUsuario = "Desconocido";
                try {
                    Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
                    if (usuario != null) {
                        nombreUsuario = usuario.getNombre();
                    }
                } catch (Exception e) {
                    // Ignorar errores al obtener el nombre
                }
                
                System.out.printf("%-5d | %-20s | %-15d\n",
                        idUsuario,
                        nombreUsuario,
                        cantidadActividades);
            }
            
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Total de usuarios activos: " + listaActividad.size());
            
        } else {
            System.out.println("Opción no válida.");
        }
        
    } catch (Exception e) {
        System.out.println("Error al generar el reporte de actividad de usuarios: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}
    
    
    /**
     * Genera un reporte de logs del sistema.
     */
    private static void generarReporteLogsSistema() {
        System.out.println("\n===========================================");
        System.out.println("      REPORTE DE LOGS DEL SISTEMA");
        System.out.println("===========================================");
        
        try {
            System.out.println("Seleccione el tipo de log a mostrar:");
            System.out.println("1. Todos los logs");
            System.out.println("2. Logs de usuario");
            System.out.println("3. Logs de libro");
            System.out.println("4. Logs de préstamo");
            System.out.println("5. Logs de reserva");
            System.out.println("6. Logs de sistema");
            System.out.print("Opción: ");
            
            int opcion = Integer.parseInt(scanner.nextLine());
            
            LogServicio logServicio = new LogServicio();
            List<Log> logs;
            
            switch (opcion) {
                case 1:
                    logs = logServicio.obtenerTodosLogs();
                    break;
                case 2:
                    logs = logServicio.obtenerLogsPorTipo(LogUtil.LOG_TIPO_USUARIO);
                    break;
                case 3:
                    logs = logServicio.obtenerLogsPorTipo(LogUtil.LOG_TIPO_LIBRO);
                    break;
                case 4:
                    logs = logServicio.obtenerLogsPorTipo(LogUtil.LOG_TIPO_PRESTAMO);
                    break;
                case 5:
                    logs = logServicio.obtenerLogsPorTipo(LogUtil.LOG_TIPO_RESERVA);
                    break;
                case 6:
                    logs = logServicio.obtenerLogsPorTipo(LogUtil.LOG_TIPO_SISTEMA);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }
            
            if (logs.isEmpty()) {
                System.out.println("No hay logs que mostrar para esta selección.");
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            System.out.printf("%-5s | %-12s | %-15s | %-20s | %-30s\n", 
                    "ID", "Tipo", "Usuario", "Acción", "Fecha");
            System.out.println("-------------------------------------------------------------------------------------");
            
            for (Log log : logs) {
                String tipoLog = obtenerNombreTipoLog(log.getIdTipoLog());
                String usuario = log.getIdUsuario() == null ? "Sistema" : "ID: " + log.getIdUsuario();
                
                // Limitar longitud de la acción para la visualización
                String accion = log.getAccion();
                if (accion.length() > 20) {
                    accion = accion.substring(0, 17) + "...";
                }
                
                System.out.printf("%-5d | %-12s | %-15s | %-20s | %-30s\n",
                        log.getIdLog(),
                        tipoLog,
                        usuario,
                        accion,
                        log.getFecha().format(formatter));
            }
            
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Total de logs: " + logs.size());
            
        } catch (Exception e) {
            System.out.println("Error al generar el reporte de logs: " + e.getMessage());
        }
        
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Obtiene el nombre descriptivo del tipo de log según su ID.
     */
    private static String obtenerNombreTipoLog(int idTipoLog) {
        switch (idTipoLog) {
            case LogUtil.LOG_TIPO_USUARIO:
                return "Usuario";
            case LogUtil.LOG_TIPO_LIBRO:
                return "Libro";
            case LogUtil.LOG_TIPO_PRESTAMO:
                return "Préstamo";
            case LogUtil.LOG_TIPO_RESERVA:
                return "Reserva";
            case LogUtil.LOG_TIPO_SISTEMA:
                return "Sistema";
            default:
                return "Desconocido";
        }
    }
    
private static void generarReporteLibrosMasPrestados() {
    System.out.println("\n===========================================");
    System.out.println("      REPORTE DE LIBROS MÁS PRESTADOS");
    System.out.println("===========================================");
    
    try {
        System.out.print("Ingrese el número de libros a mostrar: ");
        int limite = Integer.parseInt(scanner.nextLine());
        
        List<Map.Entry<Libro, Integer>> librosMasPrestados = prestamoServicio.obtenerLibrosMasPrestados(limite);
        
        if (librosMasPrestados.isEmpty()) {
            System.out.println("No hay datos de préstamos en el sistema.");
            return;
        }
        
        System.out.printf("%-5s | %-30s | %-15s | %-10s\n", 
                "ID", "Título", "ISBN", "Préstamos");
        System.out.println("-------------------------------------------------------------------------");
        
        for (Map.Entry<Libro, Integer> entry : librosMasPrestados) {
            Libro libro = entry.getKey();
            int cantidad = entry.getValue();
            
            System.out.printf("%-5d | %-30s | %-15s | %-10d\n",
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getIsbn(),
                    cantidad);
        }
        
        System.out.println("-------------------------------------------------------------------------");
        
    } catch (Exception e) {
        System.out.println("Error al generar el reporte de libros más prestados: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}
    
private static void buscarReservaPorId() {
    System.out.print("Ingrese el ID de la reserva: ");
    try {
        int id = Integer.parseInt(scanner.nextLine());
        Reserva reserva = reservaServicio.obtenerReservaPorId(id);
        
        if (reserva == null) {
            System.out.println("No se encontró una reserva con el ID: " + id);
            return;
        }
        
        // Formato para fechas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date hoy = new Date();
        
        // Mostrar información detallada de la reserva
        System.out.println("\nDetalles de la reserva:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + reserva.getIdReserva());
        
        // Información del usuario
        String nombreUsuario = reserva.getUsuario() != null ? 
                reserva.getUsuario().getNombre() : "ID: " + reserva.getIdUsuario();
        System.out.println("Usuario: " + nombreUsuario);
        
        // Información del libro
        String tituloLibro = reserva.getLibro() != null ? 
                reserva.getLibro().getTitulo() : "ID: " + reserva.getIdLibro();
        System.out.println("Libro: " + tituloLibro);
        
        // Estado de la reserva
        String estado;
        switch (reserva.getIdEstadoReserva()) {
            case 1: estado = "Pendiente"; break;
            case 2: estado = "Completada"; break;
            case 3: estado = "Vencida"; break;
            case 4: estado = "Cancelada"; break;
            default: estado = "Desconocido";
        }
        System.out.println("Estado: " + estado);
        
        // Fechas
        System.out.println("Fecha de reserva: " + sdf.format(reserva.getFechaReserva()));
        System.out.println("Fecha de vencimiento: " + sdf.format(reserva.getFechaVencimiento()));
        
        // Alerta si está vencida pero aún marcada como pendiente
        if (reserva.getIdEstadoReserva() == 1 && hoy.after(reserva.getFechaVencimiento())) {
            System.out.println("\n¡ATENCIÓN! Esta reserva está vencida pero sigue en estado pendiente.");
            System.out.println("Considere actualizar su estado a 'Vencida'.");
        }
        
        System.out.println("--------------------------------------------");
    } catch (NumberFormatException e) {
        System.out.println("Error: Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar la reserva: " + e.getMessage());
    } finally {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}

private static void buscarPrestamosPorUsuario() {
    System.out.print("Ingrese el ID del usuario: ");
    try {
        int idUsuario = Integer.parseInt(scanner.nextLine());
        
        // Verificar si el usuario existe
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
        if (usuario == null) {
            System.out.println("No se encontró un usuario con el ID: " + idUsuario);
            return;
        }
        
        // Obtener préstamos del usuario
        List<Prestamo> prestamosUsuario = prestamoServicio.obtenerPrestamosPorUsuario(idUsuario);
        
        // Mostrar resumen del usuario
        System.out.println("\nInformación del usuario:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Rol: " + (usuario.getIdRol() == 1 ? "Administrador" : "Usuario regular"));
        
        // Contar préstamos por estado
        int prestamosActivos = 0;
        int prestamosVencidos = 0;
        int prestamosDevueltos = 0;
        
        Date hoy = new Date();
        for (Prestamo p : prestamosUsuario) {
            if (p.getFechaDevolucionReal() == null) {
                if (hoy.after(p.getFechaDevolucionEsperada())) {
                    prestamosVencidos++;
                } else {
                    prestamosActivos++;
                }
            } else {
                prestamosDevueltos++;
            }
        }
        
        System.out.println("\nResumen de préstamos:");
        System.out.println("Préstamos activos: " + prestamosActivos);
        System.out.println("Préstamos vencidos: " + prestamosVencidos);
        System.out.println("Préstamos devueltos: " + prestamosDevueltos);
        System.out.println("Total de préstamos: " + prestamosUsuario.size());
        
        // Mostrar tabla con todos los préstamos
        if (prestamosUsuario.isEmpty()) {
            System.out.println("\nEl usuario no tiene préstamos registrados.");
        } else {
            System.out.println("\nDetalle de préstamos:");
            mostrarPrestamosEnTabla(prestamosUsuario);
        }
    } catch (NumberFormatException e) {
        System.out.println("Error: Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar préstamos del usuario: " + e.getMessage());
    } finally {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}

private static void buscarPrestamosPorLibro() {
    System.out.print("Ingrese el ID del libro: ");
    try {
        int idLibro = Integer.parseInt(scanner.nextLine());
        
        // Verificar si el libro existe
        Libro libro = libroServicio.obtenerLibroPorId(idLibro);
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + idLibro);
            return;
        }
        
        // Obtener préstamos del libro
        List<Prestamo> prestamosLibro = prestamoServicio.obtenerPrestamosPorLibro(idLibro);
        
        // Mostrar información del libro
        System.out.println("\nInformación del libro:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + libro.getIdLibro());
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("ISBN: " + libro.getIsbn());
        System.out.println("Estado actual: " + obtenerNombreEstadoLibro(libro.getIdEstado()));
        
        // Estadísticas de préstamos
        if (prestamosLibro.isEmpty()) {
            System.out.println("\nEste libro no tiene historial de préstamos.");
        } else {
            // Calcular estadísticas
            int totalPrestamos = prestamosLibro.size();
            long totalDiasPrestado = 0;
            Prestamo prestamoMasReciente = prestamosLibro.get(0); // La lista viene ordenada por fecha
            
            for (Prestamo p : prestamosLibro) {
                // Calcular días de préstamo para préstamos completados
                if (p.getFechaDevolucionReal() != null) {
                    long dias = (p.getFechaDevolucionReal().getTime() - p.getFechaPrestamo().getTime()) 
                              / (1000 * 60 * 60 * 24);
                    totalDiasPrestado += dias;
                }
            }
            
            double promedioDias = totalPrestamos > 0 ? 
                    (double) totalDiasPrestado / totalPrestamos : 0;
            
            // Mostrar estadísticas
            System.out.println("\nEstadísticas de préstamos:");
            System.out.println("Total de préstamos: " + totalPrestamos);
            System.out.println("Promedio de días por préstamo: " + String.format("%.1f", promedioDias));
            
            // Verificar estado actual de préstamo
            boolean prestamoActivo = false;
            for (Prestamo prestamo : prestamosLibro) {
                if (prestamo.getFechaDevolucionReal() == null) {
                    prestamoActivo = true;
                    System.out.println("\nEstado actual: PRESTADO");
                    System.out.println("Prestado a: " + 
                            (prestamo.getUsuario() != null ? prestamo.getUsuario().getNombre() 
                                                         : "Usuario ID: " + prestamo.getIdUsuario()));
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println("Fecha de préstamo: " + sdf.format(prestamo.getFechaPrestamo()));
                    System.out.println("Fecha de devolución esperada: " + sdf.format(prestamo.getFechaDevolucionEsperada()));
                    
                    // Verificar si está vencido
                    Date hoy = new Date();
                    if (hoy.after(prestamo.getFechaDevolucionEsperada())) {
                        long diasRetraso = (hoy.getTime() - prestamo.getFechaDevolucionEsperada().getTime()) 
                                         / (1000 * 60 * 60 * 24);
                        System.out.println("¡ATENCIÓN! Préstamo VENCIDO por " + diasRetraso + " días.");
                    }
                    break;
                }
            }
            
            if (!prestamoActivo) {
                System.out.println("\nEstado actual: DISPONIBLE");
            }
            
            // Mostrar el historial completo
            System.out.println("\nHistorial completo de préstamos:");
            mostrarPrestamosEnTabla(prestamosLibro);
        }
    } catch (NumberFormatException e) {
        System.out.println("Error: Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al buscar préstamos del libro: " + e.getMessage());
    } finally {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /* 
     * FUNCIONES ESPECÍFICAS DE GESTIÓN DE USUARIOS
     */
    
    
    
    private static void buscarUsuarioPorId() {
        System.out.print("Ingrese el ID del usuario: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id);
            if (usuario != null) {
                mostrarDetallesUsuario(usuario);
            } else {
                System.out.println("No se encontró un usuario con el ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
    }
    
    private static void buscarUsuarioPorNombre() {
        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();
        try {
            Usuario usuario = usuarioServicio.obtenerUsuarioPorNombre(nombre);
            if (usuario != null) {
                mostrarDetallesUsuario(usuario);
            } else {
                System.out.println("No se encontró un usuario con el nombre: " + nombre);
            }
        } catch (Exception e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
    }
    
    private static void mostrarDetallesUsuario(Usuario usuario) {
        System.out.println("\nDetalles del usuario:");
        System.out.println("--------------------------------------------");
        System.out.println("ID: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Rol: " + (usuario.getIdRol() == 1 ? "Administrador" : "Usuario regular"));
        System.out.println("Multa: $" + usuario.getMulta());
        System.out.println("Estado: " + (usuario.getActivo() ? "Activo" : "Inactivo"));
        System.out.println("--------------------------------------------");
    }
    
    private static void registrarUsuario() {
        System.out.println("\nRegistrar nuevo usuario:");
        System.out.println("--------------------------------------------");
        
        System.out.print("Nombre de usuario: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        
        System.out.print("Rol (1 = Administrador, 2 = Usuario regular): ");
        int idRol;
        try {
            idRol = Integer.parseInt(scanner.nextLine());
            if (idRol != 1 && idRol != 2) {
                System.out.println("Rol no válido. Se asignará como Usuario regular (2).");
                idRol = 2;
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido. Se asignará como Usuario regular (2).");
            idRol = 2;
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setPassword(password); // No aplicar hash aquí, el servicio lo hará
        nuevoUsuario.setIdRol(idRol);
        nuevoUsuario.setMulta(0.0);
        nuevoUsuario.setActivo(true);
        
        try {
            usuarioServicio.registrarUsuario(nuevoUsuario);
            System.out.println("Usuario registrado exitosamente.");
            mostrarDetallesUsuario(nuevoUsuario);
        } catch (Exception ex) {
            System.out.println("Error registrando usuario: " + ex.getMessage());
        }
    }
    
    private static void actualizarUsuario() {
        System.out.print("Ingrese el ID del usuario a actualizar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return;
        }
        
        try {
            Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id);
            if (usuario == null) {
                System.out.println("No se encontró un usuario con el ID: " + id);
                return;
            }
            
            mostrarDetallesUsuario(usuario);
            System.out.println("\nActualizar usuario (deje en blanco para mantener el valor actual):");
            
            System.out.print("Nuevo nombre [" + usuario.getNombre() + "]: ");
            String nombre = scanner.nextLine();
            if (!nombre.isEmpty()) {
                usuario.setNombre(nombre);
            }
            
            System.out.print("Nueva contraseña (dejar en blanco para no cambiar): ");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                usuario.setPassword(SecurityUtil.hashPassword(password));
            }
            
            System.out.print("Nuevo rol (1 = Admin, 2 = Usuario) [" + usuario.getIdRol() + "]: ");
            String rolStr = scanner.nextLine();
            if (!rolStr.isEmpty()) {
                try {
                    int rol = Integer.parseInt(rolStr);
                    if (rol == 1 || rol == 2) {
                        usuario.setIdRol(rol);
                    } else {
                        System.out.println("Rol no válido. Se mantiene el valor actual.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Valor no válido. Se mantiene el rol actual.");
                }
            }
            
            System.out.print("Nueva multa [" + usuario.getMulta() + "]: ");
            String multaStr = scanner.nextLine();
            if (!multaStr.isEmpty()) {
                try {
                    double multa = Double.parseDouble(multaStr);
                    usuario.setMulta(multa);
                } catch (NumberFormatException e) {
                    System.out.println("Valor no válido. Se mantiene la multa actual.");
                }
            }
            
            System.out.print("Estado (true = Activo, false = Inactivo) [" + usuario.getActivo() + "]: ");
            String activoStr = scanner.nextLine();
            if (!activoStr.isEmpty()) {
                usuario.setActivo(Boolean.parseBoolean(activoStr));
            }
            
            usuarioServicio.actualizarUsuario(usuario);
            System.out.println("Usuario actualizado exitosamente.");
            mostrarDetallesUsuario(usuario);
        } catch (Exception e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    private static void eliminarUsuario() {
        System.out.print("Ingrese el ID del usuario a eliminar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return;
        }
        
        try {
            Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id);
            if (usuario == null) {
                System.out.println("No se encontró un usuario con el ID: " + id);
                return;
            }
            
            mostrarDetallesUsuario(usuario);
            System.out.print("¿Está seguro de que desea eliminar este usuario? (S/N): ");
            String confirmacion = scanner.nextLine();
            
            if (confirmacion.equalsIgnoreCase("S")) {
                usuarioServicio.eliminarUsuario(id);
                System.out.println("Usuario eliminado exitosamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }
    
    
    
    
    /* 
     * FUNCIONES ESPECÍFICAS DE GESTIÓN DE USUARIOS
     */
    
    private static void listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioServicio.obtenerTodos();
            System.out.println("\nLista de usuarios:");
            System.out.println("--------------------------------------------");
            System.out.printf("%-5s | %-20s | %-10s | %-10s\n", "ID", "Nombre", "Rol", "Multa");
            System.out.println("--------------------------------------------");
            for (Usuario usuario : usuarios) {
                String rol = usuario.getIdRol() == 1 ? "Admin" : "Usuario";
                System.out.printf("%-5d | %-20s | %-10s | $%-9.2f\n", 
                        usuario.getIdUsuario(), usuario.getNombre(), rol, usuario.getMulta());
            }
            System.out.println("--------------------------------------------");
        } catch (Exception e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
    }
    
   
    
    
    /* 
     * FUNCIONES PARA USUARIO REGULAR
     */
    
private static void buscarLibros() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("           BÚSQUEDA DE LIBROS");
        System.out.println("-----------------------------------------");
        System.out.println("1. Buscar por título");
        System.out.println("2. Buscar por autor");
        System.out.println("3. Buscar por género");
        System.out.println("4. Buscar por categoría");
        System.out.println("5. Volver al menú anterior");
        
        // Implementar cada opción llamando a libroServicio.buscarPorTitulo(), 
        // libroServicio.obtenerLibrosPorAutor(), etc.
    }
}
// 2. Realizar Reserva
private static void realizarReserva() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           REALIZAR RESERVA");
    System.out.println("-----------------------------------------");
    
    System.out.print("Ingrese el ID del libro a reservar: ");
    try {
        int idLibro = Integer.parseInt(scanner.nextLine());
        Libro libro = libroServicio.obtenerLibroPorId(idLibro);
        
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + idLibro);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Verificar si el usuario ya tiene una reserva activa para este libro
        if (reservaServicio.tieneReservaActiva(usuarioActual.getIdUsuario(), idLibro)) {
            System.out.println("Ya tienes una reserva activa para este libro.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Verificar si el libro está disponible para reserva
        if (libro.getIdEstado() == 1) { // 1 = disponible
            System.out.println("Este libro está disponible para préstamo inmediato, no es necesario reservarlo.");
            System.out.print("¿Desea realizar un préstamo ahora? (S/N): ");
            String respuesta = scanner.nextLine();
            
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.println("Cambiando a préstamo directo...");
                realizarPrestamoDirecto(libro);
                return;
            }
        }
        
        // Crear objeto de reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setIdUsuario(usuarioActual.getIdUsuario());
        nuevaReserva.setIdLibro(idLibro);
        nuevaReserva.setFechaReserva(new Date());
        // Estado pendiente (1)
        nuevaReserva.setIdEstadoReserva(1);
        
        // Fecha de vencimiento (7 días después)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        nuevaReserva.setFechaVencimiento(calendar.getTime());
        
        // Registrar la reserva
        reservaServicio.registrarReserva(nuevaReserva, usuarioActual.getIdUsuario());
        
        System.out.println("\nReserva realizada exitosamente");
        System.out.println("Título del libro: " + libro.getTitulo());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Fecha de vencimiento de la reserva: " + sdf.format(nuevaReserva.getFechaVencimiento()));
        System.out.println("Serás notificado cuando el libro esté disponible.");
        
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al realizar la reserva: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// Método auxiliar para realizar préstamo directo desde la reserva
private static void realizarPrestamoDirecto(Libro libro) {
    try {
        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setIdUsuario(usuarioActual.getIdUsuario());
        nuevoPrestamo.setIdLibro(libro.getIdLibro());
        nuevoPrestamo.setFechaPrestamo(new Date());
        nuevoPrestamo.setIdTipoPrestamo(1); // Préstamo normal
        
        // Fecha de devolución esperada (15 días después)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        nuevoPrestamo.setFechaDevolucionEsperada(calendar.getTime());
        
        // Registrar préstamo
        prestamoServicio.registrarPrestamo(nuevoPrestamo);
        
        // Actualizar estado del libro a prestado (2)
        libroServicio.actualizarEstadoLibro(libro.getIdLibro(), 2, usuarioActual.getIdUsuario());
        
         // Completar reservas si existen
        if (reservaServicio.tieneReservaActiva(usuarioActual.getIdUsuario(), libro.getIdLibro())) {
            int reservasActualizadas = reservaServicio.completarReservasUsuarioLibro(usuarioActual.getIdUsuario(), libro.getIdLibro());
            if (reservasActualizadas > 0) {
                System.out.println("Se ha(n) completado " + reservasActualizadas + " reserva(s) para este libro.");
            }
        }
        System.out.println("\nPréstamo realizado exitosamente.");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Título del libro: " + libro.getTitulo());
        System.out.println("Fecha de devolución esperada: " + sdf.format(nuevoPrestamo.getFechaDevolucionEsperada()));
        
    } catch (Exception e) {
        System.out.println("Error al realizar el préstamo: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 3. Consultar Mis Reservas
private static void consultarMisReservas() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           MIS RESERVAS");
    System.out.println("-----------------------------------------");
    
    try {
        List<Reserva> misReservas = reservaServicio.obtenerReservasPorUsuario(usuarioActual.getIdUsuario());
        
        if (misReservas.isEmpty()) {
            System.out.println("No tienes reservas registradas.");
            System.out.println("Nota: Los libros disponibles se prestan directamente en lugar de reservarse.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nListado de tus reservas:");
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("%-5s | %-30s | %-15s | %-15s | %-10s\n", 
                "ID", "Título del Libro", "Fecha Reserva", "Vencimiento", "Estado");
        System.out.println("-----------------------------------------------------------------------");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaActual = new Date();
        
        for (Reserva reserva : misReservas) {
            // Intentar cargar datos del libro
            String tituloLibro = "ID: " + reserva.getIdLibro();
            try {
                Libro libro = libroServicio.obtenerLibroPorId(reserva.getIdLibro());
                if (libro != null) {
                    tituloLibro = libro.getTitulo();
                }
            } catch (Exception e) {
                // Si hay error al cargar el libro, usar el ID
            }
            
            // Determinar estado
            String estado;
            switch (reserva.getIdEstadoReserva()) {
                case 1: 
                    estado = fechaActual.after(reserva.getFechaVencimiento()) ? "Vencida" : "Pendiente"; 
                    break;
                case 2: estado = "Completada"; break;
                case 3: estado = "Vencida"; break;
                case 4: estado = "Cancelada"; break;
                default: estado = "Desconocido";
            }
            
            System.out.printf("%-5d | %-30s | %-15s | %-15s | %-10s\n",
                    reserva.getIdReserva(),
                    tituloLibro.length() > 30 ? tituloLibro.substring(0, 27) + "..." : tituloLibro,
                    sdf.format(reserva.getFechaReserva()),
                    sdf.format(reserva.getFechaVencimiento()),
                    estado);
        }
        
        System.out.println("-----------------------------------------------------------------------");
        
        // Opciones adicionales
        System.out.println("\nOpciones:");
        System.out.println("1. Cancelar una reserva");
        System.out.println("2. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
        
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            
            if (opcion == 1) {
                System.out.print("Ingrese el ID de la reserva a cancelar: ");
                int idReserva = Integer.parseInt(scanner.nextLine());
                
                // Verificar que la reserva exista y sea del usuario
                boolean reservaValida = false;
                for (Reserva r : misReservas) {
                    if (r.getIdReserva() == idReserva) {
                        reservaValida = true;
                        break;
                    }
                }
                
                if (!reservaValida) {
                    System.out.println("La reserva no existe o no te pertenece.");
                } else {
                    System.out.print("¿Estás seguro de cancelar esta reserva? (S/N): ");
                    String confirmacion = scanner.nextLine();
                    
                    if (confirmacion.equalsIgnoreCase("S")) {
                        reservaServicio.eliminarReserva(idReserva, usuarioActual.getIdUsuario());
                        System.out.println("Reserva cancelada exitosamente.");
                    } else {
                        System.out.println("Operación cancelada.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    } catch (Exception e) {
        System.out.println("Error al consultar reservas: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 4. Realizar Préstamo
private static void realizarPrestamo() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           REALIZAR PRÉSTAMO");
    System.out.println("-----------------------------------------");
    
    // Verificar si el usuario tiene multas pendientes
    if (usuarioActual.getMulta() > 0) {
        System.out.println("No puedes realizar préstamos porque tienes una multa pendiente de $" + usuarioActual.getMulta());
        System.out.println("Por favor, cancela tu multa antes de realizar un préstamo.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    try {
        // Verificar cantidad de préstamos activos
        List<Prestamo> prestamosActivos = prestamoServicio.obtenerPrestamosActivos();
        int cantidadPrestamosUsuario = 0;
        
        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                cantidadPrestamosUsuario++;
            }
        }
        
        // Límite de préstamos para usuarios regulares (3)
        if (usuarioActual.getIdRol() == 2 && cantidadPrestamosUsuario >= 3) {
            System.out.println("Has alcanzado el límite máximo de 3 préstamos simultáneos.");
            System.out.println("Debes devolver algún libro antes de realizar un nuevo préstamo.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Buscar el libro a prestar
        System.out.println("Ingrese el ID del libro o 0 para buscar por título: ");
        int idLibro = Integer.parseInt(scanner.nextLine());
        
        if (idLibro == 0) {
            System.out.print("Ingrese parte del título a buscar: ");
            String titulo = scanner.nextLine();
            
            List<Libro> librosEncontrados = libroServicio.buscarPorTitulo(titulo);
            
            if (librosEncontrados.isEmpty()) {
                System.out.println("No se encontraron libros con ese título.");
                System.out.print("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }
            
            System.out.println("\nLibros encontrados:");
            System.out.println("-----------------------------------------------------------");
            System.out.printf("%-5s | %-30s | %-15s | %-10s\n", 
                    "ID", "Título", "ISBN", "Estado");
            System.out.println("-----------------------------------------------------------");
            
            for (Libro libro : librosEncontrados) {
                String estado = obtenerNombreEstadoLibro(libro.getIdEstado());
                System.out.printf("%-5d | %-30s | %-15s | %-10s\n",
                        libro.getIdLibro(),
                        libro.getTitulo().length() > 30 ? libro.getTitulo().substring(0, 27) + "..." : libro.getTitulo(),
                        libro.getIsbn(),
                        estado);
            }
            
            System.out.println("-----------------------------------------------------------");
            System.out.print("Ingrese el ID del libro a prestar o 0 para cancelar: ");
            idLibro = Integer.parseInt(scanner.nextLine());
            
            if (idLibro == 0) {
                System.out.println("Operación cancelada.");
                System.out.print("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }
        }
        
        // Verificar que el libro exista y esté disponible
        Libro libro = libroServicio.obtenerLibroPorId(idLibro);
        
        if (libro == null) {
            System.out.println("No se encontró un libro con el ID: " + idLibro);
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        if (libro.getIdEstado() != 1) { // 1 = Disponible
            System.out.println("El libro no está disponible para préstamo actualmente.");
            System.out.println("Estado actual: " + obtenerNombreEstadoLibro(libro.getIdEstado()));
            
            // Si está reservado, verificar si el usuario actual tiene la reserva
            if (libro.getIdEstado() == 3) { // 3 = Reservado
                boolean tieneReserva = reservaServicio.tieneReservaActiva(usuarioActual.getIdUsuario(), idLibro);
                
                if (tieneReserva) {
                    System.out.println("Tienes una reserva activa para este libro.");
                    System.out.print("¿Deseas convertir tu reserva en préstamo? (S/N): ");
                    String respuesta = scanner.nextLine();
                    
                    if (!respuesta.equalsIgnoreCase("S")) {
                        System.out.println("Operación cancelada.");
                        System.out.print("\nPresione Enter para continuar...");
                        scanner.nextLine();
                        return;
                    }
                } else {
                    System.out.println("Este libro está reservado por otro usuario.");
                    System.out.print("\nPresione Enter para continuar...");
                    scanner.nextLine();
                    return;
                }
            } else {
                System.out.print("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }
        }
        
        // Crear y registrar el préstamo
        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setIdUsuario(usuarioActual.getIdUsuario());
        nuevoPrestamo.setIdLibro(idLibro);
        nuevoPrestamo.setFechaPrestamo(new Date());
        
        // Seleccionar tipo de préstamo
        System.out.println("\nSeleccione el tipo de préstamo:");
        System.out.println("1. Normal (15 días)");
        System.out.println("2. Express (7 días)");
        System.out.print("Opción: ");
        int tipoPrestamo = Integer.parseInt(scanner.nextLine());
        
        if (tipoPrestamo != 1 && tipoPrestamo != 2) {
            System.out.println("Tipo de préstamo no válido. Se establecerá como Normal.");
            tipoPrestamo = 1;
        }
        
        nuevoPrestamo.setIdTipoPrestamo(tipoPrestamo);
        
        // Calcular fecha de devolución esperada
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nuevoPrestamo.getFechaPrestamo());
        calendar.add(Calendar.DAY_OF_MONTH, tipoPrestamo == 1 ? 15 : 7); // 15 días para normal, 7 para express
        nuevoPrestamo.setFechaDevolucionEsperada(calendar.getTime());
        
        // Registrar préstamo
        prestamoServicio.registrarPrestamo(nuevoPrestamo);
        
        // Actualizar estado del libro a prestado (2)
        libroServicio.actualizarEstadoLibro(idLibro, 2, usuarioActual.getIdUsuario());
        
        // Si había una reserva, cambiar su estado a completada (2)
        if (reservaServicio.tieneReservaActiva(usuarioActual.getIdUsuario(), idLibro)) {
            int reservasActualizadas = reservaServicio.completarReservasUsuarioLibro(usuarioActual.getIdUsuario(), idLibro);
            if (reservasActualizadas > 0) {
                System.out.println("Se ha(n) completado " + reservasActualizadas + " reserva(s) para este libro.");
            }
        }
        System.out.println("\nPréstamo realizado exitosamente");
        System.out.println("Título del libro: " + libro.getTitulo());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Fecha de devolución esperada: " + sdf.format(nuevoPrestamo.getFechaDevolucionEsperada()));
        
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al realizar el préstamo: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 5. Consultar Mis Préstamos
private static void consultarMisPrestamos() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           MIS PRÉSTAMOS");
    System.out.println("-----------------------------------------");
    
    try {
        // Obtener todos los préstamos
        List<Prestamo> todosPrestamos = prestamoServicio.obtenerTodosPrestamos();
        
        // Filtrar los préstamos del usuario actual
        List<Prestamo> misPrestamos = new ArrayList<>();
        for (Prestamo prestamo : todosPrestamos) {
            if (prestamo.getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                misPrestamos.add(prestamo);
            }
        }
        
        if (misPrestamos.isEmpty()) {
            System.out.println("No tienes préstamos registrados.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Dividir en préstamos activos y históricos
        List<Prestamo> prestamosActivos = new ArrayList<>();
        List<Prestamo> prestamosHistoricos = new ArrayList<>();
        
        for (Prestamo prestamo : misPrestamos) {
            if (prestamo.getFechaDevolucionReal() == null) {
                prestamosActivos.add(prestamo);
            } else {
                prestamosHistoricos.add(prestamo);
            }
        }
        
        // Mostrar préstamos activos
        if (!prestamosActivos.isEmpty()) {
            System.out.println("\nPréstamos activos:");
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.printf("%-5s | %-30s | %-15s | %-15s | %-10s\n", 
                    "ID", "Título del Libro", "Fecha Préstamo", "Fecha Devolución", "Estado");
            System.out.println("----------------------------------------------------------------------------------------");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaActual = new Date();
            
            for (Prestamo prestamo : prestamosActivos) {
                // Intentar cargar datos del libro
                String tituloLibro = "ID: " + prestamo.getIdLibro();
                try {
                    Libro libro = libroServicio.obtenerLibroPorId(prestamo.getIdLibro());
                    if (libro != null) {
                        tituloLibro = libro.getTitulo();
                    }
                } catch (Exception e) {
                    // Si hay error al cargar el libro, usar el ID
                }
                
                // Determinar estado
                String estado;
                if (fechaActual.after(prestamo.getFechaDevolucionEsperada())) {
                    estado = "Vencido";
                    
                    // Calcular días de retraso
                    long diferenciaDias = (fechaActual.getTime() - prestamo.getFechaDevolucionEsperada().getTime()) / (1000 * 60 * 60 * 24);
                    estado += " (" + diferenciaDias + " días)";
                } else {
                    // Calcular días restantes
                    long diferenciaDias = (prestamo.getFechaDevolucionEsperada().getTime() - fechaActual.getTime()) / (1000 * 60 * 60 * 24);
                    estado = "Activo (" + diferenciaDias + " días)";
                }
                
                System.out.printf("%-5d | %-30s | %-15s | %-15s | %-10s\n",
                        prestamo.getIdPrestamo(),
                        tituloLibro.length() > 30 ? tituloLibro.substring(0, 27) + "..." : tituloLibro,
                        sdf.format(prestamo.getFechaPrestamo()),
                        sdf.format(prestamo.getFechaDevolucionEsperada()),
                        estado);
            }
            
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("Total de préstamos activos: " + prestamosActivos.size());
        } else {
            System.out.println("\nNo tienes préstamos activos.");
        }
        
        // Mostrar historial de préstamos (opcional)
        if (!prestamosHistoricos.isEmpty()) {
            System.out.println("\n¿Desea ver el historial de préstamos devueltos? (S/N): ");
            String respuesta = scanner.nextLine();
            
            if (respuesta.equalsIgnoreCase("S")) {
                // Ordenar por fecha de devolución (más reciente primero)
                Collections.sort(prestamosHistoricos, (p1, p2) -> 
                    p2.getFechaDevolucionReal().compareTo(p1.getFechaDevolucionReal()));
                
                System.out.println("\nHistorial de préstamos:");
                System.out.println("----------------------------------------------------------------------------------------");
                System.out.printf("%-5s | %-30s | %-15s | %-15s\n", 
                        "ID", "Título del Libro", "Fecha Préstamo", "Fecha Devolución");
                System.out.println("----------------------------------------------------------------------------------------");
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                for (Prestamo prestamo : prestamosHistoricos) {
                    // Intentar cargar datos del libro
                    String tituloLibro = "ID: " + prestamo.getIdLibro();
                    try {
                        Libro libro = libroServicio.obtenerLibroPorId(prestamo.getIdLibro());
                        if (libro != null) {
                            tituloLibro = libro.getTitulo();
                        }
                    } catch (Exception e) {
                        // Si hay error al cargar el libro, usar el ID
                    }
                    
                    System.out.printf("%-5d | %-30s | %-15s | %-15s\n",
                            prestamo.getIdPrestamo(),
                            tituloLibro.length() > 30 ? tituloLibro.substring(0, 27) + "..." : tituloLibro,
                            sdf.format(prestamo.getFechaPrestamo()),
                            sdf.format(prestamo.getFechaDevolucionReal()));
                }
                
                System.out.println("----------------------------------------------------------------------------------------");
                System.out.println("Total de préstamos históricos: " + prestamosHistoricos.size());
            }
        }
        
        // Opciones adicionales para préstamos activos
        if (!prestamosActivos.isEmpty()) {
            System.out.println("\nOpciones:");
            System.out.println("1. Devolver un libro");
            System.out.println("2. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                
                if (opcion == 1) {
                    devolverLibro();
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida.");
            }
        }
        
    } catch (Exception e) {
        System.out.println("Error al consultar préstamos: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 6. Devolver Libro
private static void devolverLibro() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           DEVOLVER LIBRO");
    System.out.println("-----------------------------------------");
    
    try {
        // Obtener préstamos activos del usuario
        List<Prestamo> todosPrestamos = prestamoServicio.obtenerTodosPrestamos();
        List<Prestamo> misPrestamosActivos = new ArrayList<>();
        
        for (Prestamo prestamo : todosPrestamos) {
            if (prestamo.getIdUsuario().equals(usuarioActual.getIdUsuario()) && 
                prestamo.getFechaDevolucionReal() == null) {
                misPrestamosActivos.add(prestamo);
            }
        }
        
        if (misPrestamosActivos.isEmpty()) {
            System.out.println("No tienes préstamos activos para devolver.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Mostrar préstamos activos
        System.out.println("\nSelecciona el libro a devolver:");
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("%-5s | %-30s | %-15s | %-15s\n", 
                "ID", "Título del Libro", "Fecha Préstamo", "Fecha Devolución");
        System.out.println("-----------------------------------------------------------------------");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (int i = 0; i < misPrestamosActivos.size(); i++) {
            Prestamo prestamo = misPrestamosActivos.get(i);
            
            // Intentar cargar datos del libro
            String tituloLibro = "ID: " + prestamo.getIdLibro();
            try {
                Libro libro = libroServicio.obtenerLibroPorId(prestamo.getIdLibro());
                if (libro != null) {
                    tituloLibro = libro.getTitulo();
                }
            } catch (Exception e) {
                // Si hay error al cargar el libro, usar el ID
            }
            
            System.out.printf("%-5d | %-30s | %-15s | %-15s\n",
                    prestamo.getIdPrestamo(),
                    tituloLibro.length() > 30 ? tituloLibro.substring(0, 27) + "..." : tituloLibro,
                    sdf.format(prestamo.getFechaPrestamo()),
                    sdf.format(prestamo.getFechaDevolucionEsperada()));
        }
        
        System.out.println("-----------------------------------------------------------------------");
        
        System.out.print("\nIngrese el ID del préstamo a devolver o 0 para cancelar: ");
        int idPrestamo = Integer.parseInt(scanner.nextLine());
        
        if (idPrestamo == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Verificar que el préstamo exista y pertenezca al usuario
        Prestamo prestamoADevolver = null;
        for (Prestamo prestamo : misPrestamosActivos) {
            if (prestamo.getIdPrestamo() == idPrestamo) {
                prestamoADevolver = prestamo;
                break;
            }
        }
        
        if (prestamoADevolver == null) {
            System.out.println("El préstamo seleccionado no existe o no te pertenece.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Confirmar devolución
        System.out.print("¿Está seguro de devolver este libro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        // Registrar devolución
        Date fechaDevolucion = new Date();
        prestamoServicio.registrarDevolucion(idPrestamo, fechaDevolucion);
        
        // Actualizar estado del libro a disponible (1)
        libroServicio.actualizarEstadoLibro(prestamoADevolver.getIdLibro(), 1, usuarioActual.getIdUsuario());
        
        System.out.println("\nDevolución registrada exitosamente.");
        
        // Verificar si hay multa por retraso
        if (fechaDevolucion.after(prestamoADevolver.getFechaDevolucionEsperada())) {
            // Calcular días de retraso
            long diferenciaDias = (fechaDevolucion.getTime() - prestamoADevolver.getFechaDevolucionEsperada().getTime()) / (1000 * 60 * 60 * 24);
            
            // Calcular multa (por ejemplo, $1 por día de retraso)
            double multa = diferenciaDias * 1.0;
            
            System.out.println("\n¡ATENCIÓN! El libro fue devuelto con " + diferenciaDias + " días de retraso.");
            System.out.println("Se ha aplicado una multa de $" + multa);
            
            // Actualizar multa del usuario
            double multaActual = usuarioActual.getMulta();
            usuarioActual.setMulta(multaActual + multa);
            usuarioServicio.actualizarUsuario(usuarioActual);
            
            System.out.println("Tu multa total ahora es de: $" + usuarioActual.getMulta());
        } else {
            // Verificar si es un préstamo express (devuelto en menos de 7 días)
            long diasPrestamo = (fechaDevolucion.getTime() - prestamoADevolver.getFechaPrestamo().getTime()) / (1000 * 60 * 60 * 24);
            
            if (diasPrestamo < 7) {
                System.out.println("\n¡Felicidades! Has realizado un préstamo express (devuelto en menos de 7 días).");
                // Aquí se podría registrar en una tabla especial o dar algún beneficio
            }
        }
        
    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un número válido.");
    } catch (Exception e) {
        System.out.println("Error al devolver el libro: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

// 7. Mi Perfil
private static void gestionarMiPerfil() {
    while (true) {
        System.out.println("\n-----------------------------------------");
        System.out.println("                MI PERFIL");
        System.out.println("-----------------------------------------");
        
        // Mostrar información actual
        try {
            // Recargar usuario para obtener datos actualizados
            usuarioActual = usuarioServicio.obtenerUsuarioPorId(usuarioActual.getIdUsuario());
            
            System.out.println("ID: " + usuarioActual.getIdUsuario());
            System.out.println("Nombre de usuario: " + usuarioActual.getNombre());
            System.out.println("Rol: " + (usuarioActual.getIdRol() == 1 ? "Administrador" : "Usuario regular"));
            System.out.println("Multa pendiente: $" + usuarioActual.getMulta());
            System.out.println("Estado: " + (usuarioActual.getActivo() ? "Activo" : "Inactivo"));
            
            // Obtener estadísticas
            try {
                List<Prestamo> todosPrestamos = prestamoServicio.obtenerTodosPrestamos();
                int totalPrestamos = 0;
                int prestamosActivos = 0;
                
                for (Prestamo prestamo : todosPrestamos) {
                    if (prestamo.getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                        totalPrestamos++;
                        if (prestamo.getFechaDevolucionReal() == null) {
                            prestamosActivos++;
                        }
                    }
                }
                
                List<Reserva> misReservas = reservaServicio.obtenerReservasPorUsuario(usuarioActual.getIdUsuario());
                int reservasActivas = 0;
                
                for (Reserva reserva : misReservas) {
                    if (reserva.getIdEstadoReserva() == 1) { // Pendiente
                        reservasActivas++;
                    }
                }
                
                System.out.println("\nEstadísticas:");
                System.out.println("Total de préstamos realizados: " + totalPrestamos);
                System.out.println("Préstamos activos: " + prestamosActivos);
                System.out.println("Reservas activas: " + reservasActivas);
                
            } catch (Exception e) {
                System.out.println("Error al cargar estadísticas: " + e.getMessage());
            }
            
            System.out.println("\nOpciones:");
            System.out.println("1. Cambiar contraseña");
            System.out.println("2. Pagar multa");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }
            
            switch (opcion) {
                case 1:
                    cambiarContrasena();
                    break;
                case 2:
                    pagarMulta();
                    break;
                case 3:
                    return; // Volver al menú principal
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
            
        } catch (Exception e) {
            System.out.println("Error al cargar datos del perfil: " + e.getMessage());
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
    }
}

private static void cambiarContrasena() {
    System.out.println("\n-----------------------------------------");
    System.out.println("           CAMBIAR CONTRASEÑA");
    System.out.println("-----------------------------------------");
    
    System.out.print("Ingrese su contraseña actual: ");
    String contrasenaActual = scanner.nextLine();
    
    // Verificar contraseña actual
    if (!SecurityUtil.verifyPassword(contrasenaActual, usuarioActual.getPassword())) {
        System.out.println("Contraseña incorrecta.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    System.out.print("Ingrese la nueva contraseña: ");
    String nuevaContrasena = scanner.nextLine();
    
    if (nuevaContrasena.length() < 4) {
        System.out.println("La contraseña debe tener al menos 4 caracteres.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    System.out.print("Confirme la nueva contraseña: ");
    String confirmacionContrasena = scanner.nextLine();
    
    if (!nuevaContrasena.equals(confirmacionContrasena)) {
        System.out.println("Las contraseñas no coinciden.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    try {
        // Actualizar contraseña
        usuarioActual.setPassword(SecurityUtil.hashPassword(nuevaContrasena));
        usuarioServicio.actualizarUsuario(usuarioActual);
        
        System.out.println("Contraseña actualizada exitosamente.");
    } catch (Exception e) {
        System.out.println("Error al cambiar la contraseña: " + e.getMessage());
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}

private static void pagarMulta() {
    System.out.println("\n-----------------------------------------");
    System.out.println("              PAGAR MULTA");
    System.out.println("-----------------------------------------");
    
    if (usuarioActual.getMulta() <= 0) {
        System.out.println("No tienes multas pendientes.");
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
        return;
    }
    
    System.out.println("Multa pendiente: $" + usuarioActual.getMulta());
    System.out.print("¿Deseas pagar la multa completa? (S/N): ");
    String respuesta = scanner.nextLine();
    
    if (!respuesta.equalsIgnoreCase("S")) {
        System.out.print("Ingrese el monto a pagar: $");
        double montoPago;
        
        try {
            montoPago = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Monto no válido.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        if (montoPago <= 0) {
            System.out.println("El monto a pagar debe ser mayor que cero.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        if (montoPago > usuarioActual.getMulta()) {
            System.out.println("El monto a pagar no puede ser mayor que la multa pendiente.");
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
            return;
        }
        
        try {
            // Actualizar multa
            double nuevaMulta = usuarioActual.getMulta() - montoPago;
            usuarioActual.setMulta(nuevaMulta);
            usuarioServicio.actualizarUsuario(usuarioActual);
            
            System.out.println("Pago realizado exitosamente.");
            System.out.println("Multa restante: $" + usuarioActual.getMulta());
        } catch (Exception e) {
            System.out.println("Error al procesar el pago: " + e.getMessage());
        }
    } else {
        try {
            // Pagar multa completa
            usuarioActual.setMulta(0.0);
            usuarioServicio.actualizarUsuario(usuarioActual);
            
            System.out.println("Multa pagada completamente.");
        } catch (Exception e) {
            System.out.println("Error al procesar el pago: " + e.getMessage());
        }
    }
    
    System.out.print("\nPresione Enter para continuar...");
    scanner.nextLine();
}
    
}