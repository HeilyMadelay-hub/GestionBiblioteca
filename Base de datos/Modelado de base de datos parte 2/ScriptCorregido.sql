-- Crear la base de datos
CREATE DATABASE biblioteca;
USE biblioteca;

-- Tablas para normalización de roles y permisos (5FN)
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE permisos (
    id_permiso INT AUTO_INCREMENT PRIMARY KEY,
    nombre_permiso VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE roles_permisos (
    id_rol INT,
    id_permiso INT,
    PRIMARY KEY (id_rol, id_permiso),
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol),
    FOREIGN KEY (id_permiso) REFERENCES permisos(id_permiso)
);

-- Tablas para información de contacto (3FN)
CREATE TABLE tipos_contacto (
    id_tipo_contacto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla usuarios normalizada (4FN)
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    password CHAR(64) NOT NULL, -- SHA-256
    id_rol INT NOT NULL,
    multa DECIMAL(10,2) DEFAULT 0.00 CHECK (multa >= 0),
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE contactos_usuario (
    id_contacto INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo_contacto INT NOT NULL,
    valor VARCHAR(100) NOT NULL,
    verificado BOOLEAN DEFAULT FALSE,
    UNIQUE(id_tipo_contacto, valor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_tipo_contacto) REFERENCES tipos_contacto(id_tipo_contacto)
);

-- Tablas para normalización de libros (5FN)
CREATE TABLE autores (
    id_autor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(50)
);

CREATE TABLE generos (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE estados_libro (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE libros (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    isbn VARCHAR(13) UNIQUE,
    fecha_adquisicion DATE NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY (id_estado) REFERENCES estados_libro(id_estado)
);

-- Tablas de relaciones para libros (5FN)
CREATE TABLE libros_autores (
    id_libro INT,
    id_autor INT,
    PRIMARY KEY (id_libro, id_autor),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_autor) REFERENCES autores(id_autor)
);

CREATE TABLE libros_generos (
    id_libro INT,
    id_genero INT,
    PRIMARY KEY (id_libro, id_genero),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_genero) REFERENCES generos(id_genero)
);

CREATE TABLE libros_categorias (
    id_libro INT,
    id_categoria INT,
    PRIMARY KEY (id_libro, id_categoria),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- Tablas para préstamos (4FN)
CREATE TABLE tipos_prestamo (
    id_tipo_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE,
    dias_max INT NOT NULL
);

CREATE TABLE prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    id_tipo_prestamo INT NOT NULL,
    fecha_prestamo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real TIMESTAMP NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_tipo_prestamo) REFERENCES tipos_prestamo(id_tipo_prestamo)
);

-- Tabla específica para préstamos express (FNBC)
CREATE TABLE prestamos_express (
    id_prestamo INT PRIMARY KEY,
    dias_reales INT NOT NULL CHECK (dias_reales <= 7),
    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id_prestamo)
);

-- Tabla para reservas (4FN)
CREATE TABLE estados_reserva (
    id_estado_reserva INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    id_estado_reserva INT NOT NULL,
    fecha_reserva TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    FOREIGN KEY (id_estado_reserva) REFERENCES estados_reserva(id_estado_reserva)
);

-- Tablas para auditoría y logs (3FN)
CREATE TABLE tipos_log (
    id_tipo_log INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE logs (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_tipo_log INT NOT NULL,
    id_usuario INT NULL,
    accion TEXT NOT NULL,
    detalles JSON,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tipo_log) REFERENCES tipos_log(id_tipo_log),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Datos iniciales necesarios
INSERT INTO roles (nombre_rol) VALUES ('administrador'), ('usuario');
INSERT INTO tipos_contacto (nombre) VALUES ('email'), ('telefono');
INSERT INTO estados_libro (nombre) VALUES ('disponible'), ('prestado'), ('reservado'), ('no_disponible');
INSERT INTO tipos_prestamo (nombre, dias_max) VALUES ('regular', 30), ('express', 7);
INSERT INTO estados_reserva (nombre) VALUES ('pendiente'), ('completada'), ('vencida'), ('cancelada');
INSERT INTO tipos_log (nombre) VALUES ('usuario'), ('libro'), ('prestamo'), ('reserva'), ('sistema');

-- Triggers actualizados para la estructura normalizada
DELIMITER //

-- Trigger para validar límite de préstamos para usuarios regulares
CREATE TRIGGER check_prestamos_limite
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    DECLARE usuario_rol VARCHAR(20);
    DECLARE prestamos_activos INT;
    
    SELECT r.nombre_rol INTO usuario_rol
    FROM usuarios u
    JOIN roles r ON u.id_rol = r.id_rol
    WHERE u.id_usuario = NEW.id_usuario;
    
    IF usuario_rol = 'usuario' THEN
        SELECT COUNT(*) INTO prestamos_activos
        FROM prestamos
        WHERE id_usuario = NEW.id_usuario
        AND fecha_devolucion_real IS NULL;
        
        IF prestamos_activos >= 3 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Usuario regular no puede tener más de 3 préstamos activos';
        END IF;
    END IF;
END//

-- Trigger para actualizar estado del libro al préstamo
CREATE TRIGGER update_libro_estado_prestamo
AFTER INSERT ON prestamos
FOR EACH ROW
BEGIN
    UPDATE libros
    SET id_estado = (SELECT id_estado FROM estados_libro WHERE nombre = 'prestado')
    WHERE id_libro = NEW.id_libro;
END//

-- Trigger para préstamos express
CREATE TRIGGER check_prestamo_express
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion_real IS NOT NULL THEN
        SET @dias = DATEDIFF(NEW.fecha_devolucion_real, NEW.fecha_prestamo);
        IF @dias <= 7 THEN
            INSERT INTO prestamos_express (id_prestamo, dias_reales)
            VALUES (NEW.id_prestamo, @dias);
        END IF;
    END IF;
END//

-- Trigger para validar disponibilidad del libro
CREATE TRIGGER check_libro_disponible
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    DECLARE estado_actual VARCHAR(20);
    
    SELECT el.nombre INTO estado_actual
    FROM libros l
    JOIN estados_libro el ON l.id_estado = el.id_estado
    WHERE l.id_libro = NEW.id_libro;
    
    IF estado_actual != 'disponible' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El libro no está disponible para préstamo';
    END IF;
END//

-- Trigger para validar email único
CREATE TRIGGER check_email_unico
BEFORE INSERT ON contactos_usuario
FOR EACH ROW
BEGIN
    IF NEW.id_tipo_contacto = (SELECT id_tipo_contacto FROM tipos_contacto WHERE nombre = 'email') THEN
        IF EXISTS (
            SELECT 1 FROM contactos_usuario 
            WHERE id_tipo_contacto = NEW.id_tipo_contacto 
            AND valor = NEW.valor
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El email ya está registrado';
        END IF;
    END IF;
END//

DELIMITER ;

-- Vistas para reportes
CREATE VIEW v_libros_mas_prestados AS
SELECT 
    l.id_libro,
    l.titulo,
    COUNT(p.id_prestamo) as total_prestamos
FROM libros l
LEFT JOIN prestamos p ON l.id_libro = p.id_libro
WHERE p.fecha_prestamo >= DATE_SUB(CURRENT_DATE, INTERVAL 6 MONTH)
GROUP BY l.id_libro, l.titulo
ORDER BY total_prestamos DESC;

CREATE VIEW v_usuarios_mas_prestamos AS
SELECT 
    u.id_usuario,
    u.nombre,
    COUNT(p.id_prestamo) as total_prestamos
FROM usuarios u
LEFT JOIN prestamos p ON u.id_usuario = p.id_usuario
WHERE p.fecha_prestamo >= DATE_SUB(CURRENT_DATE, INTERVAL 3 MONTH)
GROUP BY u.id_usuario, u.nombre
ORDER BY total_prestamos DESC;

CREATE VIEW v_porcentaje_libros_genero AS
SELECT 
    g.nombre as genero,
    COUNT(lg.id_libro) as total_libros,
    (COUNT(lg.id_libro) * 100.0 / (SELECT COUNT(*) FROM libros)) as porcentaje
FROM generos g
LEFT JOIN libros_generos lg ON g.id_genero = lg.id_genero
GROUP BY g.id_genero, g.nombre;

CREATE VIEW v_libros_sin_prestamos AS
SELECT l.*
FROM libros l
LEFT JOIN prestamos p ON l.id_libro = p.id_libro
WHERE p.id_prestamo IS NULL
OR p.fecha_prestamo < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR);

CREATE VIEW v_usuarios_reservas_pendientes AS
SELECT 
    u.id_usuario,
    u.nombre,
    l.titulo,
    r.fecha_reserva,
    er.nombre as estado_reserva
FROM usuarios u
JOIN reservas r ON u.id_usuario = r.id_usuario
JOIN libros l ON r.id_libro = l.id_libro
JOIN estados_reserva er ON r.id_estado_reserva = er.id_estado_reserva
WHERE er.nombre IN ('pendiente', 'vencida');

show tables;
use biblioteca;
Use autores;
ALTER TABLE autores ADD UNIQUE (nombre);
select * from categorias;

select * from contactos_usuario;

DELIMITER //

CREATE TRIGGER before_update_prestamo_id
BEFORE UPDATE ON prestamos
FOR EACH ROW
BEGIN
    -- Actualizamos las referencias en prestamos_express si existen
    IF NEW.id_prestamo != OLD.id_prestamo THEN
        UPDATE prestamos_express 
        SET id_prestamo = NEW.id_prestamo 
        WHERE id_prestamo = OLD.id_prestamo;
    END IF;
END //

DELIMITER ;

-- 1. Primero eliminar el trigger existente
DROP TRIGGER IF EXISTS check_prestamos_limite;

-- 2. Establecer el delimitador
DELIMITER //

-- 3. Crear el nuevo trigger
CREATE TRIGGER check_prestamos_limite
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    DECLARE usuario_rol VARCHAR(20);
    DECLARE prestamos_activos INT;
    DECLARE usuario_existe INT;
    
    -- Verificar si el usuario existe
    SELECT COUNT(*) INTO usuario_existe
    FROM usuarios
    WHERE id_usuario = NEW.id_usuario;
    
    IF usuario_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario no existe';
    END IF;
    
    -- Obtener el rol del usuario
    SELECT r.nombre_rol INTO usuario_rol
    FROM usuarios u
    JOIN roles r ON u.id_rol = r.id_rol
    WHERE u.id_usuario = NEW.id_usuario;
    
    IF usuario_rol IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El rol del usuario no está definido';
    END IF;
    
    IF usuario_rol = 'usuario' THEN
        SELECT COUNT(*) INTO prestamos_activos
        FROM prestamos
        WHERE id_usuario = NEW.id_usuario
        AND fecha_devolucion_real IS NULL;
        
        IF prestamos_activos >= 3 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Usuario regular no puede tener más de 3 préstamos activos';
        END IF;
    END IF;
END //

-- 4. Restaurar el delimitador
DELIMITER ;

DROP TRIGGER IF EXISTS check_prestamos_limite;

DELIMITER //

CREATE TRIGGER check_prestamos_limite
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    DECLARE usuario_rol VARCHAR(20);
    DECLARE prestamos_activos INT;
    DECLARE usuario_existe INT;
    
    -- Verificar si el usuario existe
    SELECT COUNT(*) INTO usuario_existe
    FROM usuarios
    WHERE id_usuario = NEW.id_usuario;
    
    IF usuario_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario no existe';
    END IF;
    
    -- Obtener el rol del usuario
    SELECT r.nombre_rol INTO usuario_rol
    FROM usuarios u
    JOIN roles r ON u.id_rol = r.id_rol
    WHERE u.id_usuario = NEW.id_usuario;
    
    -- Contar los préstamos activos (sin devolver) para el usuario
    SELECT COUNT(*) INTO prestamos_activos
    FROM prestamos
    WHERE id_usuario = NEW.id_usuario
    AND fecha_devolucion_real IS NULL;
    
    -- Para usuarios regulares, verificar el límite
    IF usuario_rol = 'usuario' AND prestamos_activos >= 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuario regular no puede tener más de 3 préstamos activos';
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER before_delete_prestamo
BEFORE DELETE ON prestamos
FOR EACH ROW
BEGIN
    -- Reducir el contador de préstamos activos del usuario
    UPDATE usuarios u
    SET u.prestamos_activos = u.prestamos_activos - 1
    WHERE u.id_usuario = OLD.id_usuario;

    -- Actualizar el estado del libro a disponible
    UPDATE libros l
    SET l.id_estado = (SELECT id_estado FROM estados_libro WHERE nombre = 'disponible')
    WHERE l.id_libro = OLD.id_libro;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER after_update_prestamo_devolucion
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion_real IS NOT NULL AND OLD.fecha_devolucion_real IS NULL THEN
        -- Reducir el contador de préstamos activos del usuario
        UPDATE usuarios u
        SET u.prestamos_activos = u.prestamos_activos - 1
        WHERE u.id_usuario = NEW.id_usuario;
    END IF;
END //

DELIMITER ;
ALTER TABLE usuarios ADD COLUMN prestamos_activos INT DEFAULT 0;

DROP TRIGGER IF EXISTS before_delete_prestamo;

DELIMITER //

CREATE TRIGGER after_delete_prestamo
AFTER DELETE ON prestamos
FOR EACH ROW
BEGIN
    -- Reorganizar los IDs de los préstamos restantes
    SET @count = 0;
    UPDATE prestamos SET id_prestamo = (@count := @count + 1) ORDER BY fecha_prestamo, id_prestamo;
    -- Resetear el AUTO_INCREMENT
    ALTER TABLE prestamos AUTO_INCREMENT = (SELECT COALESCE(MAX(id_prestamo), 0) + 1 FROM prestamos);
END //

DELIMITER ;

DROP TRIGGER IF EXISTS before_delete_prestamo;

DROP TRIGGER IF EXISTS after_update_prestamo_devolucion;

DELIMITER //

CREATE TRIGGER after_update_prestamo_devolucion
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion_real IS NOT NULL AND OLD.fecha_devolucion_real IS NULL THEN
        -- Reducir el contador de préstamos activos del usuario
        UPDATE usuarios u
        SET u.prestamos_activos = u.prestamos_activos - 1
        WHERE u.id_usuario = NEW.id_usuario;
        
        -- Verificar si es préstamo express
        SET @dias = DATEDIFF(NEW.fecha_devolucion_real, NEW.fecha_prestamo);
        IF @dias <= 7 THEN
            -- Verificar si ya existe un registro express para este préstamo
            IF NOT EXISTS (SELECT 1 FROM prestamos_express WHERE id_prestamo = NEW.id_prestamo) THEN
                INSERT INTO prestamos_express (id_prestamo, dias_reales) VALUES (NEW.id_prestamo, @dias);
            END IF;
        END IF;
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER before_delete_prestamo
BEFORE DELETE ON prestamos
FOR EACH ROW
BEGIN
    -- Reducir el contador de préstamos activos del usuario
    UPDATE usuarios u
    SET u.prestamos_activos = u.prestamos_activos - 1
    WHERE u.id_usuario = OLD.id_usuario;

    -- Actualizar el estado del libro a disponible
    UPDATE libros l
    SET l.id_estado = (SELECT id_estado FROM estados_libro WHERE nombre = 'disponible')
    WHERE l.id_libro = OLD.id_libro;
END //

DELIMITER ;

use biblioteca;

ALTER TABLE prestamos ADD COLUMN orden_prestamo INT;

-- 1. Validación de fechas en préstamos
ALTER TABLE prestamos ADD CHECK (fecha_devolucion_esperada > fecha_prestamo);
ALTER TABLE prestamos ADD CHECK (fecha_devolucion_real > fecha_prestamo);

-- 2. Asegurar orden único de préstamos por usuario
ALTER TABLE prestamos ADD UNIQUE (id_usuario, orden_prestamo);

-- 3. Índices para mejorar rendimiento de consultas frecuentes
CREATE INDEX idx_prestamos_usuario_fecha ON prestamos(id_usuario, fecha_prestamo);
CREATE INDEX idx_prestamos_libro_fecha ON prestamos(id_libro, fecha_prestamo);
CREATE INDEX idx_libros_estado ON libros(id_estado);
CREATE INDEX idx_usuarios_nombre ON usuarios(nombre);
CREATE INDEX idx_contactos_valor ON contactos_usuario(valor);

-- 4. Validación de contraseña
ALTER TABLE usuarios ADD CHECK (LENGTH(password) = 64);

-- 5. Tabla para tracking de multas
CREATE TABLE multas_historial (
    id_multa INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    monto DECIMAL(10,2),
    fecha_aplicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- 6. Trigger para validar fechas
DELIMITER //
CREATE TRIGGER check_fechas_prestamo
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion_esperada <= NEW.fecha_prestamo THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La fecha de devolución esperada debe ser posterior a la fecha de préstamo';
    END IF;
END //
DELIMITER ;


-- 1. Primero seleccionar la base de datos
USE biblioteca;

-- 2. Limpiar/actualizar datos existentes antes de añadir restricciones
UPDATE prestamos SET orden_prestamo = ROW_NUMBER() 
OVER (PARTITION BY id_usuario ORDER BY fecha_prestamo)
WHERE orden_prestamo IS NULL;

-- 3. Añadir las restricciones de fechas solo después de validar datos existentes
ALTER TABLE prestamos ADD CHECK (fecha_devolucion_esperada > fecha_prestamo);
ALTER TABLE prestamos ADD CHECK (fecha_devolucion_real IS NULL OR fecha_devolucion_real > fecha_prestamo);

-- 4. Crear índices (estos no dieron error)
CREATE INDEX idx_prestamos_usuario_fecha ON prestamos(id_usuario, fecha_prestamo);
CREATE INDEX idx_prestamos_libro_fecha ON prestamos(id_libro, fecha_prestamo);
CREATE INDEX idx_libros_estado ON libros(id_estado);
CREATE INDEX idx_usuarios_nombre ON usuarios(nombre);
CREATE INDEX idx_contactos_valor ON contactos_usuario(valor);

-- 5. Finalmente, añadir la restricción UNIQUE después de asegurar que no hay duplicados
ALTER TABLE prestamos ADD UNIQUE (id_usuario, orden_prestamo);


-- 1. Primero, vamos a limpiar completamente la tabla prestamos_express
SET SQL_SAFE_UPDATES = 0;
TRUNCATE TABLE prestamos_express;

-- 2. Reiniciar las variables
SET @row_number = 0;
SET @current_user = 0;

-- 3. Hacer el UPDATE usando id_prestamo específicamente
UPDATE prestamos p
        INNER JOIN
    (SELECT 
        id_prestamo,
            id_usuario,
            @row_number:=IF(@current_user = id_usuario, @row_number + 1, 1) AS row_num,
            @current_user:=id_usuario AS user_id
    FROM
        (SELECT 
        id_prestamo, id_usuario
    FROM
        prestamos
    ORDER BY id_usuario , fecha_prestamo) ordered) AS temp ON p.id_prestamo = temp.id_prestamo 
SET 
    p.orden_prestamo = temp.row_num
WHERE
    p.id_prestamo > 0

-- 4. Volver a activar safe mode
SET SQL_SAFE_UPDATES = 1;

-- 1. Verificar que no hay huecos en el orden_prestamo para cada usuario
SELECT id_usuario, 
       GROUP_CONCAT(orden_prestamo ORDER BY orden_prestamo) as secuencia,
       COUNT(*) as total_prestamos,
       MIN(orden_prestamo) as min_orden,
       MAX(orden_prestamo) as max_orden
FROM prestamos
WHERE fecha_devolucion_real IS NULL
GROUP BY id_usuario
HAVING COUNT(*) != MAX(orden_prestamo) 
   OR MIN(orden_prestamo) != 1;

-- 2. Verificar que el orden respeta las fechas de préstamo
SELECT p1.id_usuario, 
       p1.id_prestamo, 
       p1.fecha_prestamo, 
       p1.orden_prestamo,
       p2.id_prestamo as prestamo_siguiente,
       p2.fecha_prestamo as fecha_siguiente,
       p2.orden_prestamo as orden_siguiente
FROM prestamos p1
LEFT JOIN prestamos p2 ON p1.id_usuario = p2.id_usuario 
    AND p1.orden_prestamo = p2.orden_prestamo - 1
WHERE p1.fecha_prestamo > p2.fecha_prestamo
AND p1.fecha_devolucion_real IS NULL
AND p2.fecha_devolucion_real IS NULL;

-- 3. Mostrar un resumen de préstamos activos por usuario
SELECT id_usuario,
       COUNT(*) as total_prestamos,
       GROUP_CONCAT(CONCAT('ID:', id_prestamo, ' Orden:', orden_prestamo) 
                   ORDER BY orden_prestamo) as detalle_prestamos
FROM prestamos
WHERE fecha_devolucion_real IS NULL
GROUP BY id_usuario;

-- Procedimiento que elimina un índice si existe
DELIMITER //
CREATE PROCEDURE drop_index_if_exists(IN table_name VARCHAR(100), IN index_name VARCHAR(100))
BEGIN
    IF((SELECT COUNT(*) AS index_exists FROM information_schema.statistics 
        WHERE table_schema = DATABASE() AND table_name = table_name AND index_name = index_name) > 0) 
    THEN
        SET @drop_statement = CONCAT('DROP INDEX ', index_name, ' ON ', table_name);
        PREPARE stmt FROM @drop_statement;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

-- Usar el procedimiento
CALL drop_index_if_exists('prestamos', 'idx_prestamos_usuario_fecha');
CALL drop_index_if_exists('prestamos', 'idx_prestamos_libro_fecha');
CALL drop_index_if_exists('libros', 'idx_libros_estado');
CALL drop_index_if_exists('usuarios', 'idx_usuarios_nombre');
CALL drop_index_if_exists('contactos_usuario', 'idx_contactos_valor');

-- Luego crear los índices normalmente
CREATE INDEX idx_prestamos_usuario_fecha ON prestamos(id_usuario, fecha_prestamo);
CREATE INDEX idx_prestamos_libro_fecha ON prestamos(id_libro, fecha_prestamo);
CREATE INDEX idx_libros_estado ON libros(id_estado);
CREATE INDEX idx_usuarios_nombre ON usuarios(nombre);
CREATE INDEX idx_contactos_valor ON contactos_usuario(valor);



use biblioteca;


INSERT INTO usuarios (nombre, password, id_rol, multa, activo)
VALUES ('Heily', '123456', 1, 0.00, 1);
select * from usuarios;


