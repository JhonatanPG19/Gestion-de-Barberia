-- Script de inicialización de base de datos
-- Microservicio de Reservas

-- Crear tabla de reservas
CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    barbero_id BIGINT NOT NULL,
    servicio_id BIGINT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    fecha_hora_fin TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL,
    tiempo_estimado INTEGER,
    observaciones VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    es_walk_in BOOLEAN DEFAULT FALSE,
    posicion_cola INTEGER,
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA', 'NO_ASISTIO'))
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_cliente_id ON reservas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_barbero_id ON reservas(barbero_id);
CREATE INDEX IF NOT EXISTS idx_fecha_hora ON reservas(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_estado ON reservas(estado);
CREATE INDEX IF NOT EXISTS idx_barbero_fecha ON reservas(barbero_id, fecha_hora);

-- Datos de prueba
INSERT INTO reservas (cliente_id, barbero_id, servicio_id, fecha_hora, fecha_hora_fin, estado, tiempo_estimado, observaciones, es_walk_in, posicion_cola)
VALUES 
    (1, 1, 1, '2025-12-01 10:00:00', '2025-12-01 11:00:00', 'CONFIRMADA', 45, 'Primera visita', false, null),
    (2, 1, 2, '2025-12-01 11:15:00', '2025-12-01 12:15:00', 'CONFIRMADA', 45, null, false, null),
    (3, 2, 1, '2025-12-01 10:00:00', '2025-12-01 11:00:00', 'PENDIENTE', 45, null, false, null),
    (4, 2, 3, '2025-12-01 14:00:00', '2025-12-01 15:30:00', 'CONFIRMADA', 60, 'Cliente frecuente', false, null),
    (5, 1, 1, '2025-12-02 09:00:00', '2025-12-02 10:00:00', 'PENDIENTE', 45, null, true, 1);

COMMIT;
