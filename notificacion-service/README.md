# Microservicio de Notificaciones - Sistema de Gestión de Barbería

Microservicio encargado de la comunicación asíncrona con los clientes mediante correo electrónico. Consume eventos de RabbitMQ y envía notificaciones automáticas sobre el estado de las reservas.

## 🚀 Características

- ✉️ Envío automático de correos electrónicos
- 📨 Consumo de eventos de RabbitMQ (Reserva Creada, Cancelada, Modificada)
- 💾 Persistencia del historial de notificaciones
- 📊 Endpoints de reportes y métricas
- 🔄 Reintento automático de notificaciones fallidas
- 🏥 Health check endpoint

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring AMQP** (RabbitMQ)
- **Spring Mail** (SMTP)
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**

## 📋 Prerequisitos

- Java 21 o superior
- PostgreSQL 12 o superior
- RabbitMQ 3.x
- Maven 3.9+

## ⚙️ Configuración

### 1. Base de Datos

Crear la base de datos ejecutando el script:
```bash
psql -U postgres -f "Creacion BD notificacionService.sql"
```

### 2. RabbitMQ

Asegúrate de tener RabbitMQ corriendo en:
- Host: localhost
- Puerto: 5672
- Usuario: guest
- Password: guest

O inicia con Docker:
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 3. Configuración de Email

Edita `application.properties` y configura tu cuenta de email:

#### Para Gmail:
1. Habilita la verificación en 2 pasos en tu cuenta de Google
2. Genera una contraseña de aplicación: https://myaccount.google.com/apppasswords
3. Configura las variables de entorno:

```bash
# Windows
set EMAIL_USERNAME=tu-email@gmail.com
set EMAIL_PASSWORD=tu-contraseña-de-aplicacion
set EMAIL_FROM=noreply@barbershop.com

# Linux/Mac
export EMAIL_USERNAME=tu-email@gmail.com
export EMAIL_PASSWORD=tu-contraseña-de-aplicacion
export EMAIL_FROM=noreply@barbershop.com
```

### 4. Variables de Entorno

```properties
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=tu-contraseña-de-aplicacion
EMAIL_FROM=noreply@barbershop.com
EMAIL_ENABLED=true
```

## 🏃 Ejecución

### Desarrollo
```bash
./mvnw spring-boot:run
```

### Producción
```bash
./mvnw clean package
java -jar target/notificacion-service-0.0.1-SNAPSHOT.jar
```

El servicio estará disponible en: `http://localhost:8083`

## 📡 API Endpoints

### Health Check
```
GET /api/health
```

### Notificaciones

```
GET  /api/notificaciones                    # Listar todas
GET  /api/notificaciones/{id}               # Obtener por ID
GET  /api/notificaciones/destinatario/{email} # Por email
GET  /api/notificaciones/reserva/{reservaId}  # Por reserva
GET  /api/notificaciones/estado/{estado}    # Por estado
GET  /api/notificaciones/tipo/{tipo}        # Por tipo
GET  /api/notificaciones/rango-fechas       # Por rango de fechas
POST /api/notificaciones/reintentar-fallidas # Reintentar fallidas
```

### Reportes

```
GET /api/reportes/metricas           # Métricas personalizadas
GET /api/reportes/resumen-mensual    # Resumen del mes actual
GET /api/reportes/resumen-diario     # Resumen del día actual
```

## 📊 Ejemplo de Métricas

```json
{
  "periodo": {
    "inicio": "2025-11-01T00:00:00",
    "fin": "2025-11-26T15:30:00"
  },
  "enviadas": 145,
  "fallidas": 3,
  "pendientes": 2,
  "total": 150,
  "tasaExito": "96.67%"
}
```

## 🔔 Tipos de Notificaciones

### 1. Reserva Creada
Notifica al cliente cuando se crea una nueva reserva con todos los detalles.

### 2. Reserva Cancelada
Informa al cliente sobre la cancelación de su reserva.

### 3. Reserva Modificada
Notifica cambios en los detalles de una reserva existente.

## 🔄 Eventos de RabbitMQ

El servicio consume de estas colas:

- `reserva.creada.queue` → Routing Key: `reserva.creada`
- `reserva.cancelada.queue` → Routing Key: `reserva.cancelada`
- `reserva.modificada.queue` → Routing Key: `reserva.modificada`

Exchange: `reservas.exchange` (tipo: topic)

## 📝 Estructura del Proyecto

```
src/main/java/co/edu/unicauca/notificacion_service/
├── config/
│   └── RabbitMQConfig.java          # Configuración de RabbitMQ
├── consumer/
│   └── EventConsumer.java           # Consumidor de eventos
├── controller/
│   ├── HealthController.java        # Health check
│   ├── NotificacionController.java  # CRUD notificaciones
│   └── ReporteController.java       # Reportes y métricas
├── dto/
│   └── ReservaEventoDTO.java           # DTO de eventos
├── model/
│   ├── EstadoNotificacion.java      # Enum estados
│   ├── Notificacion.java            # Entidad principal
│   └── TipoNotificacion.java        # Enum tipos
├── repository/
│   └── NotificacionRepository.java  # Repositorio JPA
├── service/
│   ├── EmailService.java            # Servicio de email
│   └── NotificacionService.java     # Lógica de negocio
└── NotificacionServiceApplication.java
```

## 🧪 Testing

Para probar el envío de emails sin RabbitMQ, puedes usar curl:

```bash
# Ver health
curl http://localhost:8083/api/health

# Ver métricas
curl http://localhost:8083/api/reportes/metricas

# Listar notificaciones
curl http://localhost:8083/api/notificaciones
```

## 🐛 Troubleshooting

### Error de autenticación SMTP
- Verifica que hayas generado una contraseña de aplicación de Google
- Asegúrate de que las variables de entorno estén configuradas correctamente

### RabbitMQ no conecta
- Verifica que RabbitMQ esté corriendo: `docker ps` o `rabbitmq-server`
- Accede a la consola de administración: http://localhost:15672

### Base de datos no conecta
- Verifica que PostgreSQL esté corriendo
- Confirma las credenciales en `application.properties`

## 📄 Licencia

Proyecto académico - Universidad del Cauca

## 👥 Autores

Persona 4: El Comunicador & UX/Cliente
