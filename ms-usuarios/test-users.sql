-- Script para insertar usuarios de prueba en la base de datos usuarios_db
-- Ejecuta este script en tu PostgreSQL para crear usuarios de prueba

-- Usuario ADMIN
INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('admin', 'Admin', 'Sistema', '3001234567', 'admin123', 'admin@barberia.com', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Usuario BARBERO
INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('barbero1', 'Juan', 'Pérez', '3009876543', 'barbero123', 'barbero@barberia.com', 'BARBERO')
ON CONFLICT (username) DO NOTHING;

-- Usuario CLIENTE
INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('cliente1', 'Carlos', 'Gómez', '3001112233', 'cliente123', 'cliente@gmail.com', 'CLIENTE')
ON CONFLICT (username) DO NOTHING;

-- Verificar los usuarios insertados
SELECT id, username, nombre, apellido, rol, correo FROM usuarios;
