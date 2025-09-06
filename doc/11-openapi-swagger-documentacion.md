# OpenAPI/Swagger - Documentación Automática de API

## Índice
1. [Introducción](#introducción)
2. [Dependencias](#dependencias)
3. [Configuración OpenAPI](#configuración-openapi)
4. [Configuración de Seguridad](#configuración-de-seguridad)
5. [Anotaciones en Controladores](#anotaciones-en-controladores)
6. [Acceso a la Documentación](#acceso-a-la-documentación)
7. [Personalización Avanzada](#personalización-avanzada)
8. [Pruebas con Swagger UI](#pruebas-con-swagger-ui)
9. [Mejores Prácticas](#mejores-prácticas)

## Introducción

OpenAPI (anteriormente conocido como Swagger) es una especificación para describir APIs REST. En este proyecto hemos implementado **springdoc-openapi** que genera automáticamente la documentación de nuestra API y proporciona una interfaz web interactiva (Swagger UI) para probar los endpoints.

### ¿Por qué usar OpenAPI?

- **Documentación automática**: Se genera automáticamente desde el código
- **Interfaz interactiva**: Permite probar endpoints directamente desde el navegador
- **Estándar de la industria**: Ampliamente adoptado y compatible con múltiples herramientas
- **Sincronización**: La documentación siempre está actualizada con el código
- **Facilita la integración**: Otros desarrolladores pueden entender y usar la API fácilmente

## Dependencias

### Maven Dependency

En el archivo `pom.xml` se agregó la siguiente dependencia:

```xml
<!-- OpenAPI/Swagger Dependencies -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### ¿Por qué esta dependencia?

- **springdoc-openapi-starter-webmvc-ui**: Incluye todo lo necesario para Spring Boot 3.x
- **Integración automática**: Se configura automáticamente con Spring Boot
- **Swagger UI incluido**: No necesita configuración adicional
- **Compatible con Spring Security**: Funciona con autenticación

## Configuración OpenAPI

### Archivo: `OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name:PI Backend 2}")
    private String applicationName;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servidores())
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(componentesSeguridad());
    }
}
```

### Componentes de la Configuración

#### 1. **Información de la API (`apiInfo()`)**

```java
private Info apiInfo() {
    return new Info()
            .title(applicationName + " - API REST")
            .description("API REST para gestión de usuarios, items y préstamos...")
            .version("1.0.0")
            .contact(contacto())
            .license(licencia());
}
```

**¿Por qué esta configuración?**
- **Título dinámico**: Usa el nombre de la aplicación desde `application.properties`
- **Descripción detallada**: Incluye características principales y credenciales de prueba
- **Versionado**: Facilita el control de versiones de la API
- **Contacto y licencia**: Información importante para usuarios de la API

#### 2. **Servidores (`servers()`)**

```java
.servers(List.of(
    new Server()
        .url("http://localhost:" + serverPort)
        .description("Servidor de Desarrollo Local"),
    new Server()
        .url("https://api.ejemplo.com")
        .description("Servidor de Producción")
))
```

**¿Por qué múltiples servidores?**
- **Flexibilidad**: Permite cambiar entre entornos desde Swagger UI
- **Puerto dinámico**: Lee el puerto desde la configuración
- **Preparado para producción**: Incluye URL de producción como ejemplo

#### 3. **Seguridad (`componentesSeguridad()`)**

```java
.addSecurityItem(new SecurityRequirement().addList("basicAuth"))
.components(new Components()
    .addSecuritySchemes("basicAuth",
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .description("Autenticación HTTP Basic...")
    )
)
```

**¿Por qué esta configuración de seguridad?**
- **HTTP Basic Auth**: Coincide con la configuración de Spring Security
- **Descripción clara**: Incluye las credenciales de prueba
- **Aplicación global**: Se aplica a todos los endpoints que lo requieran

## Configuración de Seguridad

### Actualización en `SecurityConfig.java`

Se agregaron las siguientes rutas públicas para permitir acceso a Swagger:

```java
// Endpoints de Swagger/OpenAPI (públicos)
.requestMatchers("/swagger-ui/**").permitAll()
.requestMatchers("/swagger-ui.html").permitAll()
.requestMatchers("/v3/api-docs/**").permitAll()
.requestMatchers("/swagger-resources/**").permitAll()
.requestMatchers("/webjars/**").permitAll()
```

### ¿Por qué estas rutas?

- **`/swagger-ui/**`**: Interfaz web de Swagger UI
- **`/swagger-ui.html`**: Página principal de Swagger
- **`/v3/api-docs/**`**: Especificación OpenAPI en formato JSON
- **`/swagger-resources/**`**: Recursos adicionales de Swagger
- **`/webjars/**`**: Librerías JavaScript y CSS

## Anotaciones en Controladores

### Anotaciones a Nivel de Clase

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "basicAuth")
public class UserController {
```

#### Explicación de Anotaciones:

- **`@Tag`**: Agrupa endpoints en la documentación
- **`@SecurityRequirement`**: Indica que requiere autenticación

### Anotaciones a Nivel de Método

```java
@GetMapping
@Operation(
    summary = "Obtener todos los usuarios",
    description = "Retorna una lista de todos los usuarios registrados en el sistema. Requiere rol ADMIN."
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de usuarios obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDTO.class)
        )
    ),
    @ApiResponse(
        responseCode = "401", 
        description = "No autenticado - Credenciales requeridas"
    ),
    @ApiResponse(
        responseCode = "403", 
        description = "Acceso denegado - Requiere rol ADMIN"
    )
})
public List<UserDTO> getAll() {
```

#### Explicación de Anotaciones:

- **`@Operation`**: Describe el propósito del endpoint
- **`@ApiResponses`**: Define todas las posibles respuestas HTTP
- **`@ApiResponse`**: Describe una respuesta específica
- **`@Content`**: Define el tipo de contenido de la respuesta
- **`@Schema`**: Especifica el modelo de datos

### Anotaciones para Parámetros

```java
public ResponseEntity<UserDTO> getById(
    @Parameter(description = "ID del usuario a buscar", required = true)
    @PathVariable Long id
) {
```

- **`@Parameter`**: Documenta parámetros de entrada

## Acceso a la Documentación

### URLs Disponibles

1. **Swagger UI (Interfaz Interactiva)**
   ```
   http://localhost:8080/swagger-ui.html
   ```
   - Interfaz web para explorar y probar la API
   - Permite ejecutar requests directamente
   - Muestra ejemplos de request/response

2. **Especificación OpenAPI (JSON)**
   ```
   http://localhost:8080/v3/api-docs
   ```
   - Especificación completa en formato JSON
   - Útil para generar clientes automáticamente
   - Compatible con herramientas de terceros

3. **Especificación OpenAPI (YAML)**
   ```
   http://localhost:8080/v3/api-docs.yaml
   ```
   - Mismo contenido en formato YAML
   - Más legible para humanos

### Iniciar la Aplicación

**En Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**En Linux/Mac:**
```bash
./mvnw spring-boot:run
```

## Personalización Avanzada

### Configuración en `application.properties`

Puedes agregar estas propiedades para personalizar Swagger:

```properties
# Configuración de Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true

# Configuración de OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.show-actuator=true
springdoc.swagger-ui.disable-swagger-default-url=true
```

### ¿Qué hace cada propiedad?

- **`operationsSorter=method`**: Ordena endpoints por método HTTP
- **`tagsSorter=alpha`**: Ordena tags alfabéticamente
- **`tryItOutEnabled=true`**: Habilita el botón "Try it out"
- **`show-actuator=true`**: Incluye endpoints de Actuator
- **`disable-swagger-default-url=true`**: Deshabilita URL por defecto

## Pruebas con Swagger UI

### 1. Acceder a Swagger UI

1. Inicia la aplicación
2. Abre el navegador en `http://localhost:8080/swagger-ui.html`
3. Verás la interfaz de Swagger con todos los endpoints documentados

### 2. Autenticación

1. Haz clic en el botón **"Authorize"** (candado verde)
2. Ingresa las credenciales:
   - **Username**: `admin`
   - **Password**: `admin123`
3. Haz clic en **"Authorize"**
4. Cierra el modal

### 3. Probar Endpoints

1. Selecciona un endpoint (ej: `GET /api/users`)
2. Haz clic en **"Try it out"**
3. Completa los parámetros si es necesario
4. Haz clic en **"Execute"**
5. Revisa la respuesta en la sección **"Response"**

### 4. Ejemplos de Pruebas

#### Obtener todos los usuarios (requiere ADMIN)
```
GET /api/users
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Crear un nuevo item (requiere ADMIN o USER)
```
POST /api/items
Content-Type: application/json
Authorization: Basic dXNlcjp1c2VyMTIz

{
  "name": "Laptop Dell",
  "description": "Laptop para desarrollo",
  "quantity": 5
}
```

## Mejores Prácticas

### 1. Documentación Completa

- **Siempre documenta**: Todos los endpoints deben tener `@Operation`
- **Describe parámetros**: Usa `@Parameter` para claridad
- **Incluye ejemplos**: Ayuda a los usuarios a entender el formato
- **Documenta errores**: Incluye todas las posibles respuestas HTTP

### 2. Organización

- **Usa tags consistentes**: Agrupa endpoints lógicamente
- **Nombres descriptivos**: Usa nombres claros para operaciones
- **Versionado**: Mantén versiones de la API documentadas

### 3. Seguridad

- **Documenta autenticación**: Especifica qué endpoints requieren auth
- **Roles claros**: Indica qué roles pueden acceder a cada endpoint
- **No expongas secretos**: Nunca incluyas credenciales reales en ejemplos

### 4. Mantenimiento

- **Actualiza regularmente**: Mantén la documentación sincronizada
- **Revisa descripciones**: Asegúrate de que sean precisas
- **Prueba endpoints**: Verifica que los ejemplos funcionen

## Comandos Útiles

### Generar documentación estática

```bash
# Descargar especificación OpenAPI
curl http://localhost:8080/v3/api-docs > api-docs.json

# Generar documentación HTML (requiere swagger-codegen)
swagger-codegen generate -i api-docs.json -l html2 -o docs/
```

### Validar especificación

```bash
# Usando swagger-codegen
swagger-codegen validate -i http://localhost:8080/v3/api-docs
```

## Troubleshooting

### Problemas Comunes

1. **Swagger UI no carga**
   - Verifica que la aplicación esté ejecutándose
   - Revisa que las rutas estén permitidas en SecurityConfig
   - Comprueba la URL: `http://localhost:8080/swagger-ui.html`

2. **Endpoints no aparecen**
   - Verifica que los controladores tengan `@RestController`
   - Asegúrate de que estén en el package escaneado por Spring
   - Revisa que no haya errores en las anotaciones OpenAPI

3. **Autenticación no funciona**
   - Verifica las credenciales: `admin/admin123` o `user/user123`
   - Asegúrate de hacer clic en "Authorize" antes de probar
   - Revisa que Spring Security esté configurado correctamente

4. **Respuestas 403 Forbidden**
   - Verifica que el usuario tenga el rol correcto
   - Revisa la configuración de autorización en SecurityConfig
   - Asegúrate de estar autenticado

### Logs Útiles

Para debug, agrega en `application.properties`:

```properties
# Logs de Spring Security
logging.level.org.springframework.security=DEBUG

# Logs de SpringDoc
logging.level.org.springdoc=DEBUG
```

## Conclusión

La implementación de OpenAPI/Swagger en este proyecto proporciona:

- **Documentación automática** y siempre actualizada
- **Interfaz interactiva** para probar endpoints
- **Estándar de la industria** para APIs REST
- **Integración completa** con Spring Security
- **Facilidad de uso** para desarrolladores y usuarios de la API

Esta configuración facilita el desarrollo, testing y adopción de la API, mejorando significativamente la experiencia del desarrollador.