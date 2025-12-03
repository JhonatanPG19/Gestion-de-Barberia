# Sistema de Login Personalizado - Implementación Completa

## ✅ Cambios Implementados

### Backend (ms-usuarios)

#### 1. **DTOs Creados**
- `LoginRequestDTO.java` - Recibe username y password
- `LoginResponseDTO.java` - Retorna token, userId, username y rol

#### 2. **Utilidad JWT**
- `JwtUtil.java` - Genera y valida tokens JWT
- Tokens válidos por 24 horas
- Usa HS256 para firmar tokens

#### 3. **Repositorio Actualizado**
- Agregado método `findByUsername(String username)` en `IUsuarioRepository`

#### 4. **Servicio Actualizado**
- Agregado método `login(LoginRequestDTO)` en `IUsuarioService`
- Implementación en `UsuarioServiceImpl`:
  - Valida credenciales contra la base de datos
  - Genera token JWT
  - Retorna información del usuario

#### 5. **Controlador Actualizado**
- Endpoint `POST /api/v1/usuarios/login`
- CORS habilitado para `http://localhost:4200`
- Retorna 401 Unauthorized si las credenciales son inválidas

#### 6. **Seguridad Actualizada**
- Endpoint `/login` permitido sin autenticación
- Configuración de CORS en el controlador

#### 7. **Dependencias Maven**
- Agregadas librerías JJWT (jjwt-api, jjwt-impl, jjwt-jackson)

### Frontend (Angular)

#### 1. **AuthService Actualizado**
- Método `login(username, password)` que llama al backend
- Guarda token, userId, username y rol en localStorage

#### 2. **LoginComponent Creado**
- Formulario reactivo con validación
- Manejo de estados de carga y errores
- Navegación basada en roles después del login

#### 3. **Rutas Simplificadas**
- Eliminada integración con Keycloak
- Ruta `/login` como página de inicio
- Rutas directas sin guards de autenticación

#### 4. **AppConfig Limpio**
- Removida toda la configuración de Keycloak
- Configuración simple con HttpClient

## 🚀 Cómo Probar

### 1. Preparar la Base de Datos

Ejecuta el script SQL para crear usuarios de prueba:

```bash
psql -U postgres -d usuarios_db -f test-users.sql
```

O manualmente en pgAdmin/DBeaver:

```sql
INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('admin', 'Admin', 'Sistema', '3001234567', 'admin123', 'admin@barberia.com', 'ADMIN');

INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('barbero1', 'Juan', 'Pérez', '3009876543', 'barbero123', 'barbero@barberia.com', 'BARBERO');

INSERT INTO usuarios (username, nombre, apellido, telefono, password, correo, rol) 
VALUES ('cliente1', 'Carlos', 'Gómez', '3001112233', 'cliente123', 'cliente@gmail.com', 'CLIENTE');
```

### 2. Compilar el Backend

Necesitas configurar JAVA_HOME primero. En Windows:

```cmd
set JAVA_HOME=C:\ruta\a\tu\jdk
cd ms-usuarios
mvn clean install -DskipTests
```

O usa tu IDE (IntelliJ IDEA, Eclipse, VS Code con Extension Pack for Java):
- Click derecho en el proyecto → Maven → Reload Project
- Ejecuta `MsUsuariosApplication.java`

### 3. Iniciar el Backend

El servicio iniciará en `http://localhost:8081`

### 4. Iniciar el Frontend

```bash
cd frontend
npm install
ng serve
```

Abre `http://localhost:4200` en tu navegador.

### 5. Probar el Login

Usa estas credenciales de prueba:

**Usuario Admin:**
- Username: `admin`
- Password: `admin123`
- Redirige a: `/admin/barberos`

**Usuario Barbero:**
- Username: `barbero1`
- Password: `barbero123`
- Redirige a: `/barbero/agenda`

**Usuario Cliente:**
- Username: `cliente1`
- Password: `cliente123`
- Redirige a: `/reservas`

## 🔧 Testing con Postman/cURL

### Login Request

```bash
curl -X POST http://localhost:8081/api/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Respuesta Esperada

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "admin",
  "rol": "ADMIN"
}
```

### Error de Credenciales Inválidas

```json
{
  "error": "Credenciales inválidas"
}
```

## 📋 Próximos Pasos (Opcional)

### 1. **Proteger Rutas en el Frontend**
Crear un guard que verifique si existe token en localStorage:

```typescript
export const authGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('token');
  if (!token) {
    inject(Router).navigate(['/login']);
    return false;
  }
  return true;
};
```

### 2. **Interceptor HTTP**
Agregar token a todas las peticiones automáticamente:

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
```

### 3. **Logout**
Agregar función en AuthService:

```typescript
logout(): void {
  localStorage.clear();
  this.router.navigate(['/login']);
}
```

### 4. **Encriptar Contraseñas**
En el backend, usar BCrypt para hashear contraseñas:

```java
// Al registrar
String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
usuario.setPassword(hashedPassword);

// Al login
if (!BCrypt.checkpw(loginRequest.getPassword(), usuario.getPassword())) {
    throw new RuntimeException("Credenciales inválidas");
}
```

### 5. **Validar Token en Backend**
Crear un filtro JWT para validar tokens en requests protegidos.

## 🔐 Seguridad

⚠️ **Advertencias de Seguridad:**

1. **Contraseñas en texto plano**: Actualmente las contraseñas se guardan sin encriptar. En producción, SIEMPRE usa BCrypt.

2. **Clave secreta hardcodeada**: La clave JWT está en el código. En producción, usa variables de entorno.

3. **Token sin expiración validada**: El frontend no verifica si el token expiró. Agregar lógica de refresh token.

4. **CORS abierto**: Solo está configurado para localhost:4200. En producción, especifica dominios exactos.

## 🐛 Troubleshooting

### Error: "JAVA_HOME is not defined"
Configura la variable de entorno:
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21
```

### Error: "Cannot connect to database"
Verifica que PostgreSQL esté corriendo y la base de datos `usuarios_db` exista.

### Error CORS en el navegador
Verifica que el backend tenga `@CrossOrigin(origins = "http://localhost:4200")` en el controlador.

### El login no redirige
Revisa la consola del navegador (F12) para ver errores. Verifica que las rutas existan en `app.routes.ts`.

## 📦 Estructura de Archivos Modificados/Creados

```
ms-usuarios/
├── src/main/java/com/barberia/ms_usuarios/
│   ├── capaControlador/
│   │   └── RegistroController.java (modificado - agregado @CrossOrigin y endpoint login)
│   ├── capaFachada/
│   │   ├── dto/
│   │   │   ├── LoginRequestDTO.java (nuevo)
│   │   │   └── LoginResponseDTO.java (nuevo)
│   │   └── service/
│   │       ├── IUsuarioService.java (modificado - agregado método login)
│   │       └── UsuarioServiceImpl.java (modificado - implementado login)
│   ├── capaAccesoADatos/repository/
│   │   └── IUsuarioRepository.java (modificado - agregado findByUsername)
│   ├── config/
│   │   └── SecurityConfig.java (modificado - permitir /login sin auth)
│   └── util/
│       └── JwtUtil.java (nuevo)
├── pom.xml (modificado - agregadas dependencias JJWT)
└── test-users.sql (nuevo)

frontend/src/app/
├── auth/login/
│   ├── login.component.ts (nuevo)
│   ├── login.component.html (nuevo)
│   └── login.component.css (nuevo)
├── services/
│   └── auth.service.ts (modificado - agregado método login)
├── app.config.ts (modificado - removido Keycloak)
└── app.routes.ts (modificado - simplificado rutas)
```

## ✨ Características Implementadas

- ✅ Login con username y password
- ✅ Generación de tokens JWT
- ✅ Validación de credenciales
- ✅ Navegación basada en roles
- ✅ Almacenamiento de sesión en localStorage
- ✅ CORS configurado para frontend
- ✅ Formulario reactivo con validaciones
- ✅ Manejo de errores y estados de carga
- ✅ Diseño moderno con gradientes

## 🎯 Endpoints Disponibles

| Método | Endpoint | Autenticación | Descripción |
|--------|----------|---------------|-------------|
| POST | `/api/v1/usuarios/login` | No | Autenticar usuario |
| POST | `/api/v1/usuarios/registro` | No | Registrar cliente |
| GET | `/api/v1/usuarios` | Sí (Admin) | Listar usuarios |
| GET | `/api/v1/usuarios/{id}` | Sí | Obtener usuario por ID |
| GET | `/api/v1/usuarios/{id}/existe` | No | Verificar si existe usuario |

---

**Implementación completa por GitHub Copilot** 🤖
