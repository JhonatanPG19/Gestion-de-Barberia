# Microservicio de Reservas - Barbershop

Microservicio para gestión de reservas y turnos de barbería, implementado con arquitectura hexagonal y Java 21.

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

```
reservas-service/
├── domain/                          # Capa de Dominio (Negocio)
│   ├── model/                       # Entidades de dominio
│   │   ├── Reserva.java
│   │   ├── EstadoReserva.java
│   │   └── ColaEspera.java
│   └── ports/                       # Interfaces (Contratos)
│       ├── input/                   # Casos de uso
│       │   └── ReservaServicePort.java
│       └── output/                  # Puertos de salida
│           ├── ReservaPersistencePort.java
│           ├── NotificacionPort.java
│           ├── BarberoServicePort.java
│           ├── ServicioServicePort.java
│           └── ClienteServicePort.java
│
├── application/                     # Capa de Aplicación
│   ├── usecases/                   # Implementación de casos de uso
│   │   └── ReservaService.java
│   ├── dto/                        # DTOs de entrada/salida
│   └── mapper/                     # Mappers
│
└── infrastructure/                  # Capa de Infraestructura
    ├── adapters/
    │   ├── input/                  # Adaptadores de entrada
    │   │   └── rest/
    │   │       └── ReservaController.java
    │   └── output/                 # Adaptadores de salida
    │       ├── persistence/        # Base de datos
    │       ├── messaging/          # RabbitMQ
    │       └── rest/               # Clientes Feign
    └── config/                     # Configuraciones
```

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ** (Mensajería asíncrona)
- **OpenFeign** (Comunicación REST con otros microservicios)
- **Docker & Docker Compose**
- **Maven**
- **Lombok**
- **Swagger/OpenAPI**

## 📋 Prerequisitos

- Java 21
- Maven 3.9+
- Docker y Docker Compose (opcional)
- PostgreSQL 15+ (si se ejecuta sin Docker)
- RabbitMQ 3+ (si se ejecuta sin Docker)

## 🔧 Configuración

### Opción 1: Con Docker (Recomendado)

1. **Clonar el repositorio y navegar al directorio:**
```bash
cd reservas-service
```

2. **Iniciar todos los servicios con Docker Compose:**
```bash
docker-compose up -d
```

Esto iniciará:
- PostgreSQL en el puerto 5432
- RabbitMQ en el puerto 5672 (Management UI en 15672)
- El microservicio de reservas en el puerto 8081

3. **Verificar que los contenedores estén corriendo:**
```bash
docker-compose ps
```

### Opción 2: Sin Docker (Local)

1. **Instalar y configurar PostgreSQL:**
```bash
# Crear base de datos
createdb barbershop_reservas
```

2. **Instalar y configurar RabbitMQ:**
```bash
# Iniciar RabbitMQ
rabbitmq-server
```

3. **Configurar variables de entorno:**
```bash
cp .env.example .env
# Editar .env con tus credenciales
```

4. **Compilar y ejecutar:**
```bash
mvn clean install
mvn spring-boot:run
```

## 📡 Endpoints de la API

### Documentación interactiva
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/api-docs

### Endpoints principales

#### Crear Reserva
```http
POST /api/v1/reservas
Content-Type: application/json

{
  "clienteId": 1,
  "barberoId": 1,
  "servicioId": 1,
  "fechaHora": "2025-12-01T10:00:00",
  "observaciones": "Primera visita",
  "esWalkIn": false
}
```

#### Obtener Reserva por ID
```http
GET /api/v1/reservas/{id}
```

#### Obtener Reservas de un Cliente
```http
GET /api/v1/reservas/cliente/{clienteId}
```

#### Obtener Reservas de un Barbero por Fecha
```http
GET /api/v1/reservas/barbero/{barberoId}?fecha=2025-12-01
```

#### Obtener Horarios Disponibles
```http
GET /api/v1/reservas/disponibilidad?barberoId=1&servicioId=1&fecha=2025-12-01
```

#### Confirmar Reserva
```http
PUT /api/v1/reservas/{id}/confirmar
```

#### Iniciar Servicio
```http
PUT /api/v1/reservas/{id}/iniciar
```

#### Completar Servicio
```http
PUT /api/v1/reservas/{id}/completar
```

#### Reprogramar Reserva
```http
PUT /api/v1/reservas/{id}/reprogramar
Content-Type: application/json

{
  "nuevaFechaHora": "2025-12-02T11:00:00"
}
```

#### Cancelar Reserva
```http
DELETE /api/v1/reservas/{id}?motivo=Cliente canceló
```

## 🔌 Comunicación con Otros Microservicios

### REST (Síncrona)
El microservicio se comunica con otros servicios mediante OpenFeign:

- **Barbero Service** (Puerto 8082): Validación de barberos y disponibilidad
- **Servicio Service** (Puerto 8083): Información de servicios y duración
- **Usuario Service** (Puerto 8084): Información de clientes

### RabbitMQ (Asíncrona)
Envía eventos a través de RabbitMQ para notificaciones:

**Exchange:** `notificaciones.exchange`

**Routing Keys:**
- `reserva.creada`
- `reserva.confirmada`
- `reserva.cancelada`
- `reserva.reprogramada`
- `servicio.iniciado`
- `servicio.completado`

## 🧪 Testing

### Ejecutar pruebas unitarias:
```bash
mvn test
```

### Ejecutar pruebas con cobertura:
```bash
mvn clean test jacoco:report
```

## 📊 Health Check y Monitoreo

### Health Check
```http
GET /actuator/health
```

### Métricas
```http
GET /actuator/metrics
```

### Información del servicio
```http
GET /actuator/info
```

## 🔒 Reglas de Negocio Implementadas

1. **Duración mínima:** 45 minutos por servicio (configurable)
2. **Tiempo de buffer:** 5-10 minutos entre citas
3. **Tolerancia de retraso:** 10 minutos máximo
4. **Prioridad:** Reservas confirmadas sobre walk-ins
5. **Horario de atención:** 8:00 AM - 8:00 PM

## 🛠️ Comandos Útiles

### Maven
```bash
# Compilar
mvn clean compile

# Empaquetar
mvn clean package

# Ejecutar
mvn spring-boot:run

# Instalar dependencias
mvn dependency:resolve
```

### Docker
```bash
# Construir imagen
docker build -t reservas-service .

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f reservas-service

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

## 🐛 Troubleshooting

### El microservicio no se conecta a PostgreSQL
```bash
# Verificar que PostgreSQL esté corriendo
docker-compose ps postgres

# Ver logs de PostgreSQL
docker-compose logs postgres
```

### Problemas con RabbitMQ
```bash
# Acceder al Management UI
http://localhost:15672
# Usuario: guest / Password: guest

# Ver logs de RabbitMQ
docker-compose logs rabbitmq
```

### Error de conexión con otros microservicios
- Verificar que las URLs en `application.yml` sean correctas
- Asegurar que los otros microservicios estén corriendo
- Revisar logs: `docker-compose logs -f reservas-service`

## 📝 Patrones de Diseño Implementados

1. **Hexagonal Architecture (Ports & Adapters)**
2. **Repository Pattern**
3. **Adapter Pattern**
4. **Observer Pattern** (mediante eventos de RabbitMQ)
5. **Builder Pattern** (Lombok)
6. **Strategy Pattern** (diferentes implementaciones de puertos)

## 👥 Contacto

Para preguntas o soporte, contactar al equipo de desarrollo.

## 📄 Licencia

Este proyecto es parte del trabajo final de Ingeniería de Software III - Universidad del Cauca.
