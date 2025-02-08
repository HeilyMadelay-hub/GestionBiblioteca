
package Main;

import DAO.IMPL.UsuarioDAOImpl;
import DAO.UsuarioDAO;
import Modelo.Usuario;
import Vista.MainFrame;
import java.util.List;
import DAO.AutorDAO;
import DAO.IMPL.AutorDAOImpl;
import Modelo.Autor;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        
        
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
        
          
        /*
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
    }
}
