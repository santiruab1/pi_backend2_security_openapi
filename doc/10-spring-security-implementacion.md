# Implementación de Spring Security con Autenticación Básica y Roles

## Índice
1. [Introducción](#introducción)
2. [Dependencias Agregadas](#dependencias-agregadas)
3. [Configuración de Seguridad](#configuración-de-seguridad)
4. [Entidad User Modificada](#entidad-user-modificada)
5. [Repositorio User Actualizado](#repositorio-user-actualizado)
6. [Servicio UserDetailsService](#servicio-userdetailsservice)
7. [Carga de Datos Iniciales](#carga-de-datos-iniciales)
8. [Endpoints Protegidos](#endpoints-protegidos)
9. [Credenciales de Prueba](#credenciales-de-prueba)
10. [Cómo Probar la Implementación](#cómo-probar-la-implementación)

## Introducción

Este documento detalla la implementación completa de Spring Security en el proyecto PI Backend 2. La implementación incluye:

- **Autenticación HTTP Basic**: Los usuarios deben proporcionar username y password
- **Autorización basada en roles**: Dos roles principales (ADMIN y USER)
- **Encriptación de contraseñas**: Usando BCrypt
- **Integración con JPA**: Los usuarios se almacenan en base de datos
- **Datos de prueba**: Usuarios predefinidos para testing

## Dependencias Agregadas

### Archivo: `pom.xml`

**¿Qué se agregó?**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**¿Dónde se agregó?**
En la sección `<dependencies>`, después de `spring-boot-starter-actuator` y antes de `h2`.

**¿Por qué se agregó aquí?**
- Spring Boot Starter Security incluye todas las dependencias necesarias para seguridad
- Se coloca con las demás dependencias de Spring Boot para mantener organización
- No requiere versión específica ya que hereda del parent de Spring Boot

## Configuración de Seguridad

### Archivo: `src/main/java/com/example/pib2/config/SecurityConfig.java`

**¿Qué se creó?**
Una clase de configuración completa que define:

1. **Cadena de filtros de seguridad (`SecurityFilterChain`)**
2. **Codificador de contraseñas (`PasswordEncoder`)**
3. **Administrador de autenticación (`AuthenticationManager`)**
4. **Proveedor de autenticación DAO (`DaoAuthenticationProvider`)**

**¿Dónde se creó?**
En un nuevo paquete `config` dentro de la estructura principal del proyecto.

**¿Por qué se configuró así?**

#### Anotaciones utilizadas:
- `@Configuration`: Marca la clase como fuente de configuración de Spring
- `@EnableWebSecurity`: Habilita la configuración de seguridad web
- `@EnableMethodSecurity(prePostEnabled = true)`: Permite usar anotaciones como `@PreAuthorize`

#### Configuración de endpoints:
```java
.authorizeHttpRequests(authz -> authz
    // Endpoints públicos (sin autenticación)
    .requestMatchers("/actuator/**").permitAll()
    .requestMatchers("/h2-console/**").permitAll()
    
    // Endpoints que requieren rol ADMIN
    .requestMatchers("/api/users/**").hasRole("ADMIN")
    .requestMatchers("/api/items/**").hasAnyRole("ADMIN", "USER")
    
    // Endpoints que requieren rol USER o ADMIN
    .requestMatchers("/api/loans/**").hasAnyRole("ADMIN", "USER")
    .requestMatchers("/api/loan-history/**").hasAnyRole("ADMIN", "USER")
    
    // Cualquier otro request requiere autenticación
    .anyRequest().authenticated()
)
```

**Justificación de la configuración:**
- **Actuator y H2**: Públicos para monitoreo y desarrollo
- **Users**: Solo ADMIN puede gestionar usuarios
- **Items**: Ambos roles pueden ver items
- **Loans/History**: Ambos roles pueden gestionar préstamos

#### Autenticación HTTP Basic:
```java
.httpBasic(basic -> basic.realmName("PI Backend API"))
```
- Simple de implementar y probar
- Adecuado para APIs internas
- Compatible con herramientas como Postman, curl

#### Política de sesión:
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```
- **STATELESS**: No mantiene sesiones en servidor
- Ideal para APIs REST
- Cada request debe incluir credenciales

#### BCrypt para contraseñas:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
- Algoritmo seguro con salt automático
- Resistente a ataques de fuerza bruta
- Estándar de la industria

## Entidad User Modificada

### Archivo: `src/main/java/com/example/pib2/models/entities/User.java`

**¿Qué se modificó?**

1. **Implementación de UserDetails**:
```java
public class User implements UserDetails
```

2. **Nuevos campos de seguridad**:
```java
@Column(nullable = false)
private boolean enabled = true;

@Column(nullable = false)
private boolean accountNonExpired = true;

@Column(nullable = false)
private boolean accountNonLocked = true;

@Column(nullable = false)
private boolean credentialsNonExpired = true;
```

3. **Anotaciones de seguridad**:
```java
@JsonIgnore // No exponer la contraseña en JSON
@Column(nullable = false)
private String password;
```

4. **Implementación de métodos UserDetails**:
```java
@Override
@JsonIgnore
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
}
```

**¿Por qué estos cambios?**

- **UserDetails**: Interface requerida por Spring Security
- **Campos booleanos**: Control granular de estado de cuenta
- **@JsonIgnore**: Seguridad - no exponer contraseñas en APIs
- **@Column constraints**: Integridad de datos en BD
- **ROLE_ prefix**: Convención de Spring Security para roles

## Repositorio User Actualizado

### Archivo: `src/main/java/com/example/pib2/repositories/UserRepository.java`

**¿Qué se agregó?**

```java
Optional<User> findByUsername(String username);
Optional<User> findByEmail(String email);
```

**¿Por qué se agregaron?**

- **findByUsername**: Requerido por Spring Security para autenticación
- **findByEmail**: Útil para validaciones y recuperación de contraseña
- **Optional**: Manejo seguro de valores nulos
- **Naming convention**: Spring Data JPA genera automáticamente las consultas

## Servicio UserDetailsService

### Archivo: `src/main/java/com/example/pib2/config/CustomUserDetailsService.java`

**¿Qué se creó?**

Un servicio que implementa `UserDetailsService` de Spring Security.

**¿Dónde se creó?**
En el paquete `config` junto con la configuración de seguridad.

**¿Por qué se implementó?**

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado con username: " + username));
    
    return user;
}
```

- **Integración con BD**: Spring Security necesita cargar usuarios desde la base de datos
- **Manejo de errores**: `UsernameNotFoundException` para usuarios inexistentes
- **Retorno directo**: La entidad User ya implementa UserDetails
- **@Service**: Registra el componente en el contexto de Spring

## Carga de Datos Iniciales

### Archivo: `src/main/java/com/example/pib2/config/DataLoader.java`

**¿Qué se creó?**

Un componente que implementa `CommandLineRunner` para cargar datos al inicio.

**¿Por qué se implementó?**

1. **Testing inmediato**: Usuarios listos para probar
2. **Demostración**: Ejemplos de diferentes roles
3. **Desarrollo**: No necesidad de crear usuarios manualmente

**¿Cómo funciona?**

```java
@Override
public void run(String... args) throws Exception {
    if (userRepository.findByUsername("admin").isEmpty()) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        userRepository.save(admin);
    }
}
```

- **Verificación de existencia**: No duplica usuarios
- **Encriptación**: Contraseñas hasheadas con BCrypt
- **Roles diferentes**: ADMIN y USER para testing
- **Logging**: Muestra credenciales en consola

## Endpoints Protegidos

### Configuración de Autorización

| Endpoint | Roles Permitidos | Justificación |
|----------|------------------|---------------|
| `/actuator/**` | Público | Monitoreo y salud de la aplicación |
| `/h2-console/**` | Público | Base de datos de desarrollo |
| `/api/users/**` | ADMIN | Solo administradores gestionan usuarios |
| `/api/items/**` | ADMIN, USER | Ambos roles pueden ver items |
| `/api/loans/**` | ADMIN, USER | Ambos roles gestionan préstamos |
| `/api/loan-history/**` | ADMIN, USER | Ambos roles ven historial |
| Otros | Autenticado | Cualquier usuario autenticado |

### ¿Por qué esta configuración?

- **Principio de menor privilegio**: Solo el acceso mínimo necesario
- **Separación de responsabilidades**: ADMIN para gestión, USER para operaciones
- **Flexibilidad**: Fácil modificar roles por endpoint
- **Seguridad por defecto**: Todo requiere autenticación salvo excepciones explícitas

## Credenciales de Prueba

### Usuarios Creados Automáticamente

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | ADMIN | admin@example.com |
| user | user123 | USER | user@example.com |
| john | john123 | USER | john@example.com |

### ¿Por qué estas credenciales?

- **Simplicidad**: Fáciles de recordar para desarrollo
- **Variedad**: Diferentes roles para testing
- **Seguridad**: Contraseñas encriptadas en BD
- **Documentación**: Claramente documentadas

## Cómo Probar la Implementación

### 1. Iniciar la Aplicación

**En Windows (PowerShell o CMD):**
```cmd
.\mvnw.cmd spring-boot:run
```

**O usando Maven instalado globalmente:**
```cmd
mvn spring-boot:run
```

### 2. Probar Endpoint Público

**En Windows (PowerShell):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
```

**O usando curl en Windows:**
```cmd
curl http://localhost:8080/actuator/health
```

**Resultado esperado**: Respuesta sin autenticación

### 3. Probar Endpoint Protegido sin Autenticación

**En Windows (PowerShell):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users"
```

**O usando curl en Windows:**
```cmd
curl http://localhost:8080/api/users
```

**Resultado esperado**: `401 Unauthorized`

### 4. Probar con Credenciales ADMIN

**En Windows (PowerShell):**
```powershell
$credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("admin:admin123"))
$headers = @{"Authorization" = "Basic $credentials"}
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Headers $headers
```

**O usando curl en Windows:**
```cmd
curl -u admin:admin123 http://localhost:8080/api/users
```

**Resultado esperado**: Lista de usuarios (acceso permitido)

### 5. Probar con Credenciales USER en Endpoint ADMIN

**En Windows (PowerShell):**
```powershell
$credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("user:user123"))
$headers = @{"Authorization" = "Basic $credentials"}
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Headers $headers
```

**O usando curl en Windows:**
```cmd
curl -u user:user123 http://localhost:8080/api/users
```

**Resultado esperado**: `403 Forbidden`

### 6. Probar con Credenciales USER en Endpoint Permitido

**En Windows (PowerShell):**
```powershell
$credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("user:user123"))
$headers = @{"Authorization" = "Basic $credentials"}
Invoke-RestMethod -Uri "http://localhost:8080/api/items" -Headers $headers
```

**O usando curl en Windows:**
```cmd
curl -u user:user123 http://localhost:8080/api/items
```

**Resultado esperado**: Lista de items (acceso permitido)

### 7. Usando Postman - Guía Detallada

#### Configuración Inicial en Postman

1. **Abrir Postman** y crear una nueva request

2. **Configurar la URL base**:
   ```
   http://localhost:8080
   ```

#### Método 1: Configurar Authorization por Request

1. **En la pestaña "Authorization"**:
   - **Type**: Seleccionar "Basic Auth"
   - **Username**: `admin`
   - **Password**: `admin123`

2. **Configurar el endpoint**:
   - **Method**: GET
   - **URL**: `http://localhost:8080/api/users`

3. **Enviar la request** - Debería funcionar correctamente

#### Método 2: Configurar Authorization en Headers

1. **En la pestaña "Headers"**:
   - **Key**: `Authorization`
   - **Value**: `Basic YWRtaW46YWRtaW4xMjM=`
   
   *Nota: El valor es la codificación Base64 de "admin:admin123"*

#### Método 3: Usar Variables de Entorno en Postman

1. **Crear un Environment**:
   - Click en el ícono de engranaje (Settings)
   - "Manage Environments" → "Add"
   - **Environment name**: `PI Backend Local`

2. **Agregar variables**:
   ```
   Variable: base_url
   Initial Value: http://localhost:8080
   Current Value: http://localhost:8080
   
   Variable: admin_username
   Initial Value: admin
   Current Value: admin
   
   Variable: admin_password
   Initial Value: admin123
   Current Value: admin123
   
   Variable: user_username
   Initial Value: user
   Current Value: user
   
   Variable: user_password
   Initial Value: user123
   Current Value: user123
   ```

3. **Usar las variables**:
   - **URL**: `{{base_url}}/api/users`
   - **Authorization**: Basic Auth
     - Username: `{{admin_username}}`
     - Password: `{{admin_password}}`

#### Ejemplos de Requests en Postman

**1. Probar endpoint público (sin autenticación)**
```
GET {{base_url}}/actuator/health
```
*No requiere Authorization*

**2. Probar endpoint ADMIN con credenciales ADMIN**
```
GET {{base_url}}/api/users
Authorization: Basic Auth
Username: {{admin_username}}
Password: {{admin_password}}
```
*Resultado esperado: 200 OK con lista de usuarios*

**3. Probar endpoint ADMIN con credenciales USER**
```
GET {{base_url}}/api/users
Authorization: Basic Auth
Username: {{user_username}}
Password: {{user_password}}
```
*Resultado esperado: 403 Forbidden*

**4. Probar endpoint permitido para ambos roles**
```
GET {{base_url}}/api/items
Authorization: Basic Auth
Username: {{user_username}}
Password: {{user_password}}
```
*Resultado esperado: 200 OK con lista de items*

#### Crear una Collection en Postman

1. **Crear nueva Collection**: "PI Backend Security Tests"

2. **Agregar requests organizadas**:
   ```
   📁 PI Backend Security Tests
   ├── 📁 Public Endpoints
   │   ├── GET Health Check
   │   └── GET H2 Console
   ├── 📁 ADMIN Only
   │   ├── GET Users (Admin)
   │   ├── POST Create User (Admin)
   │   └── DELETE User (Admin)
   ├── 📁 USER & ADMIN
   │   ├── GET Items (User)
   │   ├── GET Loans (User)
   │   └── GET Loan History (User)
   └── 📁 Authentication Tests
       ├── GET Users (No Auth) - Should Fail
       ├── GET Users (Wrong Password) - Should Fail
       └── GET Users (USER role) - Should Fail
   ```

3. **Configurar Authorization a nivel de Collection**:
   - Click derecho en la Collection → "Edit"
   - Pestaña "Authorization"
   - Type: "Basic Auth"
   - Username: `{{admin_username}}`
   - Password: `{{admin_password}}`
   
   *Esto aplicará la autenticación a todas las requests de la collection*

#### Troubleshooting en Postman

**Error 401 Unauthorized:**
- Verificar que las credenciales sean correctas
- Verificar que la aplicación esté ejecutándose
- Verificar que el endpoint requiera autenticación

**Error 403 Forbidden:**
- El usuario está autenticado pero no tiene permisos
- Verificar que el rol del usuario sea correcto para el endpoint

**Error de conexión:**
- Verificar que la aplicación esté ejecutándose en `http://localhost:8080`
- Verificar que no haya firewall bloqueando la conexión

#### Exportar/Importar Collection

**Para compartir la configuración:**
1. Click derecho en la Collection → "Export"
2. Seleccionar "Collection v2.1"
3. Guardar el archivo JSON
4. Otros usuarios pueden importar con "Import" → seleccionar el archivo

2. **Probar diferentes endpoints** con diferentes usuarios

### 8. Verificar Logs

Al iniciar la aplicación, buscar en logs:

```
Usuario ADMIN creado: username=admin, password=admin123
Usuario USER creado: username=user, password=user123
Usuario USER creado: username=john, password=john123

=== CREDENCIALES DE PRUEBA ===
ADMIN: username=admin, password=admin123
USER: username=user, password=user123
USER: username=john, password=john123
================================
```

## Consideraciones de Seguridad

### En Desarrollo
- Contraseñas simples para facilitar testing
- H2 Console habilitado
- Logs muestran credenciales

### Para Producción (Recomendaciones)
1. **Cambiar contraseñas por defecto**
2. **Deshabilitar H2 Console**
3. **Usar variables de entorno para credenciales**
4. **Implementar HTTPS**
5. **Considerar JWT en lugar de Basic Auth**
6. **Agregar rate limiting**
7. **Implementar auditoría de accesos**

## Archivos Modificados/Creados

### Archivos Modificados
1. `pom.xml` - Dependencia Spring Security
2. `User.java` - Implementación UserDetails
3. `UserRepository.java` - Métodos de búsqueda

### Archivos Creados
1. `config/SecurityConfig.java` - Configuración principal
2. `config/CustomUserDetailsService.java` - Servicio de usuarios
3. `config/DataLoader.java` - Datos iniciales
4. `doc/10-spring-security-implementacion.md` - Esta documentación

## Conclusión

La implementación de Spring Security proporciona:

✅ **Autenticación robusta** con HTTP Basic  
✅ **Autorización granular** basada en roles  
✅ **Seguridad de contraseñas** con BCrypt  
✅ **Integración completa** con JPA/Hibernate  
✅ **Datos de prueba** listos para usar  
✅ **Configuración flexible** y extensible  
✅ **Documentación completa** para mantenimiento  

El sistema está listo para desarrollo y puede escalarse fácilmente para producción con las consideraciones de seguridad apropiadas.