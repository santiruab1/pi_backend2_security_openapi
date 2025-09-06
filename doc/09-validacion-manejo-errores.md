# 9. Validación y Manejo de Errores

## 🎯 Objetivos

En esta sección aprenderás a:
- Implementar validaciones con Bean Validation (JSR-303)
- Crear manejo centralizado de errores con @ControllerAdvice
- Configurar validaciones personalizadas
- Manejar diferentes tipos de excepciones
- Crear respuestas de error consistentes
- Implementar validaciones de negocio
- Usar @Valid y @Validated
- Crear excepciones personalizadas
- Configurar mensajes de error internacionalizados

## 📋 Prerrequisitos

- Controladores REST implementados
- DTOs y entidades creadas
- Servicios con lógica de negocio
- Conocimientos básicos de excepciones en Java

## 🛡️ Fundamentos de Validación

### ¿Por qué Validar?

1. **Seguridad**: Prevenir ataques de inyección
2. **Integridad**: Mantener datos consistentes
3. **Experiencia de usuario**: Feedback claro y rápido
4. **Robustez**: Aplicación más estable
5. **Mantenimiento**: Código más limpio y predecible

### Tipos de Validación

| Tipo | Descripción | Cuándo Usar | Ejemplo |
|------|-------------|-------------|----------|
| **Sintáctica** | Formato y estructura | Entrada de datos | Email válido, longitud |
| **Semántica** | Reglas de negocio | Lógica específica | Usuario único, stock disponible |
| **Referencial** | Integridad de datos | Relaciones entre entidades | Usuario existe, item disponible |

### Capas de Validación

```
┌─────────────────┐
│   Frontend      │ ← Validación UX (opcional)
├─────────────────┤
│   Controller    │ ← Validación de entrada (@Valid)
├─────────────────┤
│   Service       │ ← Validación de negocio
├─────────────────┤
│   Repository    │ ← Validación de integridad
├─────────────────┤
│   Database      │ ← Constraints de BD
└─────────────────┘
```

## 📦 Dependencias de Validación

En nuestro `pom.xml` ya tenemos Spring Boot Starter Web que incluye:

```xml
<!-- Spring Boot Starter Web incluye validación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Validación explícita (opcional, ya incluida) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Incluye automáticamente:**
- **Hibernate Validator**: Implementación de Bean Validation
- **Jakarta Validation API**: Especificación JSR-303/JSR-380
- **Expression Language**: Para mensajes dinámicos

## 🏷️ Anotaciones de Validación

### Validaciones Básicas

| Anotación | Descripción | Ejemplo |
|-----------|-------------|----------|
| `@NotNull` | No puede ser null | `@NotNull String name` |
| `@NotEmpty` | No null y no vacío | `@NotEmpty List<String> items` |
| `@NotBlank` | No null, no vacío, no solo espacios | `@NotBlank String username` |
| `@Size` | Longitud específica | `@Size(min=3, max=50) String name` |
| `@Min` / `@Max` | Valor mínimo/máximo | `@Min(0) Integer quantity` |
| `@Email` | Formato de email válido | `@Email String email` |
| `@Pattern` | Expresión regular | `@Pattern(regexp="[A-Z]+") String code` |

### Validaciones de Fecha y Tiempo

| Anotación | Descripción | Ejemplo |
|-----------|-------------|----------|
| `@Past` | Fecha en el pasado | `@Past LocalDate birthDate` |
| `@Future` | Fecha en el futuro | `@Future LocalDate returnDate` |
| `@PastOrPresent` | Pasado o presente | `@PastOrPresent LocalDate loanDate` |
| `@FutureOrPresent` | Futuro o presente | `@FutureOrPresent LocalDate dueDate` |

### Validaciones Numéricas

| Anotación | Descripción | Ejemplo |
|-----------|-------------|----------|
| `@Positive` | Número positivo | `@Positive Integer quantity` |
| `@PositiveOrZero` | Positivo o cero | `@PositiveOrZero BigDecimal price` |
| `@Negative` | Número negativo | `@Negative Integer adjustment` |
| `@DecimalMin` | Valor decimal mínimo | `@DecimalMin("0.0") BigDecimal amount` |
| `@DecimalMax` | Valor decimal máximo | `@DecimalMax("100.0") BigDecimal percentage` |
| `@Digits` | Dígitos específicos | `@Digits(integer=3, fraction=2) BigDecimal price` |

## 🏗️ Implementación de Validaciones

### 1. Validaciones en Entidades

Actualiza `src/main/java/com/example/pib2/models/entities/User.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Loan> loans;
}
```

Actualiza `src/main/java/com/example/pib2/models/entities/Item.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 100, message = "Item name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Loan> loans;
}
```

Actualiza `src/main/java/com/example/pib2/models/entities/Loan.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @NotNull(message = "User is required")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    @NotNull(message = "Item is required")
    private Item item;

    @NotNull(message = "Loan date is required")
    @PastOrPresent(message = "Loan date cannot be in the future")
    private LocalDate loanDate;

    @NotNull(message = "Return date is required")
    @Future(message = "Return date must be in the future")
    private LocalDate returnDate;

    @NotNull(message = "Returned status is required")
    private Boolean returned;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanHistory> loanHistories;
}
```

### 2. Validaciones en DTOs

Actualiza `src/main/java/com/example/pib2/models/dtos/UserDTO.java`:

```java
package com.example.pib2.models.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
    
    // Password solo para creación, no para respuestas
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
    private String role;
}
```

Crea `src/main/java/com/example/pib2/models/dtos/UserCreateDTO.java`:

```java
package com.example.pib2.models.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
    private String role = "USER"; // Valor por defecto
}
```

Actualiza `src/main/java/com/example/pib2/models/dtos/ItemDTO.java`:

```java
package com.example.pib2.models.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    
    @NotBlank(message = "Item name cannot be blank")
    @Size(min = 2, max = 100, message = "Item name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;
}
```

Actualiza `src/main/java/com/example/pib2/models/dtos/LoanDTO.java`:

```java
package com.example.pib2.models.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @NotNull(message = "Item ID is required")
    @Positive(message = "Item ID must be positive")
    private Long itemId;
    
    @PastOrPresent(message = "Loan date cannot be in the future")
    private LocalDate loanDate;
    
    @NotNull(message = "Return date is required")
    @Future(message = "Return date must be in the future")
    private LocalDate returnDate;
    
    private Boolean returned;
    
    // Para respuestas, incluir información del usuario e item
    private String username;
    private String itemName;
}
```

## 🚨 Excepciones Personalizadas

### Crear Excepciones de Negocio

Crea `src/main/java/com/example/pib2/exceptions/BusinessException.java`:

```java
package com.example.pib2.exceptions;

public class BusinessException extends RuntimeException {
    private final String errorCode;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
```

Crea `src/main/java/com/example/pib2/exceptions/ResourceNotFoundException.java`:

```java
package com.example.pib2.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
```

Crea `src/main/java/com/example/pib2/exceptions/ValidationException.java`:

```java
package com.example.pib2.exceptions;

import java.util.Map;
import java.util.HashMap;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}
```

## 📋 DTOs de Respuesta de Error

Crea `src/main/java/com/example/pib2/models/dtos/ErrorResponseDTO.java`:

```java
package com.example.pib2.models.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private Map<String, String> validationErrors;
    private List<String> details;
    
    // Constructor para errores simples
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    // Constructor para errores de validación
    public ErrorResponseDTO(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }
}
```

## 🎯 Manejo Centralizado de Errores

Crea `src/main/java/com/example/pib2/exceptions/GlobalExceptionHandler.java`:

```java
package com.example.pib2.exceptions;

import com.example.pib2.models.dtos.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // Manejo de errores de validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input validation failed",
            request.getRequestURI(),
            errors
        );
        
        logger.warn("Validation error: {} for request: {}", errors, request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de violaciones de constraints
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint Violation",
            "Constraint validation failed",
            request.getRequestURI(),
            errors
        );
        
        logger.warn("Constraint violation: {} for request: {}", errors, request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de recursos no encontrados
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        errorResponse.setErrorCode("RESOURCE_NOT_FOUND");
        
        logger.warn("Resource not found: {} for request: {}", ex.getMessage(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    // Manejo de excepciones de negocio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Business Rule Violation",
            ex.getMessage(),
            request.getRequestURI()
        );
        errorResponse.setErrorCode(ex.getErrorCode());
        
        logger.warn("Business exception: {} for request: {}", ex.getMessage(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de excepciones de validación personalizadas
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getRequestURI(),
            ex.getErrors()
        );
        
        logger.warn("Custom validation error: {} for request: {}", ex.getErrors(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de violaciones de integridad de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        String message = "Data integrity violation";
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        
        // Detectar tipos específicos de violación
        if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
            message = "Duplicate entry. This record already exists.";
            errorCode = "DUPLICATE_ENTRY";
        } else if (ex.getMessage().contains("foreign key")) {
            message = "Referenced record does not exist.";
            errorCode = "FOREIGN_KEY_VIOLATION";
        }
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Data Integrity Error",
            message,
            request.getRequestURI()
        );
        errorResponse.setErrorCode(errorCode);
        
        logger.error("Data integrity violation: {} for request: {}", ex.getMessage(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    // Manejo de argumentos de tipo incorrecto
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Parameter Type",
            message,
            request.getRequestURI()
        );
        errorResponse.setErrorCode("INVALID_PARAMETER_TYPE");
        
        logger.warn("Type mismatch: {} for request: {}", message, request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de JSON malformado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON",
            "Invalid JSON format in request body",
            request.getRequestURI()
        );
        errorResponse.setErrorCode("MALFORMED_JSON");
        
        logger.warn("Malformed JSON: {} for request: {}", ex.getMessage(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de endpoints no encontrados
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Endpoint Not Found",
            String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
            request.getRequestURI()
        );
        errorResponse.setErrorCode("ENDPOINT_NOT_FOUND");
        
        logger.warn("Endpoint not found: {} {} for request: {}", 
                ex.getHttpMethod(), ex.getRequestURL(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    // Manejo de IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getRequestURI()
        );
        errorResponse.setErrorCode("INVALID_ARGUMENT");
        
        logger.warn("Illegal argument: {} for request: {}", ex.getMessage(), request.getRequestURI());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Manejo de excepciones generales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getRequestURI()
        );
        errorResponse.setErrorCode("INTERNAL_ERROR");
        
        logger.error("Unexpected error: {} for request: {}", ex.getMessage(), request.getRequestURI(), ex);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

## 🔧 Actualización de Controladores

### UserController con Validaciones

Actualiza `src/main/java/com/example/pib2/controllers/UserController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.exceptions.ResourceNotFoundException;
import com.example.pib2.models.dtos.UserDTO;
import com.example.pib2.models.dtos.UserCreateDTO;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    private User toEntity(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }
    
    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        logger.info("Fetching all users");
        List<UserDTO> users = userService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        logger.info("Fetching user with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        logger.info("Creating new user: {}", userCreateDTO.getUsername());
        
        User user = toEntity(userCreateDTO);
        User savedUser = userService.save(user);
        
        logger.info("User created successfully with id: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.info("Updating user with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Mantener campos que no se deben actualizar
        userDTO.setId(id);
        User userToUpdate = toEntity(userDTO);
        userToUpdate.setPassword(existingUser.getPassword()); // Mantener password existente
        
        User updatedUser = userService.save(userToUpdate);
        
        logger.info("User updated successfully: {}", id);
        return ResponseEntity.ok(toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting user with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userService.deleteById(id);
        
        logger.info("User deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
```

### ItemController con Validaciones

Actualiza `src/main/java/com/example/pib2/controllers/ItemController.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.exceptions.ResourceNotFoundException;
import com.example.pib2.models.dtos.ItemDTO;
import com.example.pib2.models.entities.Item;
import com.example.pib2.servicios.ItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@Validated
public class ItemController {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    
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
    public ResponseEntity<List<ItemDTO>> getAll() {
        logger.info("Fetching all items");
        List<ItemDTO> items = itemService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
        logger.info("Fetching item with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("Item ID must be positive");
        }
        
        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        
        return ResponseEntity.ok(toDTO(item));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemDTO itemDTO) {
        logger.info("Creating new item: {}", itemDTO.getName());
        
        Item item = toEntity(itemDTO);
        Item savedItem = itemService.save(item);
        
        logger.info("Item created successfully with id: {}", savedItem.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDTO) {
        logger.info("Updating item with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("Item ID must be positive");
        }
        
        Item existingItem = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        
        itemDTO.setId(id);
        Item updatedItem = itemService.save(toEntity(itemDTO));
        
        logger.info("Item updated successfully: {}", id);
        return ResponseEntity.ok(toDTO(updatedItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting item with id: {}", id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("Item ID must be positive");
        }
        
        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        
        itemService.deleteById(id);
        
        logger.info("Item deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
```

## 🔍 Validaciones Personalizadas

### Crear Validador Personalizado

Crea `src/main/java/com/example/pib2/validation/UniqueUsername.java`:

```java
package com.example.pib2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Username already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

Crea `src/main/java/com/example/pib2/validation/UniqueUsernameValidator.java`:

```java
package com.example.pib2.validation;

import com.example.pib2.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        // Inicialización si es necesaria
    }
    
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Dejar que @NotNull maneje esto
        }
        
        return !userRepository.existsByUsername(username);
    }
}
```

### Agregar Método al Repositorio

Actualiza `src/main/java/com/example/pib2/repositories/UserRepository.java`:

```java
package com.example.pib2.repositories;

import com.example.pib2.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### Usar Validación Personalizada

Actualiza `UserCreateDTO`:

```java
package com.example.pib2.models.dtos;

import com.example.pib2.validation.UniqueUsername;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @UniqueUsername
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
    private String role = "USER";
}
```

## 🔧 Validaciones en Servicios

### Actualizar UserService

Actualiza `src/main/java/com/example/pib2/servicios/UserService.java`:

```java
package com.example.pib2.servicios;

import com.example.pib2.exceptions.BusinessException;
import com.example.pib2.exceptions.ResourceNotFoundException;
import com.example.pib2.models.entities.User;
import com.example.pib2.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.debug("Finding all users");
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by id: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        logger.debug("Saving user: {}", user.getUsername());
        
        validateUser(user);
        
        // Validaciones de negocio adicionales
        if (user.getId() == null) {
            // Nuevo usuario - verificar unicidad
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new BusinessException("Username already exists: " + user.getUsername(), "DUPLICATE_USERNAME");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BusinessException("Email already exists: " + user.getEmail(), "DUPLICATE_EMAIL");
            }
        } else {
            // Usuario existente - verificar que existe
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", user.getId()));
            
            // Verificar unicidad solo si cambió
            if (!existingUser.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
                throw new BusinessException("Username already exists: " + user.getUsername(), "DUPLICATE_USERNAME");
            }
            if (!existingUser.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
                throw new BusinessException("Email already exists: " + user.getEmail(), "DUPLICATE_EMAIL");
            }
        }
        
        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to save user: " + e.getMessage());
        }
    }
    
    public void deleteById(Long id) {
        logger.debug("Deleting user by id: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Validaciones de negocio antes de eliminar
        if (user.getLoans() != null && !user.getLoans().isEmpty()) {
            long activeLoans = user.getLoans().stream()
                    .filter(loan -> !loan.getReturned())
                    .count();
            if (activeLoans > 0) {
                throw new BusinessException("Cannot delete user with active loans", "USER_HAS_ACTIVE_LOANS");
            }
        }
        
        try {
            userRepository.deleteById(id);
            logger.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to delete user: " + e.getMessage());
        }
    }
    
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        if (user.getRole() == null || (!"USER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
```

## 📝 Configuración de Mensajes

### Mensajes Internacionalizados

Crea `src/main/resources/messages.properties`:

```properties
# Mensajes de validación en español
validation.user.username.required=El nombre de usuario es obligatorio
validation.user.username.size=El nombre de usuario debe tener entre {min} y {max} caracteres
validation.user.username.unique=El nombre de usuario ya existe
validation.user.email.required=El email es obligatorio
validation.user.email.format=El formato del email no es válido
validation.user.email.unique=El email ya está registrado
validation.user.password.required=La contraseña es obligatoria
validation.user.password.size=La contraseña debe tener al menos {min} caracteres
validation.user.role.required=El rol es obligatorio
validation.user.role.pattern=El rol debe ser USER o ADMIN

validation.item.name.required=El nombre del artículo es obligatorio
validation.item.name.size=El nombre debe tener entre {min} y {max} caracteres
validation.item.description.required=La descripción es obligatoria
validation.item.description.size=La descripción no puede exceder {max} caracteres
validation.item.quantity.required=La cantidad es obligatoria
validation.item.quantity.positive=La cantidad no puede ser negativa

validation.loan.user.required=El usuario es obligatorio
validation.loan.item.required=El artículo es obligatorio
validation.loan.returnDate.required=La fecha de devolución es obligatoria
validation.loan.returnDate.future=La fecha de devolución debe ser futura

# Mensajes de error de negocio
business.user.duplicate.username=El nombre de usuario ya existe
business.user.duplicate.email=El email ya está registrado
business.user.active.loans=No se puede eliminar un usuario con préstamos activos
business.item.insufficient.stock=Stock insuficiente para el artículo
business.loan.limit.exceeded=El usuario ha alcanzado el límite de préstamos
business.loan.item.unavailable=El artículo no está disponible

# Mensajes de error del sistema
error.resource.not.found={0} no encontrado con {1}: {2}
error.validation.failed=Error de validación
error.business.rule.violation=Violación de regla de negocio
error.data.integrity.violation=Violación de integridad de datos
error.internal.server=Error interno del servidor
```

Crea `src/main/resources/messages_en.properties`:

```properties
# Validation messages in English
validation.user.username.required=Username is required
validation.user.username.size=Username must be between {min} and {max} characters
validation.user.username.unique=Username already exists
validation.user.email.required=Email is required
validation.user.email.format=Email format is invalid
validation.user.email.unique=Email is already registered
validation.user.password.required=Password is required
validation.user.password.size=Password must be at least {min} characters
validation.user.role.required=Role is required
validation.user.role.pattern=Role must be USER or ADMIN

validation.item.name.required=Item name is required
validation.item.name.size=Name must be between {min} and {max} characters
validation.item.description.required=Description is required
validation.item.description.size=Description cannot exceed {max} characters
validation.item.quantity.required=Quantity is required
validation.item.quantity.positive=Quantity cannot be negative

validation.loan.user.required=User is required
validation.loan.item.required=Item is required
validation.loan.returnDate.required=Return date is required
validation.loan.returnDate.future=Return date must be in the future

# Business error messages
business.user.duplicate.username=Username already exists
business.user.duplicate.email=Email already exists
business.user.active.loans=Cannot delete user with active loans
business.item.insufficient.stock=Insufficient stock for item
business.loan.limit.exceeded=User has reached loan limit
business.loan.item.unavailable=Item is not available

# System error messages
error.resource.not.found={0} not found with {1}: {2}
error.validation.failed=Validation failed
error.business.rule.violation=Business rule violation
error.data.integrity.violation=Data integrity violation
error.internal.server=Internal server error
```

### Configuración de Internacionalización

Crea `src/main/java/com/example/pib2/config/InternationalizationConfig.java`:

```java
package com.example.pib2.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
```

## ✅ Verificación de Validaciones

### 1. Iniciar la Aplicación

```bash
./mvnw spring-boot:run
```

### 2. Probar Validaciones con curl

#### Crear Usuario Válido

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Respuesta esperada (409 Conflict):**
```json
{
  "timestamp": "2024-01-15T10:35:00",
  "status": 409,
  "error": "Data Integrity Error",
  "message": "Duplicate entry. This record already exists.",
  "path": "/api/users",
  "errorCode": "DUPLICATE_ENTRY"
}
```

#### Buscar Usuario Inexistente

```bash
curl -X GET http://localhost:8080/api/users/999
```

**Respuesta esperada (404 Not Found):**
```json
{
  "timestamp": "2024-01-15T10:40:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found with id: '999'",
  "path": "/api/users/999",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```

#### Parámetro Inválido

```bash
curl -X GET http://localhost:8080/api/users/abc
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:45:00",
  "status": 400,
  "error": "Invalid Parameter Type",
  "message": "Invalid value 'abc' for parameter 'id'. Expected type: Long",
  "path": "/api/users/abc",
  "errorCode": "INVALID_PARAMETER_TYPE"
}
```

### 3. Probar con Postman

#### Colección de Pruebas

Crea una colección en Postman con estas pruebas:

1. **Validación de Entrada**
   - POST `/api/users` con datos inválidos
   - Verificar respuesta 400 con errores de validación

2. **Reglas de Negocio**
   - POST `/api/users` con username duplicado
   - Verificar respuesta 409 con error de duplicado

3. **Recursos No Encontrados**
   - GET `/api/users/999`
   - Verificar respuesta 404

4. **Tipos de Parámetros**
   - GET `/api/users/abc`
   - Verificar respuesta 400

## 🧪 Testing de Validaciones

### Tests Unitarios para Validaciones

Crea `src/test/java/com/example/pib2/validation/ValidationTest.java`:

```java
package com.example.pib2.validation;

import com.example.pib2.models.dtos.UserCreateDTO;
import com.example.pib2.models.dtos.ItemDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Valid UserCreateDTO should pass validation")
    void testValidUserCreateDTO() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty(), "Valid user should not have validation errors");
    }
    
    @Test
    @DisplayName("UserCreateDTO with blank username should fail validation")
    void testUserCreateDTOWithBlankUsername() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }
    
    @Test
    @DisplayName("UserCreateDTO with short username should fail validation")
    void testUserCreateDTOWithShortUsername() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("ab");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username") &&
                              v.getMessage().contains("between 3 and 50")));
    }
    
    @Test
    @DisplayName("UserCreateDTO with invalid email should fail validation")
    void testUserCreateDTOWithInvalidEmail() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("testuser");
        user.setEmail("invalid-email");
        user.setPassword("password123");
        user.setRole("USER");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
    
    @Test
    @DisplayName("UserCreateDTO with short password should fail validation")
    void testUserCreateDTOWithShortPassword() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("123");
        user.setRole("USER");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
    
    @Test
    @DisplayName("UserCreateDTO with invalid role should fail validation")
    void testUserCreateDTOWithInvalidRole() {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("INVALID");
        
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }
    
    @Test
    @DisplayName("Valid ItemDTO should pass validation")
    void testValidItemDTO() {
        ItemDTO item = new ItemDTO();
        item.setName("Test Item");
        item.setDescription("Test description");
        item.setQuantity(10);
        
        Set<ConstraintViolation<ItemDTO>> violations = validator.validate(item);
        
        assertTrue(violations.isEmpty(), "Valid item should not have validation errors");
    }
    
    @Test
    @DisplayName("ItemDTO with negative quantity should fail validation")
    void testItemDTOWithNegativeQuantity() {
        ItemDTO item = new ItemDTO();
        item.setName("Test Item");
        item.setDescription("Test description");
        item.setQuantity(-1);
        
        Set<ConstraintViolation<ItemDTO>> violations = validator.validate(item);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }
}
```

### Tests de Integración para Manejo de Errores

Crea `src/test/java/com/example/pib2/controllers/UserControllerValidationTest.java`:

```java
package com.example.pib2.controllers;

import com.example.pib2.models.dtos.UserCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Transactional
class UserControllerValidationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("POST /api/users with valid data should return 201")
    void testCreateUserWithValidData() throws Exception {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }
    
    @Test
    @DisplayName("POST /api/users with invalid data should return 400 with validation errors")
    void testCreateUserWithInvalidData() throws Exception {
        UserCreateDTO user = new UserCreateDTO();
        user.setUsername("ab"); // Too short
        user.setEmail("invalid-email"); // Invalid format
        user.setPassword("123"); // Too short
        user.setRole("INVALID"); // Invalid role
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors", hasKey("username")))
                .andExpect(jsonPath("$.validationErrors", hasKey("email")))
                .andExpect(jsonPath("$.validationErrors", hasKey("password")))
                .andExpect(jsonPath("$.validationErrors", hasKey("role")));
    }
    
    @Test
    @DisplayName("GET /api/users/999 should return 404")
    void testGetNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }
    
    @Test
    @DisplayName("GET /api/users/abc should return 400 for invalid parameter type")
    void testGetUserWithInvalidIdType() throws Exception {
        mockMvc.perform(get("/api/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Parameter Type")));
    }
    
    @Test
    @DisplayName("POST /api/users with malformed JSON should return 400")
    void testCreateUserWithMalformedJson() throws Exception {
        String malformedJson = "{\"username\": \"test\", \"email\": }";
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Malformed JSON")));
    }
}
```

## 🚨 Problemas Comunes y Soluciones

### 1. Validaciones No Funcionan

**Problema:** Las validaciones no se ejecutan

**Soluciones:**
- Verificar que `@Valid` esté presente en el controlador
- Asegurar que `spring-boot-starter-validation` esté en el classpath
- Confirmar que `@Validated` esté en la clase del controlador

```java
// ❌ Incorrecto
@PostMapping
public ResponseEntity<UserDTO> create(@RequestBody UserCreateDTO user) {
    // ...
}

// ✅ Correcto
@PostMapping
public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO user) {
    // ...
}
```

### 2. Mensajes de Error No Personalizados

**Problema:** Mensajes genéricos en lugar de personalizados

**Solución:** Configurar `messages.properties` y usar `message` en anotaciones

```java
// ❌ Mensaje genérico
@NotBlank
private String username;

// ✅ Mensaje personalizado
@NotBlank(message = "Username cannot be blank")
private String username;
```

### 3. Excepciones No Capturadas

**Problema:** Excepciones no manejadas por `@ControllerAdvice`

**Soluciones:**
- Verificar que `@ControllerAdvice` esté en el package correcto
- Asegurar que la excepción tenga el `@ExceptionHandler` correspondiente
- Revisar el orden de los manejadores de excepciones

### 4. Validaciones Personalizadas No Funcionan

**Problema:** Validadores personalizados no se ejecutan

**Soluciones:**
- Verificar que el validador esté anotado con `@Component`
- Asegurar que las dependencias estén inyectadas correctamente
- Confirmar que la anotación esté aplicada correctamente

### 5. Errores de Serialización JSON

**Problema:** Errores al serializar respuestas de error

**Solución:** Configurar Jackson correctamente

```java
// En application.properties
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.serialization.fail-on-empty-beans=false
```

## 📋 Mejores Prácticas

### 1. Estrategia de Validación

```java
// ✅ Validación en capas
@RestController
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO user) {
        // Validación automática por @Valid
        User entity = userService.create(user); // Validación de negocio en servicio
        return ResponseEntity.ok(toDTO(entity));
    }
}

@Service
public class UserService {
    
    public User create(UserCreateDTO dto) {
        // Validaciones de negocio específicas
        validateBusinessRules(dto);
        return userRepository.save(toEntity(dto));
    }
}
```

### 2. Mensajes de Error Consistentes

```java
// ✅ Estructura consistente de errores
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode; // Para identificación programática
    private Map<String, String> validationErrors; // Para errores de validación
}
```

### 3. Logging Apropiado

```java
// ✅ Logging por niveles
@ExceptionHandler(ValidationException.class)
public ResponseEntity<ErrorResponseDTO> handleValidation(ValidationException ex) {
    logger.warn("Validation error: {}", ex.getErrors()); // WARN para errores de usuario
    return ResponseEntity.badRequest().body(createErrorResponse(ex));
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
    logger.error("Unexpected error", ex); // ERROR para errores del sistema
    return ResponseEntity.status(500).body(createErrorResponse(ex));
}
```

### 4. Validaciones Específicas por Contexto

```java
// ✅ DTOs específicos para diferentes operaciones
public class UserCreateDTO {
    @NotBlank
    @UniqueUsername
    private String username;
    
    @NotBlank
    @Size(min = 6)
    private String password; // Requerido para creación
}

public class UserUpdateDTO {
    @NotBlank
    private String username;
    
    // Password opcional para actualización
    @Size(min = 6)
    private String password;
}
```

### 5. Testing Comprehensivo

```java
// ✅ Tests para diferentes escenarios
@Test
void testValidationSuccess() { /* ... */ }

@Test
void testValidationFailure() { /* ... */ }

@Test
void testBusinessRuleViolation() { /* ... */ }

@Test
void testResourceNotFound() { /* ... */ }

@Test
void testUnexpectedError() { /* ... */ }
```

## 🎯 Conceptos Clave Aprendidos

### ✅ Validaciones
- **Bean Validation (JSR-303)**: Estándar para validación declarativa
- **Anotaciones de Validación**: `@NotNull`, `@NotBlank`, `@Size`, `@Email`, etc.
- **Validaciones Personalizadas**: Crear validadores específicos del dominio
- **Grupos de Validación**: Aplicar diferentes validaciones según el contexto
- **Validación en Capas**: Controller, Service, Repository

### ✅ Manejo de Errores
- **@ControllerAdvice**: Manejo centralizado de excepciones
- **@ExceptionHandler**: Manejadores específicos por tipo de excepción
- **ResponseEntity**: Control completo sobre respuestas HTTP
- **Códigos de Estado**: Uso apropiado de códigos HTTP
- **Estructura de Errores**: Respuestas consistentes y útiles

### ✅ Excepciones Personalizadas
- **BusinessException**: Para reglas de negocio
- **ResourceNotFoundException**: Para recursos no encontrados
- **ValidationException**: Para validaciones complejas
- **Jerarquía de Excepciones**: Organización lógica

### ✅ Internacionalización
- **MessageSource**: Configuración de mensajes
- **LocaleResolver**: Resolución de idioma
- **Archivos de Propiedades**: Mensajes por idioma
- **Interpolación**: Parámetros dinámicos en mensajes

## 🚀 Próximos Pasos

En el siguiente tutorial aprenderás sobre:

1. **Seguridad y Autenticación**
   - Spring Security
   - JWT Tokens
   - Autorización por roles
   - Protección de endpoints

2. **Testing Avanzado**
   - Tests de seguridad
   - Tests de performance
   - Tests de integración completos
   - Mocking avanzado

3. **Documentación de API**
   - OpenAPI/Swagger
   - Documentación automática
   - Ejemplos de uso
   - Testing desde documentación

4. **Monitoreo y Observabilidad**
   - Métricas personalizadas
   - Distributed tracing
   - Health checks avanzados
   - Alertas y notificaciones

---

**¡Felicitaciones!** 🎉 Has implementado un sistema robusto de validación y manejo de errores que hace tu aplicación más segura, confiable y fácil de usar.
```

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER"
}
```

#### Crear Usuario Inválido (Validación)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "invalid-email",
    "password": "123"
  }'
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/users",
  "validationErrors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid",
    "password": "Password must be at least 6 characters",
    "role": "Role cannot be blank"
  }
}
```

#### Crear Usuario Duplicado (Regla de Negocio)

```bash
# Crear el mismo usuario otra vez
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Respuesta esperada (409 Conflict):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "El nombre de usuario ya existe",
  "path": "/api/users"
}
```

## 🎯 Resumen

En este tutorial has aprendido:

✅ **Bean Validation** - Validaciones automáticas con anotaciones
✅ **Manejo Centralizado de Errores** - @ControllerAdvice para respuestas consistentes
✅ **Excepciones Personalizadas** - Crear excepciones específicas del dominio
✅ **Validaciones de Negocio** - Implementar reglas complejas en servicios
✅ **Internacionalización** - Mensajes de error en múltiples idiomas
✅ **Testing de Validaciones** - Verificar que las validaciones funcionan correctamente

### 🔗 Próximos Pasos

- **Seguridad**: Autenticación y autorización con Spring Security
- **Testing Avanzado**: Pruebas de integración y unitarias
- **Documentación**: Swagger/OpenAPI para documentar la API
- **Monitoreo**: Métricas y logging avanzado

---

[**← Anterior: Actuator y Monitoreo**](08-actuator-monitoreo.md) | [**Volver al Índice**](README.md)