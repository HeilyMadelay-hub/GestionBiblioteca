
package Main;

import DAO.IMPL.UsuarioDAOImpl;
import DAO.UsuarioDAO;
import Modelo.Usuario;
import Vista.MainFrame;
import java.util.List;
import DAO.AutorDAO;
import DAO.IMPL.AutorDAOImpl;
import Modelo.Autor;
import DAO.GeneroDAO;
import DAO.IMPL.GeneroDAOImpl;
import Modelo.Genero;
import java.util.List;
import DAO.CategoriaDAO;
import DAO.IMPL.CategoriaDAOImpl;
import Modelo.Categoria;
import java.util.List;
import DAO.LibroDAO;
import DAO.AutorDAO;
import DAO.GeneroDAO;
import DAO.CategoriaDAO;
import DAO.IMPL.LibroDAOImpl;
import DAO.IMPL.AutorDAOImpl;
import DAO.IMPL.GeneroDAOImpl;
import DAO.IMPL.CategoriaDAOImpl;
import Modelo.Libro;
import DAO.PrestamoDAO;
import DAO.UsuarioDAO;
import DAO.LibroDAO;
import DAO.IMPL.PrestamoDAOImpl;
import DAO.IMPL.UsuarioDAOImpl;
import DAO.IMPL.LibroDAOImpl;
import Modelo.Prestamo;
import Modelo.Usuario;
import Modelo.Libro;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import Modelo.Autor;
import Modelo.Genero;
import Modelo.Categoria;
import java.util.List;
import java.util.Date;
import DAO.ContactoDAO;
import DAO.IMPL.ContactoDAOImpl;
import Modelo.Contacto;
import java.util.List;
import DAO.ReservaDAO;
import DAO.UsuarioDAO;
import DAO.LibroDAO;
import DAO.IMPL.ReservaDAOImpl;
import DAO.IMPL.UsuarioDAOImpl;
import DAO.IMPL.LibroDAOImpl;
import DAO.IMPL.LogDAOImpl;
import DAO.LogDAO;
import Modelo.Reserva;
import Modelo.Usuario;
import Modelo.Libro;
import Modelo.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;


import Modelo.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import Modelo.Log;
import Observer.LibroNotificador;
import Observer.UsuarioNotificacion;
import Servicio.LogServicio;
import Util.SecurityUtil;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.json.JSONObject;


public class Main {




}
    /** Para rastrear notificaciones en las pruebas
    private static List<String> notificacionesRecibidas = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE BIBLIOTECA ===");
        System.out.println("---------------------------------------\n");
        
        // Ejecutar pruebas
        ejecutarPruebaSecurityUtil();
        pausar();
        ejecutarPruebaObserverSimple();
        pausar();
        ejecutarPruebaObserverExtendida();
        
        System.out.println("\n\n=== TODAS LAS PRUEBAS COMPLETADAS ===");
    }
    
    /**
     * Prueba de SecurityUtil
     
    private static void ejecutarPruebaSecurityUtil() {
        System.out.println("\n=== 1. PROBANDO SECURITYUTIL ===\n");
        
        // Prueba de hash de contraseñas
        String password = "miContraseña123";
        String hashedPassword = SecurityUtil.hashPassword(password);
        
        System.out.println("Contraseña original: " + password);
        System.out.println("Contraseña hasheada: " + hashedPassword);
        
        // Verificación de contraseñas
        boolean verificacionCorrecta = SecurityUtil.verifyPassword(password, hashedPassword);
        boolean verificacionIncorrecta = SecurityUtil.verifyPassword("contraseñaErronea", hashedPassword);
        
        System.out.println("Verificación con contraseña correcta: " + verificacionCorrecta);
        System.out.println("Verificación con contraseña incorrecta: " + verificacionIncorrecta);
        
        // Generación de token
        String token = SecurityUtil.generateToken();
        System.out.println("Token generado: " + token);
    }
    
    /**
     * Prueba básica del patrón Observer
     
    private static void ejecutarPruebaObserverSimple() {
        System.out.println("\n=== 2. PROBANDO PATRÓN OBSERVER (BÁSICO) ===\n");
        
        try {
            // Crear mock de ReservaDAO para pruebas
            ReservaDAO mockReservaDAO = crearMockReservaDAO();
            
            // Crear libro (observable)
            Libro libro = new Libro();
            libro.setIdLibro(1);
            libro.setTitulo("Don Quijote de la Mancha");
            libro.setIsbn("9788420412146");
            libro.setFechaAdquisicion(new Date());
            libro.setIdEstado(2); // 2 = prestado (no disponible)
            
            System.out.println("Libro creado: " + libro.getTitulo() + " (ID: " + libro.getIdLibro() + ")");
            
            // Crear notificador
            LibroNotificador notificador = new LibroNotificador(libro);
            
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1);
            usuario.setNombre("Usuario Test");
            
            System.out.println("Usuario creado: " + usuario.getNombre());
            
            // Crear notificación y registrar observador
            UsuarioNotificacion usuarioNotificacion = new UsuarioNotificacion(usuario, mockReservaDAO);
            notificador.registerObserver(usuarioNotificacion);
            
            System.out.println("Notificación configurada y observador registrado");
            
            // Cambiar estado del libro a disponible
            System.out.println("\nCambiando estado del libro a disponible...");
            notificador.setDisponible(true);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Prueba extendida del patrón Observer
     
    private static void ejecutarPruebaObserverExtendida() {
        System.out.println("\n=== 3. PROBANDO PATRÓN OBSERVER (EXTENDIDO) ===\n");
        notificacionesRecibidas.clear();
        
        try {
            // 1. Crear libro y notificador
            Libro libro = new Libro();
            libro.setIdLibro(2);
            libro.setTitulo("Cien años de soledad");
            libro.setIsbn("9788497592208");
            libro.setFechaAdquisicion(new Date());
            libro.setIdEstado(2); // No disponible
            
            LibroNotificador notificador = new LibroNotificador(libro);
            System.out.println("Libro creado: " + libro.getTitulo());
            
            // 2. Crear varios usuarios y observadores
            Usuario usuario1 = new Usuario();
            usuario1.setIdUsuario(1);
            usuario1.setNombre("Ana García");
            
            Usuario usuario2 = new Usuario();
            usuario2.setIdUsuario(2);
            usuario2.setNombre("Carlos López");
            
            UsuarioNotificacion observador1 = new UsuarioNotificacion(usuario1, crearMockReservaDAO()) {
               
                protected void enviarNotificacion(Libro libro) {
                    notificacionesRecibidas.add("Ana García recibió notificación sobre " + libro.getTitulo());
                    System.out.println("🔔 Ana García recibió notificación sobre " + libro.getTitulo());
                }
            };
            
            UsuarioNotificacion observador2 = new UsuarioNotificacion(usuario2, crearMockReservaDAO()) {
               
                protected void enviarNotificacion(Libro libro) {
                    notificacionesRecibidas.add("Carlos López recibió notificación sobre " + libro.getTitulo());
                    System.out.println("🔔 Carlos López recibió notificación sobre " + libro.getTitulo());
                }
            };
            
            // 3. PRUEBA: Registrar múltiples observadores
            System.out.println("\n3.1 Probando múltiples observadores:");
            notificador.registerObserver(observador1);
            notificador.registerObserver(observador2);
            
            System.out.println("- Dos observadores registrados");
            System.out.println("- Cambiando libro a disponible...");
            notificador.setDisponible(true);
            
            System.out.println("- Notificaciones enviadas: " + notificacionesRecibidas.size());
            // Aquí ambos observadores deberían haber recibido notificaciones
            
            // 4. PRUEBA: RemoveObserver
            System.out.println("\n3.2 Probando removeObserver:");
            notificacionesRecibidas.clear();
            notificador.setDisponible(false); // Resetear
            
            System.out.println("- Eliminando a Ana García como observador");
            notificador.removeObserver(observador1);
            
            System.out.println("- Cambiando libro a disponible nuevamente...");
            notificador.setDisponible(true);
            
            System.out.println("- Notificaciones enviadas: " + notificacionesRecibidas.size());
            System.out.println("- Sólo Carlos López debería haber recibido notificación");
            
            // 5. PRUEBA: setDisponible(false) no envía notificaciones
            System.out.println("\n3.3 Probando que setDisponible(false) no envía notificaciones:");
            notificacionesRecibidas.clear();
            
            System.out.println("- Cambiando libro a NO disponible...");
            notificador.setDisponible(false);
            
            System.out.println("- Notificaciones enviadas: " + notificacionesRecibidas.size());
            System.out.println("- (Debería ser 0, ya que setDisponible(false) no debe enviar notificaciones)");
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba extendida: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un mock de ReservaDAO para pruebas
     
    private static ReservaDAO crearMockReservaDAO() {
        return new ReservaDAO() {
            @Override
            public void insertar(Reserva reserva) throws Exception {}
            
            @Override
            public void actualizar(Reserva reserva) throws Exception {}
            
            @Override
            public void eliminar(Integer id) throws Exception {}
            
            @Override
            public List<Reserva> obtenerTodos() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public Reserva obtenerPorId(Integer id) throws Exception {
                return null;
            }
            
            @Override
            public void eliminarTodos() throws Exception {}
            
            @Override
            public List<Reserva> buscarPorUsuario(Integer idUsuario) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorLibro(Integer idLibro) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorEstado(Integer idEstado) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorFechas(Date fechaInicio, Date fechaFin) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public void cambiarEstado(Integer idReserva, Integer idEstado) throws Exception {}
            
            @Override
            public List<Reserva> obtenerReservasVencidas() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> obtenerReservasPendientes() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public boolean tieneReservaActiva(Integer idUsuario, Integer idLibro) throws Exception {
                return true; // Simular que el usuario tiene una reserva activa
            }
            
            @Override
            public int contarReservasActivas(Integer idUsuario) throws Exception {
                return 1;
            }
        };
    }
    
    /**
     * Pausar para esperar entrada del usuario
    
    private static void pausar() {
        System.out.println("\nPresione ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE BIBLIOTECA ===");
        System.out.println("---------------------------------------\n");
        
        // Ejecutar pruebas
        ejecutarPruebaSecurityUtil();
        pausar();
        ejecutarPruebaObserver();
        
        System.out.println("\n\n=== TODAS LAS PRUEBAS COMPLETADAS ===");
    }
    
   
    private static void ejecutarPruebaSecurityUtil() {
        System.out.println("\n=== 1. PROBANDO SECURITYUTIL ===\n");
        
        // Prueba de hash de contraseñas
        String password = "miContraseña123";
        String hashedPassword = SecurityUtil.hashPassword(password);
        
        System.out.println("Contraseña original: " + password);
        System.out.println("Contraseña hasheada: " + hashedPassword);
        
        // Verificación de contraseñas
        boolean verificacionCorrecta = SecurityUtil.verifyPassword(password, hashedPassword);
        boolean verificacionIncorrecta = SecurityUtil.verifyPassword("contraseñaErronea", hashedPassword);
        
        System.out.println("Verificación con contraseña correcta: " + verificacionCorrecta);
        System.out.println("Verificación con contraseña incorrecta: " + verificacionIncorrecta);
        
        // Generación de token
        String token = SecurityUtil.generateToken();
        System.out.println("Token generado: " + token);
    }
    
   
    private static void ejecutarPruebaObserver() {
        System.out.println("\n=== 2. PROBANDO PATRÓN OBSERVER ===\n");
        
        try {
            // Crear mock de ReservaDAO para pruebas
            ReservaDAO mockReservaDAO = crearMockReservaDAO();
            
            // Crear libro (observable)
            Libro libro = new Libro();
            libro.setIdLibro(1);
            libro.setTitulo("Don Quijote de la Mancha");
            libro.setIsbn("9788420412146");
            libro.setFechaAdquisicion(new Date());
            libro.setIdEstado(2); // 2 = prestado (no disponible)
            
            System.out.println("Libro creado: " + libro.getTitulo() + " (ID: " + libro.getIdLibro() + ")");
            
            // Crear notificador
            LibroNotificador notificador = new LibroNotificador(libro);
            
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1);
            usuario.setNombre("Usuario Test");
            
            System.out.println("Usuario creado: " + usuario.getNombre());
            
            // Crear notificación y registrar observador
            UsuarioNotificacion usuarioNotificacion = new UsuarioNotificacion(usuario, mockReservaDAO);
            notificador.registerObserver(usuarioNotificacion);
            
            System.out.println("Notificación configurada y observador registrado");
            
            // Cambiar estado del libro a disponible
            System.out.println("\nCambiando estado del libro a disponible...");
            notificador.setDisponible(true);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
   
    private static ReservaDAO crearMockReservaDAO() {
        return new ReservaDAO() {
            @Override
            public void insertar(Reserva reserva) throws Exception {}
            
            @Override
            public void actualizar(Reserva reserva) throws Exception {}
            
            @Override
            public void eliminar(Integer id) throws Exception {}
            
            @Override
            public List<Reserva> obtenerTodos() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public Reserva obtenerPorId(Integer id) throws Exception {
                return null;
            }
            
            @Override
            public void eliminarTodos() throws Exception {}
            
            @Override
            public List<Reserva> buscarPorUsuario(Integer idUsuario) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorLibro(Integer idLibro) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorEstado(Integer idEstado) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> buscarPorFechas(Date fechaInicio, Date fechaFin) throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public void cambiarEstado(Integer idReserva, Integer idEstado) throws Exception {}
            
            @Override
            public List<Reserva> obtenerReservasVencidas() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public List<Reserva> obtenerReservasPendientes() throws Exception {
                return new ArrayList<>();
            }
            
            @Override
            public boolean tieneReservaActiva(Integer idUsuario, Integer idLibro) throws Exception {
                return true; // Simular que el usuario tiene una reserva activa
            }
            
            @Override
            public int contarReservasActivas(Integer idUsuario) throws Exception {
                return 1;
            }
        };
    }
    
    
    private static void pausar() {
        System.out.println("\nPresione ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
   
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        
        
        
    
        LogServicio logServicio = new LogServicio();
        
        try {
            // 1. Registrar algunos logs de ejemplo
            System.out.println("=== Registrando logs de ejemplo ===");
            
            // Log de inicio de sesión de usuario
            JSONObject detallesLogin = new JSONObject();
            detallesLogin.put("ip", "192.168.1.100");
            detallesLogin.put("navegador", "Chrome");
            detallesLogin.put("version", "98.0.4758.102");
            
            logServicio.registrarLog(
                1, // id_tipo_log (usuario)
                1, // id_usuario
                "Inicio de sesión",
                detallesLogin.toString()
            );
            System.out.println("Log de inicio de sesión registrado");
            
            // Log de préstamo de libro
            JSONObject detallesPrestamo = new JSONObject();
            detallesPrestamo.put("id_libro", 1);
            detallesPrestamo.put("titulo", "El Quijote");
            detallesPrestamo.put("dias_prestamo", 15);
            
            logServicio.registrarLog(
                3, // id_tipo_log (prestamo)
                1, // id_usuario
                "Nuevo préstamo",
                detallesPrestamo.toString()
            );
            System.out.println("Log de préstamo registrado");
            
            // 2. Consultar todos los logs
            System.out.println("\n=== Consultando todos los logs ===");
            List<Log> todosLogs = logServicio.obtenerTodosLogs();
            mostrarLogs(todosLogs);
            
            // 3. Consultar logs por tipo
            System.out.println("\n=== Consultando logs de tipo usuario ===");
            List<Log> logsUsuario = logServicio.obtenerLogsPorTipo(1);
            mostrarLogs(logsUsuario);
            
            // 4. Consultar logs por usuario
            System.out.println("\n=== Consultando logs del usuario 1 ===");
            List<Log> logsUsuarioEspecifico = logServicio.obtenerLogsPorUsuario(1);
            mostrarLogs(logsUsuarioEspecifico);
            
            // 5. Consultar logs por rango de fecha
            System.out.println("\n=== Consultando logs de la última hora ===");
            LocalDateTime fechaFin = LocalDateTime.now();
            LocalDateTime fechaInicio = fechaFin.minusHours(1);
            List<Log> logsRecientes = logServicio.obtenerLogsPorFecha(fechaInicio, fechaFin);
            mostrarLogs(logsRecientes);
            
        } catch (SQLException e) {
            System.err.println("Error en la base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void mostrarLogs(List<Log> logs) {
        if (logs.isEmpty()) {
            System.out.println("No se encontraron logs");
            return;
        }
        
        for (Log log : logs) {
            System.out.println("----------------------------------------");
            System.out.println("ID: " + log.getIdLog());
            System.out.println("Tipo: " + log.getIdTipoLog());
            System.out.println("Usuario: " + (log.getIdUsuario() != null ? log.getIdUsuario() : "Sistema"));
            System.out.println("Acción: " + log.getAccion());
            System.out.println("Fecha: " + log.getFecha().format(formatter));
            System.out.println("Detalles: " + log.getDetalles());
        }
    }
}
/*TESTING PRESTAMO 3:      
        try {
            // Inicialización de DAOs
            PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            LibroDAO libroDAO = new LibroDAOImpl();
            
            // Limpiar datos existentes
            System.out.println("\n--- Limpiando datos existentes ---");
            prestamoDAO.eliminarTodos();
            usuarioDAO.eliminarTodos();
            libroDAO.eliminarTodos();
            
            // Crear usuario de prueba
            Usuario usuario = crearUsuario(usuarioDAO);
            
            // Crear libros de prueba
            List<Libro> libros = crearLibros(libroDAO);
            
            // Prueba 1: Crear 3 préstamos iniciales
            System.out.println("\n--- Prueba 1: Crear 3 préstamos iniciales ---");
            List<Prestamo> prestamos = crear3PrestamosIniciales(prestamoDAO, usuario, libros);
            if (prestamos.isEmpty() || prestamos.get(0).getIdPrestamo() == null) {
                throw new Exception("Error: No se pudo obtener el ID del primer préstamo");
            }

            
            mostrarPrestamosActivos(prestamoDAO);
            
            // Prueba 2: Intentar crear un cuarto préstamo (debe fallar)
            System.out.println("\n--- Prueba 2: Intentar crear un cuarto préstamo ---");
            intentarCrearCuartoPrestamo(prestamoDAO, usuario, libros.get(3));
            mostrarPrestamosActivos(prestamoDAO);
            
            // Prueba 3: Eliminar el primer préstamo y verificar que el segundo pasa a ser el primero
            System.out.println("\n--- Prueba 3: Eliminar primer préstamo ---");
            prestamoDAO.eliminar(prestamos.get(0).getIdPrestamo());
            System.out.println("Préstamo 1 eliminado. Verificando orden actualizado:");
            mostrarPrestamosActivos(prestamoDAO);
            
            // Prueba 4: Crear un nuevo préstamo después de eliminar uno
            System.out.println("\n--- Prueba 4: Crear nuevo préstamo tras eliminar uno ---");
            Prestamo nuevoPrestamo = crearPrestamo(usuario.getIdUsuario(), libros.get(3).getIdLibro());
            prestamoDAO.insertar(nuevoPrestamo);
            System.out.println("Nuevo préstamo creado. Estado actual:");
            mostrarPrestamosActivos(prestamoDAO);
            
            // Prueba 5: Devolver un préstamo y verificar que se puede crear otro
            System.out.println("\n--- Prueba 5: Devolver un préstamo y crear otro ---");
            prestamoDAO.registrarDevolucion(prestamos.get(1).getIdPrestamo(), new Date());
            System.out.println("Préstamo devuelto. Creando nuevo préstamo:");
            Prestamo otroPrestamo = crearPrestamo(usuario.getIdUsuario(), libros.get(0).getIdLibro());
            prestamoDAO.insertar(otroPrestamo);
            mostrarPrestamosActivos(prestamoDAO);
            
            System.out.println("\nPruebas completadas exitosamente.");
            
        } catch (Exception e) {
            System.err.println("Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Usuario crearUsuario(UsuarioDAO usuarioDAO) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Test");
        usuario.setPassword("123456");
        usuario.setIdRol(2); // Usuario regular
        usuario.setMulta(0.0);
        usuario.setActivo(true);
        usuarioDAO.insertar(usuario);
        System.out.println("Usuario creado con ID: " + usuario.getIdUsuario());
        return usuario;
    }
    
    private static List<Libro> crearLibros(LibroDAO libroDAO) throws Exception {
        List<Libro> libros = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Libro libro = new Libro();
            libro.setTitulo("Libro Test " + (i + 1));
            libro.setIsbn("TEST" + (1000 + i));
            libro.setFechaAdquisicion(new Date());
            libro.setIdEstado(1); // Disponible
            libroDAO.insertar(libro);
            libros.add(libro);
            System.out.println("Libro creado con ID: " + libro.getIdLibro());
        }
        return libros;
    }
    
    private static List<Prestamo> crear3PrestamosIniciales(PrestamoDAO prestamoDAO, Usuario usuario, List<Libro> libros) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Prestamo prestamo = crearPrestamo(usuario.getIdUsuario(), libros.get(i).getIdLibro());
            prestamoDAO.insertar(prestamo);
            prestamos.add(prestamo);
            System.out.println("Préstamo " + (i + 1) + " creado con ID: " + prestamo.getIdPrestamo());
        }
        return prestamos;
    }
    
    private static Prestamo crearPrestamo(Integer idUsuario, Integer idLibro) {
        Prestamo prestamo = new Prestamo();
        prestamo.setIdUsuario(idUsuario);
        prestamo.setIdLibro(idLibro);
        prestamo.setIdTipoPrestamo(1);
        prestamo.setFechaPrestamo(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        prestamo.setFechaDevolucionEsperada(calendar.getTime());
        return prestamo;
    }
    
    private static void intentarCrearCuartoPrestamo(PrestamoDAO prestamoDAO, Usuario usuario, Libro libro) {
        try {
            Prestamo cuartoPrestamo = crearPrestamo(usuario.getIdUsuario(), libro.getIdLibro());
            prestamoDAO.insertar(cuartoPrestamo);
            System.out.println("¡Advertencia! Se permitió crear un cuarto préstamo");
        } catch (Exception e) {
            System.out.println("Correcto: No se permitió crear un cuarto préstamo. " + e.getMessage());
        }
    }
    
    private static void mostrarPrestamosActivos(PrestamoDAO prestamoDAO) throws Exception {
        System.out.println("\nPréstamos activos actuales:");
        List<Prestamo> prestamosActivos = prestamoDAO.obtenerPrestamosActivos();
        if (prestamosActivos.isEmpty()) {
            System.out.println("No hay préstamos activos");
        } else {
            for (Prestamo p : prestamosActivos) {
                System.out.println("ID: " + p.getIdPrestamo() + 
                                 " | Usuario: " + p.getIdUsuario() +
                                 " | Libro: " + p.getIdLibro() +
                                 " | Fecha: " + p.getFechaPrestamo());
            }
        }
    }
}    
  ------------------------------------------------------------------
        
        TESTING prestamo 2
        
        
        try {
            PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            LibroDAO libroDAO = new LibroDAOImpl();

            // 0. Limpiar tablas
            System.out.println("0. Limpiando tablas...");
            prestamoDAO.eliminarTodos();
            usuarioDAO.eliminarTodos();
            libroDAO.eliminarTodos();

            // 1. Crear datos de prueba
            System.out.println("Creando datos de prueba...");

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario Test Prestamos");
            usuario.setPassword("123456");
            usuario.setIdRol(2);
            usuario.setMulta(0.0);
            usuario.setActivo(true);
            usuarioDAO.insertar(usuario);
            System.out.println("Usuario creado con ID: " + usuario.getIdUsuario());

            // Crear libros
            Libro[] libros = new Libro[4];
            for (int i = 0; i < 4; i++) {
                libros[i] = new Libro();
                libros[i].setTitulo("Libro Test " + (i + 1));
                libros[i].setIsbn("TEST" + (1000 + i));
                libros[i].setFechaAdquisicion(new Date());
                libros[i].setIdEstado(1);
                libroDAO.insertar(libros[i]);
                System.out.println("Libro " + (i + 1) + " creado con ID: " + libros[i].getIdLibro());
            }

            // Crear préstamos
            List<Prestamo> prestamos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdUsuario(usuario.getIdUsuario());
                prestamo.setIdLibro(libros[i].getIdLibro());
                prestamo.setIdTipoPrestamo(1);
                prestamo.setFechaPrestamo(new Date());

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                prestamo.setFechaDevolucionEsperada(calendar.getTime());

                prestamoDAO.insertar(prestamo);
                prestamos.add(prestamo);
                System.out.println("Préstamo " + (i + 1) + " creado con ID: " + prestamo.getIdPrestamo());
            }

            // Mostrar préstamos antes de eliminar
            System.out.println("Préstamos antes de eliminar:");
            prestamoDAO.obtenerTodos().forEach(System.out::println);

            // Eliminar el primer préstamo
            System.out.println("Eliminando préstamo 1...");
            prestamoDAO.eliminar(prestamos.get(0).getIdPrestamo());

            // Mostrar préstamos después de eliminar
            System.out.println("Préstamos después de eliminar:");
            prestamoDAO.obtenerTodos().forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        TESTING prestamo 1
       
        try {
            PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            LibroDAO libroDAO = new LibroDAOImpl();
            
            // 0. Limpiar tablas
            System.out.println("0. Limpiando tablas...");
            prestamoDAO.eliminarTodos();
            usuarioDAO.eliminarTodos();
            libroDAO.eliminarTodos();
            
            // 1. Crear datos de prueba
            System.out.println("\n1. Creando datos de prueba...");
            
           
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario Test Prestamos");
            usuario.setPassword("123456");
            usuario.setIdRol(2); // Usuario regular
            usuario.setMulta(0.0);
            usuario.setActivo(true);
            usuarioDAO.insertar(usuario);
            System.out.println("Usuario creado con ID: " + usuario.getIdUsuario());
            
            // Crear libros
            Libro[] libros = new Libro[4];
            for (int i = 0; i < 4; i++) {
                libros[i] = new Libro();
                libros[i].setTitulo("Libro Test " + (i + 1));
                libros[i].setIsbn("TEST" + (1000 + i));
                libros[i].setFechaAdquisicion(new Date());
                libros[i].setIdEstado(1); // Disponible
                libroDAO.insertar(libros[i]);
                System.out.println("Libro " + (i + 1) + " creado con ID: " + libros[i].getIdLibro());
            }
            
            // 2. Probar préstamos regulares (máximo 3)
            System.out.println("\n2. Probando préstamos regulares...");
            
            // Crear 3 préstamos (debe permitir)
            Prestamo[] prestamos = new Prestamo[3];
            for (int i = 0; i < 3; i++) {
                prestamos[i] = new Prestamo();
                prestamos[i].setIdUsuario(usuario.getIdUsuario());
                prestamos[i].setIdLibro(libros[i].getIdLibro());
                prestamos[i].setIdTipoPrestamo(1); // Regular
                prestamos[i].setFechaPrestamo(new Date());
                
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30); // 30 días para préstamo regular
                prestamos[i].setFechaDevolucionEsperada(calendar.getTime());
                
                prestamoDAO.insertar(prestamos[i]);
                System.out.println("Préstamo " + (i + 1) + " creado con ID: " + prestamos[i].getIdPrestamo());
            }
            
            // 3. Intentar crear un cuarto préstamo (debe fallar)
            System.out.println("\n3. Intentando crear un cuarto préstamo...");
            try {
                Prestamo prestamoExtra = new Prestamo();
                prestamoExtra.setIdUsuario(usuario.getIdUsuario());
                prestamoExtra.setIdLibro(libros[3].getIdLibro());
                prestamoExtra.setIdTipoPrestamo(1);
                prestamoExtra.setFechaPrestamo(new Date());
                
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                prestamoExtra.setFechaDevolucionEsperada(calendar.getTime());
                
                prestamoDAO.insertar(prestamoExtra);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 4. Probar devolución normal y express
            System.out.println("\n4. Probando devoluciones...");
            
            // Devolución normal (más de 7 días)
            Calendar calDevolucion = Calendar.getInstance();
            calDevolucion.add(Calendar.DAY_OF_MONTH, 10);
            prestamoDAO.registrarDevolucion(prestamos[0].getIdPrestamo(), calDevolucion.getTime());
            System.out.println("Préstamo 1 devuelto después de 10 días");
            
            // Devolución express (menos de 7 días)
            calDevolucion = Calendar.getInstance();
            calDevolucion.add(Calendar.DAY_OF_MONTH, 5);
            prestamoDAO.registrarDevolucion(prestamos[1].getIdPrestamo(), calDevolucion.getTime());
            System.out.println("Préstamo 2 devuelto después de 5 días (express)");
            
            // 5. Verificar préstamos express
            System.out.println("\n5. Verificando préstamos express:");
            List<Prestamo> prestamosExpress = prestamoDAO.obtenerPrestamosExpress();
            prestamosExpress.forEach(System.out::println);
            
            // 6. Probar búsquedas
            System.out.println("\n6. Probando búsquedas:");
            
            // Por usuario
            System.out.println("\na) Préstamos del usuario:");
            List<Prestamo> prestamosUsuario = prestamoDAO.buscarPorUsuario(usuario.getIdUsuario());
            prestamosUsuario.forEach(System.out::println);
            
            // Por libro
            System.out.println("\nb) Préstamos del primer libro:");
            List<Prestamo> prestamosLibro = prestamoDAO.buscarPorLibro(libros[0].getIdLibro());
            prestamosLibro.forEach(System.out::println);
            
            // Préstamos activos
            System.out.println("\nc) Préstamos activos:");
            List<Prestamo> prestamosActivos = prestamoDAO.obtenerPrestamosActivos();
            prestamosActivos.forEach(System.out::println);
            
            // 7. Probar eliminación
            System.out.println("\n7. Probando eliminación de préstamo...");
            prestamoDAO.eliminar(prestamos[2].getIdPrestamo());
            System.out.println("Préstamo 3 eliminado");
            
            // 8. Verificar estado final
            System.out.println("\n8. Estado final:");
            System.out.println("\na) Todos los préstamos:");
            prestamoDAO.obtenerTodos().forEach(System.out::println);
            
            System.out.println("\nb) Préstamos activos:");
            prestamoDAO.obtenerPrestamosActivos().forEach(System.out::println);
            
            System.out.println("\nc) Préstamos express:");
            prestamoDAO.obtenerPrestamosExpress().forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }

      

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
         
        
        
        
        
        
        
        try {
            ReservaDAO reservaDAO = new ReservaDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            LibroDAO libroDAO = new LibroDAOImpl();
            
            // 0. Limpiar todas las tablas relacionadas
            System.out.println("0. Limpiando tablas...");
            reservaDAO.eliminarTodos();
            usuarioDAO.eliminarTodos();
            libroDAO.eliminarTodos();
            
            // 1. Crear datos de prueba
            System.out.println("\n1. Creando datos de prueba...");
            
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario Test Reservas");  // Cambiado el nombre para evitar conflictos
            usuario.setPassword("123456");
            usuario.setIdRol(2);
            usuario.setMulta(0.0);
            usuario.setActivo(true);
            usuarioDAO.insertar(usuario);
            System.out.println("Usuario creado con ID: " + usuario.getIdUsuario());
            
            // Crear 4 libros diferentes
            Libro[] libros = new Libro[4];
            for (int i = 0; i < 4; i++) {
                libros[i] = new Libro();
                libros[i].setTitulo("Libro Test " + (i + 1));
                libros[i].setIsbn("TEST" + (1000 + i));
                libros[i].setFechaAdquisicion(new Date());
                libros[i].setIdEstado(1);
                libroDAO.insertar(libros[i]);
                System.out.println("Libro " + (i + 1) + " creado con ID: " + libros[i].getIdLibro());
            }
            // 2. Probar límite de reservas
            System.out.println("\n2. Probando límite de reservas...");
            
            // Crear 3 reservas (debe permitir)
            for (int i = 0; i < 3; i++) {
                Reserva reserva = new Reserva();
                reserva.setIdUsuario(usuario.getIdUsuario());
                reserva.setIdLibro(libros[i].getIdLibro());
                reserva.setIdEstadoReserva(1); // Pendiente
                reserva.setFechaReserva(new Date());
                
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                reserva.setFechaVencimiento(calendar.getTime());
                
                reservaDAO.insertar(reserva);
                System.out.println("Reserva " + (i + 1) + " creada con ID: " + reserva.getIdReserva());
            }
            
            // Intentar crear una cuarta reserva (debe fallar)
            System.out.println("\n3. Intentando crear una cuarta reserva...");
            try {
                Reserva reservaExtra = new Reserva();
                reservaExtra.setIdUsuario(usuario.getIdUsuario());
                reservaExtra.setIdLibro(libros[3].getIdLibro());
                reservaExtra.setIdEstadoReserva(1);
                reservaExtra.setFechaReserva(new Date());
                
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                reservaExtra.setFechaVencimiento(calendar.getTime());
                
                reservaDAO.insertar(reservaExtra);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 4. Probar diferentes estados
            System.out.println("\n4. Probando cambios de estado...");
            List<Reserva> reservas = reservaDAO.obtenerTodos();
            if (!reservas.isEmpty()) {
                // Cambiar primera reserva a completada
                reservaDAO.cambiarEstado(reservas.get(0).getIdReserva(), 2); // Completada
                // Cambiar segunda reserva a vencida
                reservaDAO.cambiarEstado(reservas.get(1).getIdReserva(), 3); // Vencida
                // Tercera reserva queda pendiente
            }
            
            // 5. Probar búsqueda por fechas
            System.out.println("\n5. Probando búsqueda por fechas...");
            Calendar calInicio = Calendar.getInstance();
            calInicio.add(Calendar.DAY_OF_MONTH, -1);
            
            Calendar calFin = Calendar.getInstance();
            calFin.add(Calendar.DAY_OF_MONTH, 1);
            
            List<Reserva> reservasPorFecha = reservaDAO.buscarPorFechas(calInicio.getTime(), calFin.getTime());
            System.out.println("Reservas encontradas en el rango de fechas: " + reservasPorFecha.size());
            reservasPorFecha.forEach(System.out::println);
            
            // 6. Probar reserva vencida
            System.out.println("\n6. Creando reserva con fecha vencida...");
            // Primero completar una reserva para liberar espacio
            reservaDAO.cambiarEstado(reservas.get(0).getIdReserva(), 2); // Completada
            
            Reserva reservaVencida = new Reserva();
            reservaVencida.setIdUsuario(usuario.getIdUsuario());
            reservaVencida.setIdLibro(libros[3].getIdLibro());
            reservaVencida.setIdEstadoReserva(1);
            reservaVencida.setFechaReserva(new Date());
            
            // Establecer fecha de vencimiento en el pasado
            Calendar calVencida = Calendar.getInstance();
            calVencida.add(Calendar.DAY_OF_MONTH, -1);
            reservaVencida.setFechaVencimiento(calVencida.getTime());
            
            reservaDAO.insertar(reservaVencida);
            System.out.println("Reserva vencida creada con ID: " + reservaVencida.getIdReserva());
            
            // 7. Verificar estado final
            System.out.println("\n7. Estado final de las reservas:");
            System.out.println("\na) Todas las reservas:");
            reservaDAO.obtenerTodos().forEach(System.out::println);
            
            System.out.println("\nb) Reservas pendientes:");
            reservaDAO.obtenerReservasPendientes().forEach(System.out::println);
            
            System.out.println("\nc) Reservas vencidas:");
            reservaDAO.obtenerReservasVencidas().forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        
        TESTING RESERVA 1
        try {
            ReservaDAO reservaDAO = new ReservaDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            LibroDAO libroDAO = new LibroDAOImpl();
            
            // 0. Limpiar tablas
            System.out.println("0. Limpiando tablas...");
            reservaDAO.eliminarTodos();
            
            // 1. Crear datos de prueba (usuarios y libros necesarios)
            System.out.println("\n1. Creando datos de prueba...");
            
            // Crear usuario
            Usuario usuario1 = new Usuario();
            usuario1.setNombre("Usuario Prueba");
            usuario1.setPassword("123456");
            usuario1.setIdRol(2);
            usuario1.setMulta(0.0);
            usuario1.setActivo(true);
            usuarioDAO.insertar(usuario1);
            System.out.println("Usuario creado con ID: " + usuario1.getIdUsuario());
            
            // Crear libro
            Libro libro1 = new Libro();
            libro1.setTitulo("Libro de Prueba");
            libro1.setIsbn("1234567890");
            libro1.setFechaAdquisicion(new Date());
            libro1.setIdEstado(1);
            libroDAO.insertar(libro1);
            System.out.println("Libro creado con ID: " + libro1.getIdLibro());
            
            // 2. Crear primera reserva
            System.out.println("\n2. Creando primera reserva...");
            Reserva reserva1 = new Reserva();
            reserva1.setIdUsuario(usuario1.getIdUsuario());
            reserva1.setIdLibro(libro1.getIdLibro());
            reserva1.setIdEstadoReserva(1); // Pendiente
            reserva1.setFechaReserva(new Date());
            
            // Establecer fecha de vencimiento (7 días después)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            reserva1.setFechaVencimiento(calendar.getTime());
            
            reservaDAO.insertar(reserva1);
            System.out.println("Reserva creada con ID: " + reserva1.getIdReserva());
            
            // 3. Intentar crear otra reserva para el mismo libro y usuario (debe fallar)
            System.out.println("\n3. Intentando crear reserva duplicada...");
            try {
                Reserva reservaDuplicada = new Reserva();
                reservaDuplicada.setIdUsuario(usuario1.getIdUsuario());
                reservaDuplicada.setIdLibro(libro1.getIdLibro());
                reservaDuplicada.setIdEstadoReserva(1);
                reservaDuplicada.setFechaReserva(new Date());
                reservaDuplicada.setFechaVencimiento(calendar.getTime());
                
                reservaDAO.insertar(reservaDuplicada);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 4. Cambiar estado de la reserva
            System.out.println("\n4. Cambiando estado de la reserva...");
            reservaDAO.cambiarEstado(reserva1.getIdReserva(), 2); // Completada
            
            // 5. Verificar el cambio
            Reserva reservaActualizada = reservaDAO.obtenerPorId(reserva1.getIdReserva());
            System.out.println("Estado actualizado de la reserva: " + reservaActualizada.getIdEstadoReserva());
            
            // 6. Búsquedas
            System.out.println("\n6. Probando búsquedas:");
            
            // Por usuario
            System.out.println("\na) Reservas del usuario:");
            List<Reserva> reservasUsuario = reservaDAO.buscarPorUsuario(usuario1.getIdUsuario());
            reservasUsuario.forEach(System.out::println);
            
            // Por libro
            System.out.println("\nb) Reservas del libro:");
            List<Reserva> reservasLibro = reservaDAO.buscarPorLibro(libro1.getIdLibro());
            reservasLibro.forEach(System.out::println);
            
            // Por estado
            System.out.println("\nc) Reservas por estado (completadas):");
            List<Reserva> reservasEstado = reservaDAO.buscarPorEstado(2);
            reservasEstado.forEach(System.out::println);
            
            // 7. Verificar reservas pendientes y vencidas
            System.out.println("\n7. Verificando reservas especiales:");
            
            System.out.println("\na) Reservas pendientes:");
            List<Reserva> reservasPendientes = reservaDAO.obtenerReservasPendientes();
            if (reservasPendientes.isEmpty()) {
                System.out.println("No hay reservas pendientes");
            } else {
                reservasPendientes.forEach(System.out::println);
            }
            
            System.out.println("\nb) Reservas vencidas:");
            List<Reserva> reservasVencidas = reservaDAO.obtenerReservasVencidas();
            if (reservasVencidas.isEmpty()) {
                System.out.println("No hay reservas vencidas");
            } else {
                reservasVencidas.forEach(System.out::println);
            }
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
          
        TESTING contacto 2
        
        try {
            ContactoDAO contactoDAO = new ContactoDAOImpl();
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            
            // 0. Limpiar las tablas
            System.out.println("0. Limpiando las tablas...");
            contactoDAO.eliminarTodos();
            usuarioDAO.eliminarTodos();
            
            // 1. Crear usuarios de prueba
            System.out.println("\n1. Creando usuarios de prueba...");
            
            Usuario usuario1 = new Usuario();
            usuario1.setNombre("Usuario Uno");
            usuario1.setPassword("123456");
            usuario1.setIdRol(2); // rol usuario normal
            usuario1.setMulta(0.0);
            usuario1.setActivo(true);
            usuarioDAO.insertar(usuario1);
            System.out.println("Usuario 1 creado con ID: " + usuario1.getIdUsuario());
            
            Usuario usuario2 = new Usuario();
            usuario2.setNombre("Usuario Dos");
            usuario2.setPassword("654321");
            usuario2.setIdRol(2);
            usuario2.setMulta(0.0);
            usuario2.setActivo(true);
            usuarioDAO.insertar(usuario2);
            System.out.println("Usuario 2 creado con ID: " + usuario2.getIdUsuario());
            
            // 2. Crear primer contacto (email)
            System.out.println("\n2. Creando primer contacto (email)...");
            Contacto contacto1 = new Contacto();
            contacto1.setIdUsuario(usuario1.getIdUsuario());
            contacto1.setIdTipoContacto(1); // email
            contacto1.setValor("usuario1@email.com");
            contacto1.setVerificado(false);
            
            contactoDAO.insertar(contacto1);
            System.out.println("Contacto 1 insertado con ID: " + contacto1.getIdContacto());
            
            // 3. Intentar crear otro contacto con el mismo tipo y valor
            System.out.println("\n3. Intentando crear contacto duplicado...");
            try {
                Contacto contactoDuplicado = new Contacto();
                contactoDuplicado.setIdUsuario(usuario2.getIdUsuario()); // Diferente usuario
                contactoDuplicado.setIdTipoContacto(1); // Mismo tipo (email)
                contactoDuplicado.setValor("usuario1@email.com"); // Mismo valor
                contactoDuplicado.setVerificado(false);
                
                contactoDAO.insertar(contactoDuplicado);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 4. Crear contacto con mismo tipo pero diferente valor
            System.out.println("\n4. Creando contacto con mismo tipo pero diferente valor...");
            Contacto contacto2 = new Contacto();
            contacto2.setIdUsuario(usuario2.getIdUsuario());
            contacto2.setIdTipoContacto(1); // email
            contacto2.setValor("usuario2@email.com"); // Diferente valor
            contacto2.setVerificado(false);
            
            contactoDAO.insertar(contacto2);
            System.out.println("Contacto 2 insertado con ID: " + contacto2.getIdContacto());
            
            // 5. Crear contacto con mismo valor pero diferente tipo
            System.out.println("\n5. Creando contacto con mismo valor pero diferente tipo...");
            Contacto contacto3 = new Contacto();
            contacto3.setIdUsuario(usuario1.getIdUsuario());
            contacto3.setIdTipoContacto(2); // teléfono
            contacto3.setValor("usuario1@email.com"); // Mismo valor que contacto1
            contacto3.setVerificado(false);
            
            contactoDAO.insertar(contacto3);
            System.out.println("Contacto 3 insertado con ID: " + contacto3.getIdContacto());
            
            // 6. Intentar actualizar un contacto a un valor+tipo que ya existe
            System.out.println("\n6. Intentando actualizar contacto a valor+tipo existente...");
            try {
                contacto2.setValor("usuario1@email.com"); // Valor que ya existe para tipo 1
                contactoDAO.actualizar(contacto2);
            } catch (Exception e) {
                System.out.println("Error esperado en actualización: " + e.getMessage());
            }
            
            // 7. Mostrar todos los contactos
            System.out.println("\n7. Lista final de contactos:");
            List<Contacto> contactos = contactoDAO.obtenerTodos();
            contactos.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error inesperado durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
          
        TESTING contacto 1
        
        try {
            ContactoDAO contactoDAO = new ContactoDAOImpl();
            
            // 0. Limpiar la tabla
            System.out.println("0. Limpiando la tabla de contactos...");
            contactoDAO.eliminarTodos();
            
            // 1. Crear contactos de prueba
            System.out.println("\n1. Creando contactos de prueba...");
            
            // Primer contacto (email)
            Contacto contacto1 = new Contacto();
            contacto1.setIdUsuario(1);
            contacto1.setIdTipoContacto(1); // Asumiendo 1 = email
            contacto1.setValor("usuario1@email.com");
            contacto1.setVerificado(false);
            
            contactoDAO.insertar(contacto1);
            System.out.println("Contacto 1 insertado con ID: " + contacto1.getIdContacto());
            
            // Segundo contacto (teléfono)
            Contacto contacto2 = new Contacto();
            contacto2.setIdUsuario(1);
            contacto2.setIdTipoContacto(2); // Asumiendo 2 = teléfono
            contacto2.setValor("123456789");
            contacto2.setVerificado(true);
            
            contactoDAO.insertar(contacto2);
            System.out.println("Contacto 2 insertado con ID: " + contacto2.getIdContacto());
            
            // 2. Mostrar todos los contactos
            System.out.println("\n2. Lista de todos los contactos:");
            List<Contacto> contactos = contactoDAO.obtenerTodos();
            contactos.forEach(System.out::println);
            
            // 3. Búsquedas
            System.out.println("\n3. Búsquedas:");
            
            // Por usuario
            System.out.println("\na) Contactos del usuario 1:");
            List<Contacto> contactosUsuario = contactoDAO.buscarPorUsuario(1);
            contactosUsuario.forEach(System.out::println);
            
            // Por tipo
            System.out.println("\nb) Contactos de tipo email (1):");
            List<Contacto> contactosTipo = contactoDAO.buscarPorTipo(1);
            contactosTipo.forEach(System.out::println);
            
            // Por valor
            System.out.println("\nc) Búsqueda por valor específico:");
            Contacto contactoEmail = contactoDAO.buscarPorValor("usuario1@email.com");
            System.out.println("Contacto encontrado: " + contactoEmail);
            
            // 4. Verificar un contacto
            System.out.println("\n4. Verificando contacto de email:");
            contactoDAO.verificarContacto(contacto1.getIdContacto());
            
            // 5. Mostrar contactos verificados y pendientes
            System.out.println("\n5. Estado de verificación:");
            
            System.out.println("\na) Contactos verificados:");
            List<Contacto> verificados = contactoDAO.obtenerContactosVerificados();
            verificados.forEach(System.out::println);
            
            System.out.println("\nb) Contactos pendientes de verificación:");
            List<Contacto> pendientes = contactoDAO.obtenerContactosPendientes();
            pendientes.forEach(System.out::println);
            
            // 6. Actualizar un contacto
            System.out.println("\n6. Actualizando contacto de teléfono:");
            contacto2.setValor("987654321");
            contactoDAO.actualizar(contacto2);
            
            // Verificar actualización
            Contacto contactoActualizado = contactoDAO.obtenerPorId(contacto2.getIdContacto());
            System.out.println("Contacto actualizado: " + contactoActualizado);
            
            // 7. Eliminar contactos y verificar
            System.out.println("\n7. Eliminando contactos y verificando eliminación:");
            contactoDAO.eliminar(contacto1.getIdContacto());
            contactoDAO.eliminar(contacto2.getIdContacto());
            
            List<Contacto> contactosFinales = contactoDAO.obtenerTodos();
            if (contactosFinales.isEmpty()) {
                System.out.println("No hay contactos en la base de datos");
            } else {
                System.out.println("Contactos restantes:");
                contactosFinales.forEach(System.out::println);
            }
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        
        TESTING LIBRO

        try {
            // Inicializar DAOs
            LibroDAO libroDAO = new LibroDAOImpl();
            AutorDAO autorDAO = new AutorDAOImpl();
            GeneroDAO generoDAO = new GeneroDAOImpl();
            CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
            
            // 0. Limpiar tablas
            System.out.println("0. Limpiando tablas...");
            libroDAO.eliminarTodos();
            autorDAO.eliminarTodos();
            generoDAO.eliminarTodos();
            categoriaDAO.eliminarTodos();
            
            // 1. Crear datos de prueba
            System.out.println("\n1. Creando datos de prueba...");
            
            // Crear autores
            Autor autor1 = new Autor();
            autor1.setNombre("Gabriel García Márquez");
            autor1.setNacionalidad("Colombiano");
            autorDAO.insertar(autor1);
            
            Autor autor2 = new Autor();
            autor2.setNombre("Mario Vargas Llosa");
            autor2.setNacionalidad("Peruano");
            autorDAO.insertar(autor2);
            
            // Crear géneros y categorías
            Genero genero1 = new Genero();
            genero1.setNombre("Novela");
            generoDAO.insertar(genero1);
            
            Genero genero2 = new Genero();
            genero2.setNombre("Realismo Mágico");
            generoDAO.insertar(genero2);
            
            Categoria categoria1 = new Categoria();
            categoria1.setNombre("Ficción");
            categoriaDAO.insertar(categoria1);
            
            Categoria categoria2 = new Categoria();
            categoria2.setNombre("Literatura Latinoamericana");
            categoriaDAO.insertar(categoria2);
            
            // 2. Crear primer libro
            System.out.println("\n2. Creando primer libro...");
            Libro libro1 = new Libro();
            libro1.setTitulo("Cien años de soledad");
            libro1.setIsbn("9780307474728");
            libro1.setFechaAdquisicion(new Date());
            libro1.setIdEstado(1);
            libro1.getAutores().add(autor1);
            libro1.getGeneros().add(genero1);
            libro1.getGeneros().add(genero2);
            libro1.getCategorias().add(categoria1);
            libro1.getCategorias().add(categoria2);
            
            libroDAO.insertar(libro1);
            System.out.println("Primer libro insertado con ID: " + libro1.getIdLibro());
            
            // 3. Crear segundo libro
            System.out.println("\n3. Creando segundo libro...");
            Libro libro2 = new Libro();
            libro2.setTitulo("La ciudad y los perros");
            libro2.setIsbn("9788420471839");
            libro2.setFechaAdquisicion(new Date());
            libro2.setIdEstado(1);
            libro2.getAutores().add(autor2);
            libro2.getGeneros().add(genero1);
            libro2.getCategorias().add(categoria1);
            libro2.getCategorias().add(categoria2);
            
            libroDAO.insertar(libro2);
            System.out.println("Segundo libro insertado con ID: " + libro2.getIdLibro());
            
            // 4. Mostrar todos los libros
            System.out.println("\n4. Lista actual de libros:");
            List<Libro> libros = libroDAO.obtenerTodos();
            libros.forEach(System.out::println);
            
            // 5. Pruebas de búsqueda
            System.out.println("\n5. Pruebas de búsqueda:");
            
            // Búsqueda por título
            System.out.println("\na) Búsqueda por título (soledad):");
            List<Libro> librosPorTitulo = libroDAO.buscarPorTitulo("soledad");
            librosPorTitulo.forEach(System.out::println);
            
            // Búsqueda por ISBN
            System.out.println("\nb) Búsqueda por ISBN:");
            Libro libroPorIsbn = libroDAO.buscarPorIsbn("9788420471839");
            System.out.println("Libro encontrado por ISBN: " + libroPorIsbn);
            
            // Búsqueda por autor
            System.out.println("\nc) Búsqueda por autor (García Márquez):");
            List<Libro> librosPorAutor = libroDAO.buscarPorAutor(autor1.getIdAutor());
            librosPorAutor.forEach(System.out::println);
            
            // Búsqueda por género
            System.out.println("\nd) Búsqueda por género (Novela):");
            List<Libro> librosPorGenero = libroDAO.buscarPorGenero(genero1.getIdGenero());
            librosPorGenero.forEach(System.out::println);
            
            // Búsqueda por categoría
            System.out.println("\ne) Búsqueda por categoría (Literatura Latinoamericana):");
            List<Libro> librosPorCategoria = libroDAO.buscarPorCategoria(categoria2.getIdCategoria());
            librosPorCategoria.forEach(System.out::println);
            
            // 6. Eliminar primer libro
            System.out.println("\n6. Eliminando primer libro...");
            libroDAO.eliminar(libro1.getIdLibro());
            System.out.println("Primer libro eliminado");
            
            // 7. Repetir búsquedas para verificar actualización
            System.out.println("\n7. Verificando búsquedas después de eliminar:");
            
            System.out.println("\na) Búsqueda por título (soledad) - debería estar vacía:");
            librosPorTitulo = libroDAO.buscarPorTitulo("soledad");
            if (librosPorTitulo.isEmpty()) {
                System.out.println("No se encontraron libros");
            } else {
                librosPorTitulo.forEach(System.out::println);
            }
            
            System.out.println("\nb) Búsqueda por autor (Vargas Llosa) - debería mostrar un libro:");
            librosPorAutor = libroDAO.buscarPorAutor(autor2.getIdAutor());
            librosPorAutor.forEach(System.out::println);
            
            // 8. Mostrar lista final
            System.out.println("\n8. Lista final de libros:");
            List<Libro> librosFinales = libroDAO.obtenerTodos();
            librosFinales.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
         TESTING DE categoria
        
        try {
            CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
            
            // 0. Limpiar la base de datos
            System.out.println("0. Limpiando la base de datos...");
            categoriaDAO.eliminarTodos();
            
            // 1. Verificar que está vacía
            System.out.println("\n1. Verificando que la base de datos está vacía:");
            List<Categoria> categoriasIniciales = categoriaDAO.obtenerTodos();
            if (categoriasIniciales.isEmpty()) {
                System.out.println("Base de datos vacía correctamente");
            } else {
                categoriasIniciales.forEach(System.out::println);
            }
            
            // 2. Insertar primera categoría
            System.out.println("\n2. Insertando primera categoría...");
            Categoria primerCategoria = new Categoria();
            primerCategoria.setNombre("Ficción");
            
            categoriaDAO.insertar(primerCategoria);
            System.out.println("Primera categoría insertada con ID: " + primerCategoria.getIdCategoria());
            
            // 3. Insertar segunda categoría
            System.out.println("\n3. Insertando segunda categoría...");
            Categoria segundaCategoria = new Categoria();
            segundaCategoria.setNombre("No Ficción");
            
            categoriaDAO.insertar(segundaCategoria);
            System.out.println("Segunda categoría insertada con ID: " + segundaCategoria.getIdCategoria());
            
            // 4. Mostrar lista actualizada
            System.out.println("\n4. Lista después de insertar dos categorías:");
            List<Categoria> categoriasTrasInsercion = categoriaDAO.obtenerTodos();
            categoriasTrasInsercion.forEach(System.out::println);
            
            // 5. Intentar insertar una categoría duplicada
            System.out.println("\n5. Intentando insertar categoría duplicada...");
            try {
                Categoria categoriaDuplicada = new Categoria();
                categoriaDuplicada.setNombre("Ficción");
                categoriaDAO.insertar(categoriaDuplicada);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 6. Eliminar primera categoría
            System.out.println("\n6. Eliminando la categoría Ficción...");
            categoriaDAO.eliminar(primerCategoria.getIdCategoria());
            System.out.println("Categoría eliminada correctamente");
            
            // 7. Mostrar lista final
            System.out.println("\n7. Lista final de categorías (debería mostrar solo No Ficción con ID=1):");
            List<Categoria> categoriasFinales = categoriaDAO.obtenerTodos();
            categoriasFinales.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        
        
         try {
            GeneroDAO generoDAO = new GeneroDAOImpl();
            
            // 0. Limpiar la base de datos
            System.out.println("0. Limpiando la base de datos...");
            generoDAO.eliminarTodos();
            
            // 1. Verificar que está vacía
            System.out.println("\n1. Verificando que la base de datos está vacía:");
            List<Genero> generosIniciales = generoDAO.obtenerTodos();
            if (generosIniciales.isEmpty()) {
                System.out.println("Base de datos vacía correctamente");
            } else {
                generosIniciales.forEach(System.out::println);
            }
            
            // 2. Insertar primer género
            System.out.println("\n2. Insertando primer género...");
            Genero primerGenero = new Genero();
            primerGenero.setNombre("Novela");
            
            generoDAO.insertar(primerGenero);
            System.out.println("Primer género insertado con ID: " + primerGenero.getIdGenero());
            
            // 3. Insertar segundo género
            System.out.println("\n3. Insertando segundo género...");
            Genero segundoGenero = new Genero();
            segundoGenero.setNombre("Poesía");
            
            generoDAO.insertar(segundoGenero);
            System.out.println("Segundo género insertado con ID: " + segundoGenero.getIdGenero());
            
            // 4. Mostrar lista actualizada
            System.out.println("\n4. Lista después de insertar dos géneros:");
            List<Genero> generosTrasInsercion = generoDAO.obtenerTodos();
            generosTrasInsercion.forEach(System.out::println);
            
            // 5. Intentar insertar un género duplicado
            System.out.println("\n5. Intentando insertar género duplicado...");
            try {
                Genero generoDuplicado = new Genero();
                generoDuplicado.setNombre("Novela");
                generoDAO.insertar(generoDuplicado);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 6. Eliminar primer género
            System.out.println("\n6. Eliminando el género Novela...");
            generoDAO.eliminar(primerGenero.getIdGenero());
            System.out.println("Género eliminado correctamente");
            
            // 7. Mostrar lista final
            System.out.println("\n7. Lista final de géneros (debería mostrar solo Poesía con ID=1):");
            List<Genero> generosFinales = generoDAO.obtenerTodos();
            generosFinales.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        
       
        
        try {
            AutorDAO autorDAO = new AutorDAOImpl();
            
            // 0. Limpiar la base de datos
            System.out.println("0. Limpiando la base de datos...");
            autorDAO.eliminarTodos();
            
            // 1. Verificar que está vacía
            System.out.println("\n1. Verificando que la base de datos está vacía:");
            List<Autor> autoresIniciales = autorDAO.obtenerTodos();
            if (autoresIniciales.isEmpty()) {
                System.out.println("Base de datos vacía correctamente");
            } else {
                autoresIniciales.forEach(System.out::println);
            }
            
            // 2. Insertar primer autor
            System.out.println("\n2. Insertando primer autor...");
            Autor primerAutor = new Autor();
            primerAutor.setNombre("Gabriel García Márquez");
            primerAutor.setNacionalidad("Colombiano");
            
            autorDAO.insertar(primerAutor);
            System.out.println("Primer autor insertado con ID: " + primerAutor.getIdAutor());
            
            // 3. Insertar segundo autor
            System.out.println("\n3. Insertando segundo autor...");
            Autor segundoAutor = new Autor();
            segundoAutor.setNombre("Mario Vargas Llosa");
            segundoAutor.setNacionalidad("Peruano");
            
            autorDAO.insertar(segundoAutor);
            System.out.println("Segundo autor insertado con ID: " + segundoAutor.getIdAutor());
            
            // 4. Mostrar lista actualizada
            System.out.println("\n4. Lista después de insertar dos autores:");
            List<Autor> autoresTrasInsercion = autorDAO.obtenerTodos();
            autoresTrasInsercion.forEach(System.out::println);
            
            // 5. Intentar insertar un autor duplicado
            System.out.println("\n5. Intentando insertar autor duplicado...");
            try {
                Autor autorDuplicado = new Autor();
                autorDuplicado.setNombre("Gabriel García Márquez");
                autorDuplicado.setNacionalidad("Colombiano");
                autorDAO.insertar(autorDuplicado);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 6. Eliminar primer autor
            System.out.println("\n6. Eliminando a Gabriel García Márquez...");
            autorDAO.eliminar(primerAutor.getIdAutor());
            System.out.println("Autor eliminado correctamente");
            
            // 7. Mostrar lista final
            System.out.println("\n7. Lista final de autores (debería mostrar solo a Vargas Llosa con ID=1):");
            List<Autor> autoresFinales = autorDAO.obtenerTodos();
            autoresFinales.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
          
       
        TESTING DE USUARIO
        
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            
            // 0. Eliminar todos los usuarios existentes y reiniciar secuencia
            System.out.println("0. Limpiando la base de datos...");
            usuarioDAO.eliminarTodos();
            
            // 1. Insertar usuarios iniciales
            System.out.println("\n1. Insertando usuarios iniciales...");
            Usuario usuario1 = new Usuario();
            usuario1.setNombre("Ana García");
            usuario1.setPassword("123456");
            usuario1.setIdRol(2);
            
            Usuario usuario2 = new Usuario();
            usuario2.setNombre("Carlos López");
            usuario2.setPassword("789012");
            usuario2.setIdRol(2);
            
            usuarioDAO.insertar(usuario1);
            usuarioDAO.insertar(usuario2);
            
            System.out.println("\n2. Listado inicial de usuarios:");
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            usuarios.forEach(System.out::println);
            
            // 3. Intentar insertar otro usuario con el mismo nombre
            System.out.println("\n3. Intentando insertar usuario duplicado (Ana García)...");
            try {
                Usuario usuarioDuplicado = new Usuario();
                usuarioDuplicado.setNombre("Ana García");
                usuarioDuplicado.setPassword("diferente");
                usuarioDuplicado.setIdRol(2);
                
                usuarioDAO.insertar(usuarioDuplicado);
            } catch (Exception e) {
                System.out.println("Error esperado: " + e.getMessage());
            }
            
            // 4. Eliminar a Ana García
            System.out.println("\n4. Eliminando a Ana García...");
            usuarioDAO.eliminar(usuario1.getIdUsuario());
            
            // 5. Mostrar lista final
            System.out.println("\n5. Lista final de usuarios:");
            List<Usuario> usuariosFinales = usuarioDAO.obtenerTodos();
            usuariosFinales.forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        Comprobar que funciona la ventan
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
        */