-- Si no tiene la base de datos, crearla con:
-- CREATE DATABASE barberoBD;

-- Elimina la tabla si ya existe (para limpieza)
DROP TABLE IF EXISTS public.barberos CASCADE;

-- Crea la tabla 'barberos'
CREATE TABLE public.barberos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(150) NOT NULL UNIQUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'activo',
    
    -- Horarios laborales
    horario_inicio_laboral TIME NOT NULL DEFAULT '08:00:00',
    horario_fin_laboral TIME NOT NULL DEFAULT '18:00:00',
    
    -- Descanso
    hora_inicio_descanso TIME,
    hora_fin_descanso TIME,
    
    -- Días laborables (formato: "Lunes,Martes,Miercoles,Jueves,Viernes,Sabado")
    dias_laborables VARCHAR(200) NOT NULL DEFAULT 'Lunes,Martes,Miercoles,Jueves,Viernes,Sabado',
    
    -- Auditoría
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT chk_estado CHECK (estado IN ('activo', 'inactivo')),
    CONSTRAINT chk_horarios CHECK (
        horario_fin_laboral > horario_inicio_laboral
    ),
    CONSTRAINT chk_descanso CHECK (
        (hora_inicio_descanso IS NULL AND hora_fin_descanso IS NULL)
        OR
        (hora_fin_descanso > hora_inicio_descanso
         AND hora_inicio_descanso >= horario_inicio_laboral
         AND hora_fin_descanso <= horario_fin_laboral)
    )
);

-- Inserta datos de prueba
INSERT INTO public.barberos (
    nombre, telefono, email, estado,
    horario_inicio_laboral, horario_fin_laboral,
    hora_inicio_descanso, hora_fin_descanso,
    dias_laborables
) VALUES
(
    'Carlos Ramírez',
    '3001234568',
    'carlos@barberia.com',
    'activo',
    '08:00:00',
    '18:00:00',
    '13:00:00',
    '14:00:00',
    'Lunes,Martes,Miercoles,Jueves,Viernes,Sabado'
),
(
    'Juan Pérez',
    '3001234569',
    'juan@barberia.com',
    'activo',
    '09:00:00',
    '19:00:00',
    '14:00:00',
    '15:00:00',
    'Lunes,Martes,Miercoles,Jueves,Viernes'
),
(
    'María López',
    '3101234570',
    'maria@barberia.com',
    'inactivo',
    '08:00:00',
    '17:00:00',
    NULL,
    NULL,
    'Martes,Miercoles,Jueves'
);