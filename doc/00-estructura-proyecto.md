# 0. Estructura del Proyecto Spring Boot

## 🎯 Objetivos

En esta sección aprenderás a:
- Entender la estructura estándar de un proyecto Spring Boot
- Conocer la organización de paquetes en Java
- Crear la estructura de carpetas correcta
- Aplicar las mejores prácticas de organización de código

## 📁 Estructura General del Proyecto

```
pi_backend2/
├── .gitattributes
├── .gitignore
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── README.md
├── doc/                              # Documentación del proyecto
│   ├── 00-estructura-proyecto.md
│   ├── 01-configuracion-inicial.md
│   ├── 02-configuracion-database.md
│   ├── 03-entidades-modelos.md
│   ├── 04-dtos-mapeo.md
│   ├── 05-repositorios-acceso-datos.md
│   ├── 06-servicios-logica-negocio.md
│   ├── 07-controladores-rest.md
│   ├── 08-actuator-monitoreo.md
│   ├── 09-validacion-manejo-errores.md
│   └── README.md
├── info.json
├── mvnw                              # Maven Wrapper (Unix)
├── mvnw.cmd                          # Maven Wrapper (Windows)
├── pom.xml                           # Configuración de Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── pib2/         # Paquete base de la aplicación
│   │   │               ├── Pib2Application.java
│   │   │               ├── config/   # Configuraciones
│   │   │               ├── controller/ # Controladores REST
│   │   │               ├── dto/      # Data Transfer Objects
│   │   │               ├── entity/   # Entidades JPA
│   │   │               ├── exception/ # Excepciones personalizadas
│   │   │               ├── health/   # Health Indicators
│   │   │               ├── info/     # Info Contributors
│   │   │               ├── metrics/  # Métricas personalizadas
│   │   │               ├── repository/ # Repositorios JPA
│   │   │               ├── service/  # Servicios de negocio
│   │   │               └── util/     # Utilidades
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── static/               # Archivos estáticos
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── pib2/         # Tests del proyecto
└── test-endpoints.ps1                # Script de pruebas
```

## 🏗️ Estructura de Paquetes Java

### Paquete Base: `com.example.pib2`

El paquete base sigue la convención de Java:
- **com**: Dominio comercial
- **example**: Nombre de la organización
- **pib2**: Nombre del proyecto

### Organización por Capas

#### 1. **config/** - Configuraciones
```java
com.example.pib2.config/
├── DatabaseConfig.java           // Configuración de base de datos
├── SecurityConfig.java           // Configuración de seguridad
├── WebConfig.java               // Configuración web
├── ActuatorConfig.java          // Configuración de Actuator
└── InternationalizationConfig.java // Configuración de i18n
```

#### 2. **entity/** - Entidades JPA
```java
com.example.pib2.entity/
├── User.java                    // Entidad Usuario
├── Item.java                    // Entidad Artículo
├── Loan.java                    // Entidad Préstamo
└── LoanHistory.java             // Entidad Historial de Préstamos
```

#### 3. **dto/** - Data Transfer Objects
```java
com.example.pib2.dto/
├── UserDTO.java                 // DTO para Usuario
├── ItemDTO.java                 // DTO para Artículo
├── LoanDTO.java                 // DTO para Préstamo
├── LoanHistoryDTO.java          // DTO para Historial
├── CreateUserDTO.java           // DTO para crear usuario
├── UpdateUserDTO.java           // DTO para actualizar usuario
└── ErrorResponseDTO.java        // DTO para respuestas de error
```

#### 4. **repository/** - Repositorios de Datos
```java
com.example.pib2.repository/
├── UserRepository.java          // Repositorio de Usuario
├── ItemRepository.java          // Repositorio de Artículo
├── LoanRepository.java          // Repositorio de Préstamo
└── LoanHistoryRepository.java   // Repositorio de Historial
```

#### 5. **service/** - Servicios de Negocio
```java
com.example.pib2.service/
├── UserService.java             // Servicio de Usuario
├── ItemService.java             // Servicio de Artículo
├── LoanService.java             // Servicio de Préstamo
├── LoanHistoryService.java      // Servicio de Historial
└── impl/                        // Implementaciones
    ├── UserServiceImpl.java
    ├── ItemServiceImpl.java
    ├── LoanServiceImpl.java
    └── LoanHistoryServiceImpl.java
```

#### 6. **controller/** - Controladores REST
```java
com.example.pib2.controller/
├── UserController.java          // Controlador de Usuario
├── ItemController.java          // Controlador de Artículo
├── LoanController.java          // Controlador de Préstamo
└── LoanHistoryController.java   // Controlador de Historial
```

#### 7. **exception/** - Excepciones Personalizadas
```java
com.example.pib2.exception/
├── GlobalExceptionHandler.java  // Manejador global de excepciones
├── ResourceNotFoundException.java // Excepción de recurso no encontrado
├── BadRequestException.java     // Excepción de petición incorrecta
├── DuplicateResourceException.java // Excepción de recurso duplicado
└── ValidationException.java     // Excepción de validación
```

#### 8. **health/** - Health Indicators
```java
com.example.pib2.health/
├── DatabaseHealthIndicator.java // Indicador de salud de BD
└── ExternalServiceHealthIndicator.java // Indicador de servicios externos
```

#### 9. **metrics/** - Métricas Personalizadas
```java
com.example.pib2.metrics/
└── CustomMetrics.java           // Métricas personalizadas
```

#### 10. **info/** - Info Contributors
```java
com.example.pib2.info/
└── CustomInfoContributor.java   // Contribuidor de información
```

#### 11. **util/** - Utilidades
```java
com.example.pib2.util/
├── DateUtils.java               // Utilidades de fecha
├── StringUtils.java             // Utilidades de cadenas
└── ValidationUtils.java         // Utilidades de validación
```

## 🛠️ Cómo Crear la Estructura de Paquetes

### Paso 1: Crear Paquete Base

1. **En tu IDE (IntelliJ IDEA, Eclipse, VS Code)**:
   - Navega a `src/main/java`
   - Clic derecho → New → Package
   - Nombre: `com.example.pib2`

2. **Desde línea de comandos**:
   ```bash
   mkdir -p src/main/java/com/example/pib2
   ```

### Paso 2: Crear Subpaquetes

```bash
# Crear todos los paquetes de una vez
mkdir -p src/main/java/com/example/pib2/config
mkdir -p src/main/java/com/example/pib2/controller
mkdir -p src/main/java/com/example/pib2/dto
mkdir -p src/main/java/com/example/pib2/entity
mkdir -p src/main/java/com/example/pib2/exception
mkdir -p src/main/java/com/example/pib2/health
mkdir -p src/main/java/com/example/pib2/info
mkdir -p src/main/java/com/example/pib2/metrics
mkdir -p src/main/java/com/example/pib2/repository
mkdir -p src/main/java/com/example/pib2/service
mkdir -p src/main/java/com/example/pib2/service/impl
mkdir -p src/main/java/com/example/pib2/util
```

### Paso 3: Crear Estructura de Tests

```bash
# Crear estructura de tests
mkdir -p src/test/java/com/example/pib2/controller
mkdir -p src/test/java/com/example/pib2/service
mkdir -p src/test/java/com/example/pib2/repository
mkdir -p src/test/resources
```

### Paso 4: Crear Archivos de Recursos

```bash
# Crear archivos de configuración
touch src/main/resources/application.properties
touch src/main/resources/application-dev.properties
touch src/main/resources/application-prod.properties
touch src/test/resources/application-test.properties
```

## 📝 Convenciones de Nomenclatura

### Paquetes
- **Minúsculas**: `com.example.pib2.controller`
- **Descriptivos**: `service`, `repository`, `controller`
- **Singulares**: `entity` (no `entities`)

### Clases
- **PascalCase**: `UserController`, `ItemService`
- **Descriptivas**: `UserRepository`, `LoanHistoryDTO`
- **Sufijos claros**: `Controller`, `Service`, `Repository`, `DTO`

### Archivos
- **Configuración**: `application.properties`
- **Perfiles**: `application-{profile}.properties`
- **Tests**: `application-test.properties`

## 🎨 Mejores Prácticas

### 1. Separación de Responsabilidades

```java
// ✅ Correcto: Cada clase tiene una responsabilidad
com.example.pib2.controller.UserController  // Solo maneja HTTP
com.example.pib2.service.UserService        // Solo lógica de negocio
com.example.pib2.repository.UserRepository  // Solo acceso a datos

// ❌ Incorrecto: Mezclar responsabilidades
com.example.pib2.UserEverything             // Hace todo
```

### 2. Organización por Funcionalidad vs Capas

**Por Capas (Recomendado para proyectos pequeños-medianos):**
```
com.example.pib2/
├── controller/
├── service/
├── repository/
└── entity/
```

**Por Funcionalidad (Para proyectos grandes):**
```
com.example.pib2/
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   └── User.java
├── item/
│   ├── ItemController.java
│   ├── ItemService.java
│   ├── ItemRepository.java
│   └── Item.java
└── loan/
    ├── LoanController.java
    ├── LoanService.java
    ├── LoanRepository.java
    └── Loan.java
```

### 3. Configuración por Entornos

```
src/main/resources/
├── application.properties           # Configuración base
├── application-dev.properties       # Desarrollo
├── application-test.properties      # Testing
└── application-prod.properties      # Producción
```

### 4. Estructura de Tests

```
src/test/java/com/example/pib2/
├── controller/
│   ├── UserControllerTest.java      # Tests de controlador
│   └── ItemControllerTest.java
├── service/
│   ├── UserServiceTest.java         # Tests de servicio
│   └── ItemServiceTest.java
├── repository/
│   ├── UserRepositoryTest.java      # Tests de repositorio
│   └── ItemRepositoryTest.java
└── integration/
    ├── UserIntegrationTest.java     # Tests de integración
    └── ItemIntegrationTest.java
```

## 🔧 Comandos Útiles para Crear Estructura

### Script PowerShell para Windows

```powershell
# crear-estructura.ps1
$basePath = "src/main/java/com/example/pib2"
$testPath = "src/test/java/com/example/pib2"
$resourcesPath = "src/main/resources"
$testResourcesPath = "src/test/resources"

# Crear directorios principales
$directories = @(
    "$basePath/config",
    "$basePath/controller",
    "$basePath/dto",
    "$basePath/entity",
    "$basePath/exception",
    "$basePath/health",
    "$basePath/info",
    "$basePath/metrics",
    "$basePath/repository",
    "$basePath/service",
    "$basePath/service/impl",
    "$basePath/util",
    "$testPath/controller",
    "$testPath/service",
    "$testPath/repository",
    "$testPath/integration",
    $resourcesPath,
    "$resourcesPath/static",
    $testResourcesPath
)

foreach ($dir in $directories) {
    New-Item -ItemType Directory -Path $dir -Force
    Write-Host "Creado: $dir"
}

# Crear archivos de configuración
$configFiles = @(
    "$resourcesPath/application.properties",
    "$resourcesPath/application-dev.properties",
    "$resourcesPath/application-prod.properties",
    "$testResourcesPath/application-test.properties"
)

foreach ($file in $configFiles) {
    New-Item -ItemType File -Path $file -Force
    Write-Host "Creado: $file"
}

Write-Host "Estructura del proyecto creada exitosamente!"
```

### Script Bash para Linux/Mac

```bash
#!/bin/bash
# crear-estructura.sh

BASE_PATH="src/main/java/com/example/pib2"
TEST_PATH="src/test/java/com/example/pib2"
RESOURCES_PATH="src/main/resources"
TEST_RESOURCES_PATH="src/test/resources"

# Crear directorios
directories=(
    "$BASE_PATH/config"
    "$BASE_PATH/controller"
    "$BASE_PATH/dto"
    "$BASE_PATH/entity"
    "$BASE_PATH/exception"
    "$BASE_PATH/health"
    "$BASE_PATH/info"
    "$BASE_PATH/metrics"
    "$BASE_PATH/repository"
    "$BASE_PATH/service"
    "$BASE_PATH/service/impl"
    "$BASE_PATH/util"
    "$TEST_PATH/controller"
    "$TEST_PATH/service"
    "$TEST_PATH/repository"
    "$TEST_PATH/integration"
    "$RESOURCES_PATH"
    "$RESOURCES_PATH/static"
    "$TEST_RESOURCES_PATH"
)

for dir in "${directories[@]}"; do
    mkdir -p "$dir"
    echo "Creado: $dir"
done

# Crear archivos de configuración
config_files=(
    "$RESOURCES_PATH/application.properties"
    "$RESOURCES_PATH/application-dev.properties"
    "$RESOURCES_PATH/application-prod.properties"
    "$TEST_RESOURCES_PATH/application-test.properties"
)

for file in "${config_files[@]}"; do
    touch "$file"
    echo "Creado: $file"
done

echo "Estructura del proyecto creada exitosamente!"
```

## 🚨 Errores Comunes a Evitar

### 1. Paquetes Mal Nombrados
```java
// ❌ Incorrecto
com.example.pib2.Controllers  // Mayúscula
com.example.pib2.ENTITY       // Todo mayúsculas
com.example.pib2.dto_package  // Guiones bajos

// ✅ Correcto
com.example.pib2.controller
com.example.pib2.entity
com.example.pib2.dto
```

### 2. Clases en Paquetes Incorrectos
```java
// ❌ Incorrecto
com.example.pib2.controller.UserService     // Servicio en controller
com.example.pib2.entity.UserController      // Controlador en entity

// ✅ Correcto
com.example.pib2.service.UserService
com.example.pib2.controller.UserController
```

### 3. Dependencias Circulares
```java
// ❌ Incorrecto: Controller depende de Repository directamente
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;  // Saltar la capa de servicio
}

// ✅ Correcto: Controller depende de Service
@RestController
public class UserController {
    @Autowired
    private UserService userService;        // Respetar las capas
}
```

## 📚 Conceptos Clave Aprendidos

- **Estructura de Proyecto**: Organización estándar de Spring Boot
- **Paquetes Java**: Convenciones de nomenclatura y organización
- **Separación de Capas**: Controller, Service, Repository, Entity
- **Configuración por Entornos**: Perfiles de Spring Boot
- **Mejores Prácticas**: Organización limpia y mantenible
- **Herramientas**: Scripts para automatizar creación de estructura

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Configurar el proyecto Spring Boot inicial
- Configurar Maven y dependencias
- Crear la clase principal de la aplicación
- Configurar el servidor embebido
- Establecer la configuración básica

---

[**Volver al Índice**](README.md) | [**Siguiente: Configuración Inicial →**](01-configuracion-inicial.md)