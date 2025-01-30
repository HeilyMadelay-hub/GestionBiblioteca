-- Crear la base de datos
CREATE DATABASE biblioteca;
USE biblioteca;

-- Crear la tabla usuarios
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    rol ENUM('administrador', 'usuario') NOT NULL,
    password CHAR(64) NOT NULL, -- Cifrado SHA-256
    multa DECIMAL(10,2) DEFAULT 0.00 CHECK (multa >= 0) -- Nueva columna para multas
);

-- Crear la tabla libros
CREATE TABLE libros (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    categoria VARCHAR(50) NOT NULL, -- Nueva columna para categorías
    estado ENUM('disponible', 'prestado') NOT NULL DEFAULT 'disponible'
);

-- Crear la tabla prestamos
CREATE TABLE prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE DEFAULT NULL,
    CONSTRAINT fk_prestamos_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_prestamos_libros FOREIGN KEY (id_libro) REFERENCES libros(id_libro) ON DELETE CASCADE
);

-- Crear la tabla prestamos_express
CREATE TABLE prestamos_express (
    id_express INT AUTO_INCREMENT PRIMARY KEY,
    id_prestamo INT NOT NULL,
    dias_prestamo INT CHECK (dias_prestamo <= 7),
    CONSTRAINT fk_express_prestamos FOREIGN KEY (id_prestamo) REFERENCES prestamos(id_prestamo) ON DELETE CASCADE
);

-- Crear la tabla reservas (Nueva)
CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    estado ENUM('pendiente', 'vencida', 'cancelada', 'completada') NOT NULL DEFAULT 'pendiente',
    CONSTRAINT fk_reservas_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_reservas_libros FOREIGN KEY (id_libro) REFERENCES libros(id_libro) ON DELETE CASCADE
);

-- Crear la tabla historial_prestamos (Nueva)
CREATE TABLE historial_prestamos (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE NOT NULL,
    tipo_prestamo ENUM('regular', 'express') NOT NULL,
    CONSTRAINT fk_historial_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_historial_libros FOREIGN KEY (id_libro) REFERENCES libros(id_libro) ON DELETE CASCADE
);

-- Crear la tabla reportes
CREATE TABLE reportes (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE logs (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NULL,
    accion TEXT NOT NULL,
    detalles TEXT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger para limitar préstamos de usuarios regulares
DELIMITER //
CREATE TRIGGER limitar_prestamos_usuario
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    DECLARE total_prestamos INT;
    
    -- Verificar si el usuario es regular (excluir administradores)
    IF (SELECT rol FROM usuarios WHERE id_usuario = NEW.id_usuario) = 'usuario' THEN
        -- Contar los préstamos activos del usuario
        SELECT COUNT(*) INTO total_prestamos 
        FROM prestamos 
        WHERE id_usuario = NEW.id_usuario 
        AND fecha_devolucion IS NULL;

        -- Limitar a 3 préstamos activos
        IF total_prestamos >= 3 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El usuario ya tiene 3 préstamos activos.';
        END IF;
    END IF;
END;
//
DELIMITER ;

-- Trigger para actualizar estado del libro y asignar reserva correctamente
DELIMITER //
CREATE TRIGGER actualizar_estado_libro
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion IS NOT NULL THEN
        -- Verificar si existe una reserva pendiente antes de actualizar
        IF EXISTS (SELECT 1 FROM reservas WHERE id_libro = NEW.id_libro AND estado = 'pendiente') THEN
            -- Asignar el libro a la reserva más antigua y cambiar su estado a prestado (o no disponible si lo prefieres)
            UPDATE reservas 
            SET estado = 'completada' 
            WHERE id_reserva = (
                SELECT id_reserva FROM reservas 
                WHERE id_libro = NEW.id_libro 
                AND estado = 'pendiente'
                ORDER BY fecha_reserva ASC 
                LIMIT 1
            );
            
            UPDATE libros 
            SET estado = 'prestado'  -- O 'no disponible' si lo prefieres
            WHERE id_libro = NEW.id_libro;
        ELSE
            -- Cambiar estado del libro a disponible solo si no hay reservas
            UPDATE libros 
            SET estado = 'disponible' 
            WHERE id_libro = NEW.id_libro;
        END IF;
    END IF;
END;
//
DELIMITER ;


-- Trigger para validar estado del libro antes de préstamo
DELIMITER //
CREATE TRIGGER validar_estado_libro
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    -- Verificar multa del usuario
    IF (SELECT multa FROM usuarios WHERE id_usuario = NEW.id_usuario) > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario tiene multas pendientes y no puede realizar préstamos.';
    END IF;

    -- Verificar estado del libro
    IF (SELECT estado FROM libros WHERE id_libro = NEW.id_libro) != 'disponible' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El libro no está disponible para préstamo.';
    END IF;
END;
//
DELIMITER ;

-- Trigger para mover préstamos al historial tras devolución sin redundancia
DELIMITER //
CREATE TRIGGER mover_a_historial
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion IS NOT NULL THEN
        INSERT INTO historial_prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion, tipo_prestamo)
        VALUES (NEW.id_usuario, NEW.id_libro, NEW.fecha_prestamo, NEW.fecha_devolucion,
                CASE WHEN DATEDIFF(NEW.fecha_devolucion, NEW.fecha_prestamo) <= 7 THEN 'express' ELSE 'regular' END);
        
        -- Eliminar el préstamo después de moverlo al historial, asegurando que no existan dependencias
        DELETE FROM prestamos WHERE id_prestamo = NEW.id_prestamo;
    END IF;
END;
//
DELIMITER ;

-- Trigger para cancelar reservas si el usuario ya tiene el libro en préstamo
DELIMITER //
CREATE TRIGGER cancelar_reserva_si_prestado
BEFORE INSERT ON prestamos
FOR EACH ROW
BEGIN
    UPDATE reservas 
    SET estado = 'vencida' 
    WHERE id_usuario = NEW.id_usuario 
    AND id_libro = NEW.id_libro 
    AND estado = 'pendiente';
END;
//
DELIMITER ;


-- Trigger para registrar préstamos en logs con más detalles y trazabilidad
DELIMITER //
CREATE TRIGGER log_prestamos
AFTER INSERT ON prestamos
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, accion, detalles, fecha)
    VALUES (NEW.id_usuario, 'Préstamo registrado', 
            CONCAT('Libro ID ', NEW.id_libro, ' | Fecha préstamo: ', NEW.fecha_prestamo, ' | ID préstamo: ', NEW.id_prestamo), NOW());
END;
//
DELIMITER ;



DELIMITER //
CREATE TRIGGER evitar_eliminar_usuario_con_prestamos
BEFORE DELETE ON usuarios
FOR EACH ROW
BEGIN
    DECLARE prestamos_activos INT;

    SELECT COUNT(*) INTO prestamos_activos 
    FROM prestamos 
    WHERE id_usuario = OLD.id_usuario AND fecha_devolucion IS NULL;

    IF prestamos_activos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede eliminar un usuario con préstamos activos.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER evitar_eliminar_libro_prestado
BEFORE DELETE ON libros
FOR EACH ROW
BEGIN
    DECLARE libro_prestado INT;

    SELECT COUNT(*) INTO libro_prestado 
    FROM prestamos 
    WHERE id_libro = OLD.id_libro AND fecha_devolucion IS NULL;

    IF libro_prestado > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede eliminar un libro que está actualmente prestado.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER registrar_prestamo_express
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    IF NEW.fecha_devolucion IS NOT NULL AND DATEDIFF(NEW.fecha_devolucion, NEW.fecha_prestamo) <= 7 THEN
        INSERT INTO prestamos_express (id_prestamo, dias_prestamo)
        VALUES (NEW.id_prestamo, DATEDIFF(NEW.fecha_devolucion, NEW.fecha_prestamo));
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER validar_email_unico
BEFORE INSERT ON usuarios
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE email = NEW.email) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El email ingresado ya está registrado.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER validar_telefono_unico
BEFORE INSERT ON usuarios
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE telefono = NEW.telefono) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El teléfono ingresado ya está registrado.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER log_actualizacion_usuario
AFTER UPDATE ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, accion)
    VALUES (NEW.id_usuario, CONCAT('Datos del usuario actualizados en fecha ', NOW()));
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER log_actualizacion_libro
AFTER UPDATE ON libros
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, accion)
    VALUES (NULL, CONCAT('Libro ID ', NEW.id_libro, ' actualizado en fecha ', NOW()));
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER evitar_reserva_si_prestado
BEFORE INSERT ON reservas
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM prestamos WHERE id_usuario = NEW.id_usuario AND id_libro = NEW.id_libro AND fecha_devolucion IS NULL) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No puedes reservar un libro que ya tienes en préstamo.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER log_eliminacion_usuario
AFTER DELETE ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, accion)
    VALUES (OLD.id_usuario, CONCAT('Usuario eliminado en fecha ', NOW()));
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER prevenir_eliminacion_administrador
BEFORE DELETE ON usuarios
FOR EACH ROW
BEGIN
    IF OLD.rol = 'administrador' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se pueden eliminar administradores.';
    END IF;
END;
//
DELIMITER ;

-- Agregar "no disponible" al tipo ENUM en la columna estado de la tabla libros
ALTER TABLE libros 
MODIFY COLUMN estado ENUM('disponible', 'prestado', 'reservado', 'no disponible');

USE biblioteca;
SHOW tables;
