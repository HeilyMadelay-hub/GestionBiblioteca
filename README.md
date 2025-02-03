# Sistema de Gestión de Biblioteca JDBC

## 📚 Descripción
Sistema completo de gestión bibliotecaria implementando patrones DAO y Observer.


## 🌟 Funcionalidades
- Gestión completa CRUD de usuarios, libros y préstamos.
- Sistema de roles (administrador/usuario).
- Gestión de reservas con notificaciones automáticas.
- Sistema de auditoría y logging.
- Reportes estadísticos avanzados.

## 🛠️ Tecnologías
- Java 8+
- MySQL
- JDBC
- Patrones: DAO, Observer
- SHA-256 para cifrado

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

## 📥 Requisitos
- JDK 8+
- MySQL 5.7+
- NetBeans IDE
- Driver MySQL JDBC
- Base de datos biblioteca creada

## ⚙️ Configuración
### Clonar el repositorio
```bash
git clone https://github.com/tuusuario/biblioteca-jdbc.git
```
### Configurar la base de datos
```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```
### Configurar `config.properties`
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/biblioteca
db.user=root
db.password=root

# Application Configuration
app.language=es
app.transaction.isolation=TRANSACTION_READ_COMMITTED
app.transaction.timeout=30

# Logging Configuration
log.file.path=./log.txt
log.level=INFO
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

## 📫 Contacto
- **Email**: heilymadelayajtan@icloud.com

## 📄 Licencia
Este proyecto está bajo licencia MIT.




