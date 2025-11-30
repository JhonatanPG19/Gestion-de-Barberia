-- Crear la base de datos para el servicio de notificaciones
CREATE DATABASE notificaciondb;

-- Conectar a la base de datos
\c notificaciondb;

-- La tabla será creada automáticamente por JPA/Hibernate
-- pero aquí está el esquema para referencia:

CREATE TABLE IF NOT EXISTS notificaciones (
    id BIGSERIAL PRIMARY KEY,
    destinatario VARCHAR(255) NOT NULL,
    asunto VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_envio TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL,
    error_mensaje TEXT,
    reserva_id BIGINT
);

-- Índices para mejorar el rendimiento de las consultas
CREATE INDEX idx_notificaciones_estado ON notificaciones(estado);
CREATE INDEX idx_notificaciones_destinatario ON notificaciones(destinatario);
CREATE INDEX idx_notificaciones_reserva_id ON notificaciones(reserva_id);
CREATE INDEX idx_notificaciones_tipo ON notificaciones(tipo);
CREATE INDEX idx_notificaciones_fecha_creacion ON notificaciones(fecha_creacion);
