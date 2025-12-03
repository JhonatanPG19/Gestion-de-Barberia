-- Script para agregar la columna user_id a la tabla barberos
-- Ejecuta este script en tu base de datos barberoservice

-- Agregar la columna user_id
ALTER TABLE barberos ADD COLUMN IF NOT EXISTS user_id INTEGER;

-- Agregar índice para mejorar consultas
CREATE INDEX IF NOT EXISTS idx_barberos_user_id ON barberos(user_id);

-- Verificar la estructura de la tabla
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'barberos';
