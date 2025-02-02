# Sistema de Gestión de Biblioteca

## Descripción
Este es un proyecto desarrollado en **Java** utilizando **NetBeans** como entorno de desarrollo y **MySQL** como motor de base de datos. Implementa un sistema de gestión de biblioteca que permite administrar usuarios, libros y préstamos mediante el uso de conectores **JDBC**.

## Características Principales

### 📌 Gestión de Usuarios
- Registro, actualización y eliminación de usuarios con roles (*administrador* y *usuario regular*).

### 📌 Gestión de Libros
- Registro, actualización, búsqueda y eliminación de libros.

### 📌 Gestión de Préstamos
- Control de préstamos y devoluciones con restricciones.

### 📌 Reportes
- Generación de estadísticas sobre uso de libros y usuarios activos.

### 🔒 Seguridad
- Uso de consultas parametrizadas.
- Cifrado de contraseñas.
- Registro de operaciones en logs.

## 🛠 Tecnologías Utilizadas

- **Lenguaje**: Java
- **Entorno de Desarrollo**: NetBeans
- **Base de Datos**: MySQL
- **JDBC**: Para la conexión y manipulación de datos en MySQL
- **Patrones de Diseño**: DAO (Data Access Object) y Observer

## 📥 Requisitos de Instalación

1. **Instalar MySQL** y crear una base de datos llamada `biblioteca`.
2. **Configurar la base de datos** ejecutando los scripts SQL proporcionados en el directorio `sql/`.
3. **Configurar NetBeans** e importar el proyecto.
4. **Asegurar que el driver JDBC de MySQL** está incluido en las librerías del proyecto.
5. **Configurar el archivo `config.properties`** con los datos de conexión a la base de datos.

## 📂 Estructura del Proyecto

```
|-- src/
|   |-- modelo/         # Clases de entidades (Usuario, Libro, Prestamo)
|   |-- dao/            # Acceso a datos con JDBC
|   |-- servicio/       # Lógica de negocio y validaciones
|   |-- util/           # Herramientas auxiliares (configuración, logs)
|   |-- main/           # Punto de entrada del sistema
|   |-- vista/          # Interfaces gráficas de usuario
|-- sql/                # Scripts para la base de datos
|-- config.properties   # Configuración de la conexión a MySQL
|-- README.md           # Documentación del proyecto
```

## 🚀 Uso del Proyecto

1. **Ejecutar la aplicación** desde NetBeans.
2. **Iniciar sesión** como usuario administrador para gestionar libros y usuarios.
3. **Realizar operaciones** como registrar usuarios, añadir libros, gestionar préstamos y generar reportes.

## 🔐 Seguridad Implementada

- **Consultas parametrizadas** para prevenir inyección SQL.
- **Cifrado de contraseñas** utilizando SHA-256.
- **Registro de operaciones** en un archivo `log.txt`.

## 📧 Contacto
Si tienes dudas o sugerencias, no dudes en abrir un *issue* o contribuir al proyecto.


