# Sistema de Gestión de Biblioteca JDBC

## 📚 Descripción
Sistema completo de gestión bibliotecaria que permite realizar operaciones como registrar usuarios, gestionar libros y préstamos, generar reportes avanzados y garantizar la seguridad de los datos. Implementa operaciones CRUD utilizando JDBC y MySQL, con patrones DAO y Observer.

## 🌟 Funcionalidades
- Gestión completa CRUD de usuarios, libros y préstamos.
- Sistema de roles (administrador/usuario).
- Gestión de reservas con notificaciones automáticas.
- Sistema de auditoría y logging.
- Reportes estadísticos avanzados.

## ⭐ Características Destacadas
- Sistema de notificaciones automáticas
- Reportes estadísticos avanzados
- Gestión de préstamos express
- Auditoría completa de operaciones
- Interfaz gráfica intuitiva

## 🛠️ Tecnologías
- Java 8+
- MySQL
- JDBC
- Patrones: DAO, Observer
- SHA-256 para cifrado

## 📥 Requisitos Previos
Herramientas Necesarias
- JDK 8 o superior
- MySQL 5.7+
- NetBeans IDE
- Driver MySQL JDBC

# Configuración y Despliegue del Proyecto

## 📋 Requisitos Previos
- Java JDK 8 o superior
- MySQL Server
- NetBeans IDE
- Driver JDBC de MySQL (ejemplo: `mysql-connector-java-8.0.30.jar`)

---

## 🚀 Pasos para Configuración

### 1. Preparación de la Base de Datos
Ejecuta los siguientes comandos en tu terminal:

```bash
# Crear esquema y tablas (ajusta el nombre de la base de datos si es necesario)
mysql -u root -p < sql/schema.sql

# Insertar datos iniciales (ejemplo: libros, usuarios, etc.)
mysql -u root -p < sql/data.sql
```
### ⚠️ Nota:

Reemplaza root por tu usuario de MySQL si es diferente.

Asegúrate de que la contraseña coincida con tu configuración de MySQL.

### 2. Configuración del Archivo config.properties

Crea un archivo config.properties en la raíz del proyecto con el siguiente contenido:

# Configuración de Base de Datos
```bash
db.url=jdbc:mysql://localhost:3306/biblioteca
db.user=root
db.password=root  # Cambiar si tu contraseña es diferente
```
# Configuración de la Aplicación
```bash
app.language=es
app.transaction.isolation=TRANSACTION_READ_COMMITTED
app.transaction.timeout=30
```
# Configuración de Logging
```bash
log.file.path=./log.txt
log.level=INFO
```

### 🔧 Ajustes Recomendados:

Si usas otro puerto o nombre de base de datos, modifica db.url.

Cambia app.language a en para inglés.

### 3. Configuración en NetBeans

- Importar Proyecto:

- Abre NetBeans y selecciona File > Open Project.

- Navega hasta la carpeta del proyecto y ábrelo.

- Añadir Driver JDBC:

- Haz clic derecho en el proyecto > Properties > Libraries > Add JAR/Folder.

- Busca el archivo .jar del driver MySQL (ejemplo: mysql-connector-java-8.0.30.jar).

### 🛠️ Despliegue de la Aplicación

### Compilar el Proyecto:

- En NetBeans, haz clic en el botón Clean & Build (martillo verde).

- Ejecutar la Aplicación:

- Desde la terminal, usa:
  
```bash
java -jar NombreDelProyecto.jar
```
Verifica los logs en ./log.txt para detectar errores.

### 🔍 Soporte

Si hay errores de conexión a la base de datos, revisa:

- Credenciales en config.properties.

- Que MySQL esté corriendo en el puerto 3306.

- Para problemas con el driver JDBC, asegúrate de que la versión coincida con tu servidor MySQL.

  
## 📁 Estructura del Proyecto
```
Copysrc/
├── modelo/                    # Entidades
│   ├── Autor.java
│   ├── Categoria.java
│   ├── Contacto.java
│   ├── Genero.java
│   ├── Libro.java
│   ├── Log.java
│   ├── Prestamo.java
│   ├── Reserva.java
│   └── Usuario.java
│
├── dao/                      # Capa de acceso a datos
│   ├── impl/                 # Implementaciones
│   │   ├── AutorDAOImpl.java
│   │   ├── CategoriaDAOImpl.java
│   │   ├── ContactoDAOImpl.java
│   │   ├── GeneroDAOImpl.java
│   │   ├── LibroDAOImpl.java
│   │   ├── LogDAOImpl.java
│   │   ├── PrestamoDAOImpl.java
│   │   ├── ReservaDAOImpl.java
│   │   └── UsuarioDAOImpl.java
│   │
│   ├── AutorDAO.java        # Interfaces
│   ├── CategoriaDAO.java
│   ├── ContactoDAO.java
│   ├── GeneroDAO.java
│   ├── LibroDAO.java
│   ├── LogDAO.java
│   ├── PrestamoDAO.java
│   ├── ReservaDAO.java
│   └── UsuarioDAO.java
│
├── observer/                 # Patrón Observer
│   ├── Observable.java
│   ├── Observer.java
│   ├── LibroNotificador.java
│   └── UsuarioNotificacion.java
│
├── servicio/                 # Lógica de negocio
│   ├── AutorServicio.java
│   ├── CategoriaServicio.java
│   ├── ContactoServicio.java
│   ├── GeneroServicio.java
│   ├── LibroServicio.java
│   ├── LogServicio.java
│   ├── PrestamoServicio.java
│   ├── ReservaServicio.java
│   └── UsuarioServicio.java
│
├── util/                     # Utilidades
│   ├── DatabaseUtil.java     # Conexión BD
│   ├── SecurityUtil.java     # Cifrado
│   └── LogUtil.java         # Logging
│
├── enums/                    # Enumerados
│   ├── RolEnum.java
│   ├── TipoContactoEnum.java
│   ├── EstadoLibroEnum.java
│   ├── TipoPrestamoEnum.java
│   ├── EstadoReservaEnum.java
│   └── TipoLogEnum.java
│
├── vista/                    # Interfaz gráfica
│   ├── AutorVista.java
│   ├── CategoriaVista.java
│   ├── ContactoVista.java
│   ├── GeneroVista.java
│   ├── LibroVista.java
│   ├── PrestamoVista.java
│   ├── ReservaVista.java
│   └── UsuarioVista.java
│
└── main/                     # Clase principal
    └── Main.java
```

## 🔒 Seguridad Implementada
- Consultas parametrizadas (evita SQL Injection).
- Cifrado SHA-256 para contraseñas.
- Validación de datos de entrada.
- Registro de operaciones (`log.txt`).

## 📊 Reportes Disponibles
- Libros más prestados (6 meses).
- Usuarios más activos (3 meses).
- Distribución por género.
- Libros sin préstamos (1 año).
- Reservas pendientes/vencidas.

## 🎯 Funcionalidades por Rol
### 👨‍💼 Administrador
- Gestión completa de usuarios.
- Gestión completa de libros.
- Acceso a todos los reportes.
- Sin límite de préstamos.

### 👤 Usuario Regular
- Consulta de libros.
- Máximo 3 préstamos simultáneos.
- Gestión de reservas.
- Consulta de préstamos propios.

## 📝 Modelos de implementación
- Implementación del patrón DAO para acceso a datos.
- Patrón Observer para notificaciones de libros.
- Logs detallados de operaciones.
- Validaciones de negocio en capa servicio.

## 📊 Estado del Proyecto
- [x] Diseño de base de datos
- [ ] Implementación core (DAO)
- [ ] Sistema de préstamos y reservas
- [ ] Sistema de notificaciones
- [ ] Interfaz gráfica
- [ ] Testing y deployment

## 📫 Contacto
- **Email**: heilymadelayajtan@icloud.com

## 📄 Licencia
Este proyecto no tiene licencia.




