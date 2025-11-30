# barbero-service

Microservicio para la **Gestión de Barberos** del sistema de barbería.

## ✨ Funcionalidades
- Registro, edición e inactivación de barberos
- Configuración de horarios laborales, descansos y días laborables
- Visualización de estado (`activo` / `inactivo`)

## 🛠️ Tecnologías
- Java 17
- Spring Boot 3.5.7
- PostgreSQL
- Maven

## 🚀 Ejecutar localmente

1. Configura la base de datos `barberoBD` en PostgreSQL.
2. Edita `src/main/resources/application.properties` con tus credenciales.
3. Ejecuta:

```bash
mvn clean install
mvn spring-boot:run
```