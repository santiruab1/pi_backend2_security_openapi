# 7. Controladores REST

## 🎯 Objetivos

En esta sección aprenderás a:
- Entender los fundamentos de REST y HTTP
- Crear controladores REST con Spring Boot
- Implementar endpoints CRUD completos
- Manejar códigos de estado HTTP apropiados
- Usar anotaciones de Spring Web
- Convertir entre DTOs y entidades en controladores
- Manejar parámetros de ruta y cuerpo de peticiones
- Implementar respuestas HTTP estructuradas

## 📋 Prerrequisitos

- Servicios implementados
- DTOs creados
- Conocimientos básicos de HTTP
- Comprensión de JSON

## 🌐 Fundamentos de REST

### ¿Qué es REST?

**REST (Representational State Transfer)** es un estilo arquitectónico para servicios web que utiliza HTTP de manera estándar.

### Principios REST

1. **Stateless**: Cada petición contiene toda la información necesaria
2. **Client-Server**: Separación clara entre cliente y servidor
3. **Cacheable**: Las respuestas pueden ser cacheadas
4. **Uniform Interface**: Interfaz uniforme para todas las operaciones
5. **Layered System**: Arquitectura en capas

### Métodos HTTP y Operaciones CRUD

| Método HTTP | Operación CRUD | Propósito | Ejemplo |
|-------------|----------------|-----------|----------|
| `GET` | **Read** | Obtener recursos | `GET /api/users` |
| `POST` | **Create** | Crear nuevo recurso | `POST /api/users` |
| `PUT` | **Update** | Actualizar recurso completo | `PUT /api/users/1` |
| `PATCH` | **Update** | Actualizar recurso parcial | `PATCH /api/users/1` |
| `DELETE` | **Delete** | Eliminar recurso | `DELETE /api/users/1` |

### Códigos de Estado HTTP

#### Códigos de Éxito (2xx)
| Código | Nombre | Cuándo usar |
|--------|--------|-------------|
| `200` | OK | Operación exitosa con datos |
| `201` | Created | Recurso creado exitosamente |
| `204` | No Content | Operación exitosa sin datos |

#### Códigos de Error del Cliente (4xx)
| Código | Nombre | Cuándo usar |
|--------|--------|-------------|
| `400` | Bad Request | Datos inválidos |
| `404` | Not Found | Recurso no encontrado |
| `409` | Conflict | Conflicto de datos |

#### Códigos de Error del Servidor (5xx)
| Código | Nombre | Cuándo usar |
|--------|--------|-------------|
| `500` | Internal Server Error | Error interno |

## 🏗️ Arquitectura de Controladores

### Flujo de una Petición REST

```
┌─────────────────────────────────────────────────────────────┐
│                    PETICIÓN HTTP                           │
│  GET /api/users/1                                          │
│  Content-Type: application/json                            │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 SPRING DISPATCHER                          │
│  - Enrutamiento de peticiones                              │
│  - Deserialización JSON → DTO                             │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   CONTROLADOR                              │
│  @GetMapping("/{id}")                                      │
│  public ResponseEntity<UserDTO> getById(@PathVariable...)  │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVICIO                                │
│  userService.findById(id)                                  │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                  REPOSITORIO                               │
│  userRepository.findById(id)                               │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 BASE DE DATOS                              │
│  SELECT * FROM users WHERE id = ?                          │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 RESPUESTA HTTP                             │
│  HTTP/1.1 200 OK                                          │
│  Content-Type: application/json                            │
│  {"id":1,"username":"john","email":"john@example.com"}    │
└─────────────────────────────────────────────────────────────┘
```

### Responsabilidades del Controlador

1. **Recibir peticiones HTTP**: Endpoints y parámetros
2. **Validar entrada**: Datos de entrada válidos
3. **Convertir DTOs**: Entre DTOs y entidades
4. **Llamar servicios**: Delegar lógica de negocio
5. **Manejar respuestas**: Códigos de estado apropiados
6. **Serializar salida**: Entidades a JSON

## 🔧 Anotaciones de Spring Web

### Anotaciones de Clase

#### @RestController
```java
@RestController  // Combina @Controller + @ResponseBody
public class UserController {
    // Todos los métodos devuelven JSON automáticamente
}
```

#### @RequestMapping
```java
@RestController
@RequestMapping("/api/users")  // Prefijo para todos los endpoints
public class UserController {
    // Todos los endpoints empiezan con /api/users
}
```

### Anotaciones de Método

#### Métodos HTTP
```java
@GetMapping          // GET requests
@PostMapping         // POST requests
@PutMapping          // PUT requests
@PatchMapping        // PATCH requests
@DeleteMapping       // DELETE requests
```

#### Parámetros
```java
@PathVariable        // Variables en la URL: /users/{id}
@RequestBody         // Cuerpo de la petición (JSON)
@RequestParam        // Parámetros de consulta: ?name=value
@RequestHeader       // Headers HTTP
```

## 👤 UserController (Controlador de Usuarios)

Crea el archivo `src/main/java/com/example/pib2/controllers/UserController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.models.dtos.UserDTO;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    // Método para convertir Entity a DTO
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // NO incluimos password por seguridad
        return dto;
    }

    // Método para convertir DTO a Entity
    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // password y role se manejan por separado
        return user;
    }

    // GET /api/users - Obtener todos los usuarios
    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/users/{id} - Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users - Crear nuevo usuario
    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User saved = userService.save(user);
        return toDTO(saved);
    }

    // PUT /api/users/{id} - Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.findById(id)
                .map(existing -> {
                    userDTO.setId(id);  // Asegurar que el ID coincida
                    User updated = toEntity(userDTO);
                    User saved = userService.save(updated);
                    return ResponseEntity.ok(toDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/{id} - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();  // 204 No Content
        }
        return ResponseEntity.notFound().build();  // 404 Not Found
    }
}
```

### 🔍 Análisis del UserController

#### Estructura del Controlador

```java
@RestController  // 1. Marca como controlador REST
@RequestMapping("/api/users")  // 2. Prefijo base para todos los endpoints
public class UserController {
    
    @Autowired  // 3. Inyección del servicio
    private UserService userService;
    
    // 4. Métodos de conversión
    private UserDTO toDTO(User user) { ... }
    private User toEntity(UserDTO dto) { ... }
    
    // 5. Endpoints CRUD
    @GetMapping
    public List<UserDTO> getAll() { ... }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) { ... }
    
    // ... más endpoints
}
```

#### Endpoints Implementados

| Endpoint | Método | Descripción | Respuesta |
|----------|--------|-------------|----------|
| `GET /api/users` | `getAll()` | Lista todos los usuarios | `200 OK` + Lista |
| `GET /api/users/{id}` | `getById()` | Usuario específico | `200 OK` o `404 Not Found` |
| `POST /api/users` | `create()` | Crear usuario | `200 OK` + Usuario creado |
| `PUT /api/users/{id}` | `update()` | Actualizar usuario | `200 OK` o `404 Not Found` |
| `DELETE /api/users/{id}` | `delete()` | Eliminar usuario | `204 No Content` o `404 Not Found` |

#### Uso de ResponseEntity

```java
// ✅ Bueno: Control explícito del código de estado
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    return userService.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))        // 200 OK
            .orElse(ResponseEntity.notFound().build());         // 404 Not Found
}

// ✅ También bueno: Retorno directo para casos simples
@GetMapping
public List<UserDTO> getAll() {
    return userService.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    // Spring automáticamente devuelve 200 OK
}
```

#### Conversión DTO ↔ Entity

```java
// Entity → DTO (para respuestas)
private UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    // ⚠️ NO incluir password por seguridad
    return dto;
}

// DTO → Entity (para peticiones)
private User toEntity(UserDTO dto) {
    User user = new User();
    user.setId(dto.getId());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    // ⚠️ password se maneja por separado
    return user;
}
```

## 📦 ItemController (Controlador de Artículos)

Crea el archivo `src/main/java/com/example/pib2/controllers/ItemController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.models.dtos.ItemDTO;
import com.example.pib2.models.entities.Item;
import com.example.pib2.servicios.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    // Conversión Entity → DTO
    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    // Conversión DTO → Entity
    private Item toEntity(ItemDTO dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        return item;
    }

    // GET /api/items - Obtener todos los artículos
    @GetMapping
    public List<ItemDTO> getAll() {
        return itemService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/items/{id} - Obtener artículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> ResponseEntity.ok(toDTO(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/items - Crear nuevo artículo
    @PostMapping
    public ItemDTO create(@RequestBody ItemDTO itemDTO) {
        Item item = toEntity(itemDTO);
        Item saved = itemService.save(item);
        return toDTO(saved);
    }

    // PUT /api/items/{id} - Actualizar artículo
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        return itemService.findById(id)
                .map(existing -> {
                    itemDTO.setId(id);
                    Item updated = toEntity(itemDTO);
                    Item saved = itemService.save(updated);
                    return ResponseEntity.ok(toDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/items/{id} - Eliminar artículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (itemService.findById(id).isPresent()) {
            itemService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### 🔍 Características del ItemController

#### Endpoints de Inventario

| Endpoint | Funcionalidad | Ejemplo de Uso |
|----------|---------------|----------------|
| `GET /api/items` | Listar inventario | Ver todos los artículos disponibles |
| `GET /api/items/1` | Ver artículo específico | Detalles de un producto |
| `POST /api/items` | Agregar al inventario | Nuevo producto en stock |
| `PUT /api/items/1` | Actualizar inventario | Cambiar cantidad o descripción |
| `DELETE /api/items/1` | Remover del inventario | Descontinuar producto |

#### Ejemplo de Peticiones

```bash
# Crear nuevo artículo
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell",
    "description": "Laptop para desarrollo",
    "quantity": 5
  }'

# Actualizar cantidad
curl -X PUT http://localhost:8080/api/items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell",
    "description": "Laptop para desarrollo",
    "quantity": 3
  }'
```

## 📋 LoanController (Controlador de Préstamos)

Crea el archivo `src/main/java/com/example/pib2/controllers/LoanController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.models.dtos.LoanDTO;
import com.example.pib2.models.entities.Loan;
import com.example.pib2.models.entities.Item;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.LoanService;
import com.example.pib2.servicios.ItemService;
import com.example.pib2.servicios.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    // Conversión Entity → DTO (con relaciones)
    private LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        // Convertir objetos relacionados a IDs
        dto.setItemId(loan.getItem() != null ? loan.getItem().getId() : null);
        dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());
        return dto;
    }

    // Conversión DTO → Entity (con validación de relaciones)
    private Loan toEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setId(dto.getId());
        
        // Resolver relaciones por ID
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

    // GET /api/loans - Obtener todos los préstamos
    @GetMapping
    public List<LoanDTO> getAll() {
        return loanService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/loans/{id} - Obtener préstamo por ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getById(@PathVariable Long id) {
        return loanService.findById(id)
                .map(loan -> ResponseEntity.ok(toDTO(loan)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/loans - Crear nuevo préstamo
    @PostMapping
    public LoanDTO create(@RequestBody LoanDTO loanDTO) {
        Loan loan = toEntity(loanDTO);
        Loan saved = loanService.save(loan);
        return toDTO(saved);
    }

    // PUT /api/loans/{id} - Actualizar préstamo
    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> update(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        return loanService.findById(id)
                .map(existing -> {
                    loanDTO.setId(id);
                    Loan updated = toEntity(loanDTO);
                    Loan saved = loanService.save(updated);
                    return ResponseEntity.ok(toDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/loans/{id} - Eliminar préstamo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanService.findById(id).isPresent()) {
            loanService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### 🔍 Características del LoanController

#### Manejo de Relaciones

El `LoanController` es más complejo porque maneja relaciones entre entidades:

```java
// DTO usa IDs para las relaciones
public class LoanDTO {
    private Long id;
    private Long itemId;    // ← ID del artículo
    private Long userId;    // ← ID del usuario
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}

// Entity usa objetos completos
public class Loan {
    private Long id;
    private Item item;      // ← Objeto completo
    private User user;      // ← Objeto completo
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}
```

#### Conversión con Validación

```java
private Loan toEntity(LoanDTO dto) {
    Loan loan = new Loan();
    loan.setId(dto.getId());
    
    // ✅ Validar que el artículo existe
    if (dto.getItemId() != null) {
        Optional<Item> item = itemService.findById(dto.getItemId());
        if (item.isPresent()) {
            loan.setItem(item.get());
        } else {
            // Podrías lanzar una excepción aquí
            throw new EntityNotFoundException("Item not found: " + dto.getItemId());
        }
    }
    
    // ✅ Validar que el usuario existe
    if (dto.getUserId() != null) {
        Optional<User> user = userService.findById(dto.getUserId());
        if (user.isPresent()) {
            loan.setUser(user.get());
        } else {
            throw new EntityNotFoundException("User not found: " + dto.getUserId());
        }
    }
    
    return loan;
}
```

#### Ejemplo de Peticiones

```bash
# Crear préstamo
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "userId": 1,
    "loanDate": "2024-01-15",
    "returnDate": "2024-01-22",
    "returned": false
  }'

# Marcar como devuelto
curl -X PUT http://localhost:8080/api/loans/1 \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "userId": 1,
    "loanDate": "2024-01-15",
    "returnDate": "2024-01-20",
    "returned": true
  }'
```

## 📊 LoanHistoryController (Controlador de Historial)

Crea el archivo `src/main/java/com/example/pib2/controllers/LoanHistoryController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.models.dtos.LoanHistoryDTO;
import com.example.pib2.models.entities.LoanHistory;
import com.example.pib2.models.entities.Loan;
import com.example.pib2.servicios.LoanHistoryService;
import com.example.pib2.servicios.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loanhistories")
public class LoanHistoryController {
    @Autowired
    private LoanHistoryService loanHistoryService;
    @Autowired
    private LoanService loanService;

    // Conversión Entity → DTO
    private LoanHistoryDTO toDTO(LoanHistory history) {
        LoanHistoryDTO dto = new LoanHistoryDTO();
        dto.setId(history.getId());
        dto.setLoanId(history.getLoan() != null ? history.getLoan().getId() : null);
        dto.setActionDate(history.getActionDate());
        dto.setAction(history.getAction());
        return dto;
    }

    // Conversión DTO → Entity
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

    // GET /api/loanhistories - Obtener todo el historial
    @GetMapping
    public List<LoanHistoryDTO> getAll() {
        return loanHistoryService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/loanhistories/{id} - Obtener historial por ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanHistoryDTO> getById(@PathVariable Long id) {
        return loanHistoryService.findById(id)
                .map(history -> ResponseEntity.ok(toDTO(history)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/loanhistories - Crear registro de historial
    @PostMapping
    public LoanHistoryDTO create(@RequestBody LoanHistoryDTO loanHistoryDTO) {
        LoanHistory history = toEntity(loanHistoryDTO);
        LoanHistory saved = loanHistoryService.save(history);
        return toDTO(saved);
    }

    // PUT /api/loanhistories/{id} - Actualizar historial
    @PutMapping("/{id}")
    public ResponseEntity<LoanHistoryDTO> update(@PathVariable Long id, @RequestBody LoanHistoryDTO loanHistoryDTO) {
        return loanHistoryService.findById(id)
                .map(existing -> {
                    loanHistoryDTO.setId(id);
                    LoanHistory updated = toEntity(loanHistoryDTO);
                    LoanHistory saved = loanHistoryService.save(updated);
                    return ResponseEntity.ok(toDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/loanhistories/{id} - Eliminar registro de historial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanHistoryService.findById(id).isPresent()) {
            loanHistoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### 🔍 Características del LoanHistoryController

#### Propósito de Auditoría

El `LoanHistoryController` maneja el historial de acciones sobre préstamos:

```java
// Ejemplo de registros de historial
{
  "id": 1,
  "loanId": 5,
  "actionDate": "2024-01-15T10:30:00",
  "action": "PRESTAMO_CREADO"
}

{
  "id": 2,
  "loanId": 5,
  "actionDate": "2024-01-20T14:15:00",
  "action": "PRESTAMO_DEVUELTO"
}
```

#### Tipos de Acciones Comunes

| Acción | Descripción | Cuándo se crea |
|--------|-------------|----------------|
| `PRESTAMO_CREADO` | Préstamo inicial | Al crear préstamo |
| `PRESTAMO_MODIFICADO` | Cambio en fechas | Al actualizar préstamo |
| `PRESTAMO_DEVUELTO` | Devolución | Al marcar como devuelto |
| `PRESTAMO_VENCIDO` | Préstamo vencido | Proceso automático |

## 📁 Estructura de Directorios

Organiza tus controladores:

```
src/main/java/com/example/pib2/
├── controllers/
│   ├── UserController.java
│   ├── ItemController.java
│   ├── LoanController.java
│   └── LoanHistoryController.java
├── servicios/
├── repositories/
├── models/
│   ├── entities/
│   └── dtos/
└── Pib2Application.java
```

## 🔧 Configuración Avanzada

### CORS (Cross-Origin Resource Sharing)

Para permitir peticiones desde el frontend:

```java
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")  // React app
public class UserController {
    // endpoints...
}
```

### Configuración Global de CORS

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Content Negotiation

```java
@GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    // Spring automáticamente serializa según el Accept header
    return userService.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))
            .orElse(ResponseEntity.notFound().build());
}
```

## ✅ Verificación de Controladores

### 1. Compilar el Proyecto

```bash
./mvnw clean compile
```

### 2. Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

### 3. Verificar Endpoints

Usa el script de pruebas incluido en el proyecto:

```powershell
# En Windows PowerShell
.\test-endpoints.ps1
```

### 4. Pruebas Manuales con curl

```bash
# Listar usuarios
curl http://localhost:8080/api/users

# Crear usuario
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com"}'

# Obtener usuario por ID
curl http://localhost:8080/api/users/1

# Actualizar usuario
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"username":"updated","email":"updated@example.com"}'

# Eliminar usuario
curl -X DELETE http://localhost:8080/api/users/1
```

### 5. Verificar Respuestas

#### Respuesta Exitosa (200 OK)
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com"
}
```

#### Recurso No Encontrado (404 Not Found)
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/users/999"
}
```

## 🚨 Problemas Comunes y Soluciones

### Error: "404 Not Found" en todos los endpoints

**Causa**: Controlador no está siendo detectado por Spring

**Solución**: Verificar que el controlador esté en el package correcto
```java
// ✅ Correcto
package com.example.pib2.controllers;

// ❌ Incorrecto
package com.other.package.controllers;
```

### Error: "405 Method Not Allowed"

**Causa**: Método HTTP incorrecto

**Solución**: Verificar anotaciones de mapeo
```java
// ✅ Correcto
@PostMapping  // Para crear
public UserDTO create(@RequestBody UserDTO userDTO) { }

// ❌ Incorrecto
@GetMapping   // GET no es para crear
public UserDTO create(@RequestBody UserDTO userDTO) { }
```

### Error: "400 Bad Request" con JSON

**Causa**: JSON malformado o campos faltantes

**Solución**: Verificar estructura del JSON
```json
// ✅ Correcto
{
  "username": "john",
  "email": "john@example.com"
}

// ❌ Incorrecto (coma extra)
{
  "username": "john",
  "email": "john@example.com",
}
```

### Error: "500 Internal Server Error"

**Causa**: Excepción no manejada en el código

**Solución**: Revisar logs y agregar manejo de errores
```java
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    try {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
        // Log del error
        log.error("Error getting user by id: {}", id, e);
        return ResponseEntity.internalServerError().build();
    }
}
```

### Error: "Circular Reference" en JSON

**Causa**: Relaciones bidireccionales en entidades

**Solución**: Usar DTOs (ya implementado) o anotaciones Jackson
```java
// ✅ Solución con DTOs (recomendado)
private UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    // NO incluir loans para evitar referencias circulares
    return dto;
}

// ✅ Alternativa con anotaciones Jackson
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    @JsonIgnore  // Ignorar en serialización
    private List<Loan> loans;
}
```

## 🎨 Mejores Prácticas

### 1. Nomenclatura de Endpoints

✅ **Bueno:**
```
GET    /api/users          # Listar usuarios
GET    /api/users/1        # Usuario específico
POST   /api/users          # Crear usuario
PUT    /api/users/1        # Actualizar usuario
DELETE /api/users/1        # Eliminar usuario
```

❌ **Malo:**
```
GET    /api/getUsers       # No usar verbos en URLs
POST   /api/createUser     # No usar verbos en URLs
GET    /api/user/1         # Usar plural
```

### 2. Códigos de Estado Apropiados

```java
// ✅ Bueno: Códigos específicos
@PostMapping
public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
    User saved = userService.save(toEntity(userDTO));
    return ResponseEntity.status(HttpStatus.CREATED)  // 201 Created
            .body(toDTO(saved));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();  // 204 No Content
}
```

### 3. Validación de Entrada

```java
@PostMapping
public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) {
    // @Valid activa validaciones automáticas
    User saved = userService.save(toEntity(userDTO));
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(toDTO(saved));
}
```

### 4. Manejo Consistente de Errores

```java
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    if (id <= 0) {
        return ResponseEntity.badRequest().build();  // 400 Bad Request
    }
    
    return userService.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))  // 200 OK
            .orElse(ResponseEntity.notFound().build());   // 404 Not Found
}
```

### 5. Documentación con Comentarios

```java
/**
 * Obtiene un usuario por su ID
 * 
 * @param id ID del usuario a buscar
 * @return ResponseEntity con el usuario encontrado o 404 si no existe
 */
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    return userService.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))
            .orElse(ResponseEntity.notFound().build());
}
```

### 6. Logging

```java
@RestController
@RequestMapping("/api/users")
@Slf4j  // Lombok para logging
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getUsername());
        
        try {
            User saved = userService.save(toEntity(userDTO));
            log.info("User created successfully with ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toDTO(saved));
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

## 📚 Conceptos Clave Aprendidos

- **REST**: Arquitectura estándar para APIs web
- **HTTP Methods**: GET, POST, PUT, DELETE para operaciones CRUD
- **Status Codes**: Códigos apropiados para diferentes situaciones
- **@RestController**: Controladores que devuelven JSON automáticamente
- **@RequestMapping**: Mapeo de URLs a métodos
- **@PathVariable**: Variables en la URL
- **@RequestBody**: Datos JSON en el cuerpo de la petición
- **ResponseEntity**: Control explícito de respuestas HTTP
- **DTO Conversion**: Transformación entre DTOs y entidades
- **Error Handling**: Manejo apropiado de errores y excepciones

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Implementar validación de datos
- Crear manejo global de errores
- Configurar Spring Boot Actuator
- Crear pruebas automatizadas
- Documentar APIs con Swagger

---

[**← Anterior: Servicios y Lógica de Negocio**](06-servicios-logica-negocio.md) | [**Volver al Índice**](README.md) | [**Siguiente: Actuator y Monitoreo →**](08-actuator-monitoreo.md)