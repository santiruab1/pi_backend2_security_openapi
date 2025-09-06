# 4. DTOs y Mapeo de Datos

## 🎯 Objetivos

En esta sección aprenderás a:
- Entender qué son los DTOs y por qué son importantes
- Crear DTOs para cada entidad del sistema
- Implementar métodos de conversión entre entidades y DTOs
- Aplicar mejores prácticas en el mapeo de datos
- Separar la capa de presentación de la capa de persistencia

## 📋 Prerrequisitos

- Entidades JPA creadas (sección anterior)
- Conocimientos básicos de Java
- Comprensión de patrones de diseño

## 🤔 ¿Qué son los DTOs?

### Definición

**DTO (Data Transfer Object)** es un patrón de diseño que se utiliza para transferir datos entre diferentes capas de una aplicación o entre diferentes sistemas.

### ¿Por qué usar DTOs?

#### 1. **Separación de Responsabilidades**
```java
// ❌ Malo: Exponer entidad directamente
@GetMapping
public List<User> getUsers() {
    return userService.findAll(); // Expone password, relaciones, etc.
}

// ✅ Bueno: Usar DTO
@GetMapping
public List<UserDTO> getUsers() {
    return userService.findAll().stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
}
```

#### 2. **Control de Datos Expuestos**
- **Entidad**: Contiene todos los campos, incluyendo sensibles
- **DTO**: Solo contiene campos que deben ser expuestos

#### 3. **Evitar Referencias Circulares**
- Las entidades pueden tener relaciones bidireccionales
- Los DTOs usan IDs en lugar de objetos completos

#### 4. **Versionado de API**
- Cambios en entidades no afectan la API
- Múltiples DTOs para diferentes versiones

#### 5. **Validación Específica**
- Validaciones diferentes para creación vs actualización
- Campos obligatorios según el contexto

## 👤 UserDTO (Usuario)

Crea el archivo `src/main/java/com/example/pib2/models/dtos/UserDTO.java`:

```java
package com.example.pib2.models.dtos;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // Nota: NO incluimos password por seguridad
    // Nota: NO incluimos loans para evitar referencias circulares
}
```

### 🔍 Análisis del UserDTO

#### Campos Incluidos
- **id**: Identificador único
- **username**: Nombre de usuario público
- **email**: Email del usuario

#### Campos Excluidos
- **password**: Información sensible que nunca debe exponerse
- **role**: Podría incluirse según los requerimientos
- **loans**: Lista de préstamos (evita referencias circulares)

#### Ventajas
```java
// La respuesta JSON será limpia:
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
// Sin password, sin relaciones complejas
```

## 📦 ItemDTO (Artículo)

Crea el archivo `src/main/java/com/example/pib2/models/dtos/ItemDTO.java`:

```java
package com.example.pib2.models.dtos;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private int quantity;
    // Nota: NO incluimos loans para evitar referencias circulares
}
```

### 🔍 Análisis del ItemDTO

#### Campos Incluidos
- **id**: Identificador único
- **name**: Nombre del artículo
- **description**: Descripción detallada
- **quantity**: Cantidad disponible

#### Campos Excluidos
- **loans**: Lista de préstamos (evita complejidad)

#### Ejemplo de Uso
```json
{
  "id": 1,
  "name": "Laptop Dell",
  "description": "Laptop para desarrollo",
  "quantity": 5
}
```

## 📋 LoanDTO (Préstamo)

Crea el archivo `src/main/java/com/example/pib2/models/dtos/LoanDTO.java`:

```java
package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private Long itemId;    // ID en lugar del objeto completo
    private Long userId;    // ID en lugar del objeto completo
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
    // Nota: NO incluimos histories para evitar complejidad
}
```

### 🔍 Análisis del LoanDTO

#### Uso de IDs en lugar de Objetos
```java
// ❌ En la entidad (relaciones completas)
private User user;
private Item item;

// ✅ En el DTO (solo IDs)
private Long userId;
private Long itemId;
```

#### Ventajas de usar IDs
1. **Simplicidad**: JSON más limpio
2. **Performance**: No carga objetos relacionados
3. **Flexibilidad**: El cliente decide si necesita más datos
4. **Evita ciclos**: No hay referencias circulares

#### Ejemplo de JSON
```json
{
  "id": 1,
  "itemId": 5,
  "userId": 3,
  "loanDate": "2024-01-15",
  "returnDate": "2024-01-30",
  "returned": false
}
```

## 📊 LoanHistoryDTO (Historial de Préstamos)

Crea el archivo `src/main/java/com/example/pib2/models/dtos/LoanHistoryDTO.java`:

```java
package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoanHistoryDTO {
    private Long id;
    private Long loanId;    // ID del préstamo relacionado
    private LocalDateTime actionDate;
    private String action;  // e.g., "CREATED", "RETURNED"
}
```

### 🔍 Análisis del LoanHistoryDTO

#### Campos de Auditoría
- **actionDate**: Timestamp completo con hora
- **action**: Tipo de acción realizada

#### Ejemplo de JSON
```json
{
  "id": 1,
  "loanId": 5,
  "actionDate": "2024-01-15T10:30:00",
  "action": "CREATED"
}
```

## 🔄 Métodos de Conversión

### Patrón de Mapeo Manual

En cada controlador, implementamos métodos para convertir entre entidades y DTOs:

#### UserController - Métodos de Conversión

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Convertir de Entidad a DTO
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // NO incluimos password ni loans
        return dto;
    }
    
    // Convertir de DTO a Entidad
    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // password y role se manejan por separado
        return user;
    }
    
    // Uso en endpoints
    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User saved = userService.save(user);
        return toDTO(saved);
    }
}
```

#### ItemController - Métodos de Conversión

```java
@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
    
    private Item toEntity(ItemDTO dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        return item;
    }
    
    @GetMapping
    public List<ItemDTO> getAll() {
        return itemService.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
```

#### LoanController - Métodos de Conversión Complejos

```java
@RestController
@RequestMapping("/api/loans")
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    
    private LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        // Convertir objetos a IDs
        dto.setItemId(loan.getItem() != null ? loan.getItem().getId() : null);
        dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());
        return dto;
    }
    
    private Loan toEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setId(dto.getId());
        
        // Convertir IDs a objetos (con validación)
        if (dto.getItemId() != null) {
            Optional<Item> item = itemService.findById(dto.getItemId());
            item.ifPresent(loan::setItem);
        }
        
        if (dto.getUserId() != null) {
            Optional<User> user = userService.findById(dto.getUserId());
            user.ifPresent(loan::setUser);
        }
        
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setReturned(dto.isReturned());
        return loan;
    }
}
```

#### LoanHistoryController - Métodos de Conversión

```java
@RestController
@RequestMapping("/api/loanhistories")
public class LoanHistoryController {
    
    @Autowired
    private LoanHistoryService loanHistoryService;
    @Autowired
    private LoanService loanService;
    
    private LoanHistoryDTO toDTO(LoanHistory history) {
        LoanHistoryDTO dto = new LoanHistoryDTO();
        dto.setId(history.getId());
        dto.setLoanId(history.getLoan() != null ? history.getLoan().getId() : null);
        dto.setActionDate(history.getActionDate());
        dto.setAction(history.getAction());
        return dto;
    }
    
    private LoanHistory toEntity(LoanHistoryDTO dto) {
        LoanHistory history = new LoanHistory();
        history.setId(dto.getId());
        
        if (dto.getLoanId() != null) {
            Optional<Loan> loan = loanService.findById(dto.getLoanId());
            loan.ifPresent(history::setLoan);
        }
        
        history.setActionDate(dto.getActionDate());
        history.setAction(dto.getAction());
        return history;
    }
}
```

## 📁 Estructura de Directorios

Organiza tus DTOs de la siguiente manera:

```
src/main/java/com/example/pib2/
├── models/
│   ├── entities/
│   │   ├── User.java
│   │   ├── Item.java
│   │   ├── Loan.java
│   │   └── LoanHistory.java
│   └── dtos/
│       ├── UserDTO.java
│       ├── ItemDTO.java
│       ├── LoanDTO.java
│       └── LoanHistoryDTO.java
├── controllers/
├── services/
└── repositories/
```

## 🎨 Mejores Prácticas

### 1. Nomenclatura Consistente

✅ **Bueno:**
```java
// Entidad
public class User { }

// DTO correspondiente
public class UserDTO { }

// Métodos de conversión
private UserDTO toDTO(User user) { }
private User toEntity(UserDTO dto) { }
```

### 2. Validación en DTOs

```java
@Data
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
}
```

### 3. DTOs Específicos por Operación

```java
// Para creación (sin ID)
public class CreateUserDTO {
    @NotBlank
    private String username;
    
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
}

// Para actualización (con ID)
public class UpdateUserDTO {
    @NotNull
    private Long id;
    
    private String username;  // Opcional
    private String email;     // Opcional
}

// Para respuesta (sin password)
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
```

### 4. Manejo de Nulos

```java
private LoanDTO toDTO(Loan loan) {
    if (loan == null) {
        return null;
    }
    
    LoanDTO dto = new LoanDTO();
    dto.setId(loan.getId());
    // Verificar nulos antes de acceder a propiedades
    dto.setItemId(loan.getItem() != null ? loan.getItem().getId() : null);
    dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
    return dto;
}
```

### 5. Uso de Optional

```java
private Loan toEntity(LoanDTO dto) {
    Loan loan = new Loan();
    
    // Usar Optional para manejo seguro
    Optional.ofNullable(dto.getItemId())
        .flatMap(itemService::findById)
        .ifPresent(loan::setItem);
    
    Optional.ofNullable(dto.getUserId())
        .flatMap(userService::findById)
        .ifPresent(loan::setUser);
    
    return loan;
}
```

## 🔧 Alternativas de Mapeo

### 1. MapStruct (Recomendado para proyectos grandes)

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "loans", ignore = true)
    UserDTO toDTO(User user);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "loans", ignore = true)
    User toEntity(UserDTO dto);
    
    List<UserDTO> toDTOList(List<User> users);
}
```

### 2. ModelMapper

```java
@Service
public class MappingService {
    
    private final ModelMapper modelMapper;
    
    public MappingService() {
        this.modelMapper = new ModelMapper();
        configureMapper();
    }
    
    private void configureMapper() {
        // Configurar mapeos específicos
        modelMapper.typeMap(User.class, UserDTO.class)
            .addMappings(mapper -> {
                mapper.skip(UserDTO::setPassword);
                mapper.skip(UserDTO::setLoans);
            });
    }
    
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
```

### 3. Mapeo Manual (Usado en nuestro proyecto)

Ventajas:
- **Control total** sobre el mapeo
- **Sin dependencias adicionales**
- **Fácil debugging**
- **Flexibilidad máxima**

Desventajas:
- **Más código** para mantener
- **Propenso a errores** manuales
- **Repetitivo** para entidades simples

## ✅ Verificación del Mapeo

### 1. Prueba de Endpoints

```bash
# Crear un usuario
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com"
  }'

# Respuesta esperada (sin password)
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

### 2. Verificar Referencias

```bash
# Crear un préstamo
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "userId": 1,
    "loanDate": "2024-01-15",
    "returnDate": "2024-01-30",
    "returned": false
  }'

# Respuesta esperada (con IDs, no objetos completos)
{
  "id": 1,
  "itemId": 1,
  "userId": 1,
  "loanDate": "2024-01-15",
  "returnDate": "2024-01-30",
  "returned": false
}
```

## 🚨 Problemas Comunes y Soluciones

### Error: "StackOverflowError" en JSON

**Causa**: Referencias circulares en entidades

**Solución**: Usar DTOs con IDs
```java
// ❌ Malo: Exponer entidad con relaciones
@GetMapping
public List<User> getUsers() {
    return userService.findAll();
}

// ✅ Bueno: Usar DTO
@GetMapping
public List<UserDTO> getUsers() {
    return userService.findAll().stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
}
```

### Error: "NullPointerException" en conversión

**Causa**: No validar nulos

**Solución**: Verificar antes de acceder
```java
private LoanDTO toDTO(Loan loan) {
    if (loan == null) return null;
    
    LoanDTO dto = new LoanDTO();
    dto.setItemId(loan.getItem() != null ? loan.getItem().getId() : null);
    return dto;
}
```

### Error: "Entity not found" al convertir DTO a Entity

**Causa**: ID referenciado no existe

**Solución**: Usar Optional y manejar casos
```java
if (dto.getItemId() != null) {
    Optional<Item> item = itemService.findById(dto.getItemId());
    if (item.isPresent()) {
        loan.setItem(item.get());
    } else {
        throw new EntityNotFoundException("Item not found: " + dto.getItemId());
    }
}
```

### Error: "Validation failed" en DTOs

**Causa**: Datos inválidos en DTO

**Solución**: Agregar validaciones apropiadas
```java
@Data
public class UserDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
```

## 📚 Conceptos Clave Aprendidos

- **DTOs**: Objetos para transferir datos entre capas
- **Separación de responsabilidades**: Entidades vs DTOs
- **Mapeo manual**: Control total sobre la conversión
- **Referencias por ID**: Evitar ciclos y complejidad
- **Validación**: Datos seguros en la capa de presentación
- **Manejo de nulos**: Código robusto y seguro
- **Patrones de conversión**: toDTO() y toEntity()

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Crear repositorios JPA
- Implementar consultas personalizadas
- Usar Spring Data JPA
- Manejar transacciones

---

[**← Anterior: Entidades y Modelos**](03-entidades-modelos.md) | [**Volver al Índice**](README.md) | [**Siguiente: Repositorios →**](05-repositorios-acceso-datos.md)