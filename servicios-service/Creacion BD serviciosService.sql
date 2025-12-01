
-- Si no tiene la base de datos, crearla con:
-- CREATE DATABASE serviciosBD;

-- Elimina la tabla si ya existe (para limpieza)
DROP TABLE IF EXISTS public.servicios CASCADE;


-- Tabla principal de servicios
CREATE TABLE servicios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    duracion_minutos INTEGER NOT NULL CHECK (duracion_minutos >= 45),
    precio NUMERIC(10,2) NOT NULL,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla intermedia para relación N:N con barberos
-- Nota: asumimos que la tabla `barberos` está en otra BD (`barberoBD`)
-- Por simplicidad académica, usaremos solo el ID del barbero
-- En este caso, como `barbero-service` usa `INTEGER id`, usamos INTEGER
CREATE TABLE servicios_barberos (
    servicio_id INTEGER NOT NULL,
    barbero_id INTEGER NOT NULL,
    PRIMARY KEY (servicio_id, barbero_id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE CASCADE
    -- NOTA: no hacemos FK a barberos.barberos porque está en otra BD
);

-- Datos de prueba
INSERT INTO servicios (nombre, descripcion, duracion_minutos, precio) VALUES
('Corte Premium', 'Corte con lavado y diseño personalizado', 45, 25000.00),
('Barba Ejecutiva', 'Arreglo completo de barba con crema posafeitado', 60, 30000.00),
('Corte + Barba', 'Servicio completo: corte y arreglo de barba', 75, 45000.00);

-- Asociar servicios a barberos (usa IDs reales de tu BD `barberoBD`)
INSERT INTO servicios_barberos (servicio_id, barbero_id) VALUES
(1, 1), (1, 2),
(2, 2),
(3, 1), (3, 2);