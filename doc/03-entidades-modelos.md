# 3. Entidades y Modelos de Datos

## 🎯 Objetivos

En esta sección aprenderás a:
- Diseñar el modelo de datos del sistema de inventario
- Crear entidades JPA con anotaciones
- Definir relaciones entre entidades
- Usar Lombok para reducir código boilerplate
- Manejar referencias circulares con Jackson
- Implementar buenas prácticas en el diseño de entidades

## 📋 Prerrequisitos

- Proyecto Spring Boot configurado
- Base de datos PostgreSQL configurada
- Conocimientos básicos de JPA/Hibernate
- Comprensión de relaciones de base de datos

## 🏗️ Diseño del Modelo de Datos

### Diagrama de Entidades

Nuestro sistema de inventario tendrá las siguientes entidades:

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │       │    Item     │       │    Loan     │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │       │ id (PK)     │
│ username    │       │ name        │       │ item_id(FK) │
│ email       │       │ description │       │ user_id(FK) │
│ password    │       │ quantity    │       │ loanDate    │
│ role        │       │             │       │ returnDate  │
└─────────────┘       └─────────────┘       │ returned    │
       │                       │             └─────────────┘
       │                       │                     │
       │                       │                     │
       └───────────────────────┼─────────────────────┘
                               │
                               │
                    ┌─────────────┐
                    │ LoanHistory │
                    ├─────────────┤
                    │ id (PK)     │
                    │ loan_id(FK) │
                    │ actionDate  │
                    │ action      │
                    └─────────────┘
```

### Relaciones

- **User ↔ Loan**: Un usuario puede tener múltiples préstamos (1:N)
- **Item ↔ Loan**: Un item puede estar en múltiples préstamos (1:N)
- **Loan ↔ LoanHistory**: Un préstamo puede tener múltiples registros de historial (1:N)

## 👤 Entidad User (Usuario)

Crea el archivo `src/main/java/com/example/pib2/models/entities/User.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Loan> loans;
}
```

### 🔍 Explicación de la Entidad User

#### Anotaciones de Clase

```java
@Entity
```
- **Propósito**: Marca la clase como una entidad JPA
- **Resultado**: Hibernate creará una tabla en la base de datos

```java
@Data
```
- **Propósito**: Anotación de Lombok que genera automáticamente:
  - Getters y setters para todos los campos
  - Método `toString()`
  - Métodos `equals()` y `hashCode()`
  - Constructor sin argumentos

```java
@Table(name = "users")
```
- **Propósito**: Especifica el nombre de la tabla en la base de datos
- **Razón**: "user" es una palabra reservada en PostgreSQL

#### Anotaciones de Campo

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- **@Id**: Marca el campo como clave primaria
- **@GeneratedValue**: Especifica cómo se genera el valor
- **IDENTITY**: Usa auto-incremento de la base de datos

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference
private List<Loan> loans;
```
- **@OneToMany**: Relación uno a muchos
- **mappedBy**: Indica que la relación es mapeada por el campo "user" en la entidad Loan
- **cascade = CascadeType.ALL**: Las operaciones se propagan a las entidades relacionadas
- **orphanRemoval = true**: Elimina automáticamente los préstamos huérfanos
- **@JsonManagedReference**: Evita referencias circulares en JSON

## 📦 Entidad Item (Artículo)

Crea el archivo `src/main/java/com/example/pib2/models/entities/Item.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int quantity;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Loan> loans;
}
```

### 🔍 Explicación de la Entidad Item

#### Campos de Negocio

```java
private String name;
private String description;
private int quantity;
```
- **name**: Nombre del artículo
- **description**: Descripción detallada
- **quantity**: Cantidad disponible en inventario

#### Relación con Loan

```java
@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference
private List<Loan> loans;
```
- Un item puede estar en múltiples préstamos
- Si se elimina un item, se eliminan todos sus préstamos
- Evita referencias circulares en la serialización JSON

## 📋 Entidad Loan (Préstamo)

Crea el archivo `src/main/java/com/example/pib2/models/entities/Loan.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LoanHistory> histories;
}
```

### 🔍 Explicación de la Entidad Loan

#### Relaciones Many-to-One

```java
@ManyToOne
@JoinColumn(name = "item_id")
@JsonBackReference
private Item item;
```
- **@ManyToOne**: Muchos préstamos pueden referenciar un item
- **@JoinColumn**: Especifica el nombre de la columna de clave foránea
- **@JsonBackReference**: Lado "back" de la referencia bidireccional

```java
@ManyToOne
@JoinColumn(name = "user_id")
@JsonBackReference
private User user;
```
- Similar configuración para la relación con User

#### Campos de Negocio

```java
private LocalDate loanDate;
private LocalDate returnDate;
private boolean returned;
```
- **LocalDate**: Tipo de Java 8+ para fechas (sin hora)
- **loanDate**: Fecha en que se realizó el préstamo
- **returnDate**: Fecha programada de devolución
- **returned**: Estado booleano del préstamo

#### Relación con LoanHistory

```java
@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference
private List<LoanHistory> histories;
```
- Un préstamo puede tener múltiples registros de historial
- Cascada completa y eliminación de huérfanos

## 📊 Entidad LoanHistory (Historial de Préstamos)

Crea el archivo `src/main/java/com/example/pib2/models/entities/LoanHistory.java`:

```java
package com.example.pib2.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class LoanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    @JsonBackReference
    private Loan loan;

    private LocalDateTime actionDate;
    private String action; // e.g., "CREATED", "RETURNED"
}
```

### 🔍 Explicación de la Entidad LoanHistory

#### Campos de Auditoría

```java
private LocalDateTime actionDate;
private String action;
```
- **LocalDateTime**: Incluye fecha y hora exacta
- **action**: Tipo de acción realizada (ej: "CREATED", "RETURNED", "EXTENDED")

#### Relación con Loan

```java
@ManyToOne
@JoinColumn(name = "loan_id")
@JsonBackReference
private Loan loan;
```
- Múltiples registros de historial por préstamo
- Referencia hacia atrás para evitar ciclos JSON

## 🔄 Manejo de Referencias Circulares

### Problema de Referencias Circulares

Sin las anotaciones de Jackson, tendríamos:
```
User → Loan → User → Loan → ... (infinito)
```

### Solución con Jackson

```java
// En el lado "padre" (User, Item)
@JsonManagedReference
private List<Loan> loans;

// En el lado "hijo" (Loan)
@JsonBackReference
private User user;
@JsonBackReference
private Item item;
```

**Resultado en JSON:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "loans": [
    {
      "id": 1,
      "loanDate": "2024-01-15",
      "returned": false
      // user e item no aparecen aquí
    }
  ]
}
```

## 📁 Estructura de Directorios

Organiza tus entidades de la siguiente manera:

```
src/main/java/com/example/pib2/
├── models/
│   └── entities/
│       ├── User.java
│       ├── Item.java
│       ├── Loan.java
│       └── LoanHistory.java
├── controllers/
├── services/
├── repositories/
└── Pib2Application.java
```

## ✅ Verificación de las Entidades

### 1. Compilar el Proyecto

```bash
./mvnw clean compile
```

### 2. Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

### 3. Verificar Creación de Tablas

En los logs deberías ver:

```sql
Hibernate: create table users (
    id bigserial not null,
    email varchar(255),
    password varchar(255),
    role varchar(255),
    username varchar(255),
    primary key (id)
)

Hibernate: create table item (
    id bigserial not null,
    description varchar(255),
    name varchar(255),
    quantity integer not null,
    primary key (id)
)

Hibernate: create table loan (
    id bigserial not null,
    loan_date date,
    return_date date,
    returned boolean not null,
    item_id bigint,
    user_id bigint,
    primary key (id)
)

Hibernate: create table loan_history (
    id bigserial not null,
    action varchar(255),
    action_date timestamp(6),
    loan_id bigint,
    primary key (id)
)
```

## 🎨 Mejores Prácticas

### 1. Nomenclatura de Entidades

✅ **Bueno:**
```java
@Entity
@Table(name = "users")  // Plural, snake_case
public class User {     // Singular, PascalCase
```

❌ **Malo:**
```java
@Entity
public class user {     // Minúscula
```

### 2. Uso de Lombok

✅ **Bueno:**
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // campos
}
```

❌ **Malo:**
```java
@Entity
public class User {
    // Escribir manualmente todos los getters/setters
}
```

### 3. Relaciones Bidireccionales

✅ **Bueno:**
```java
// Lado padre
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference
private List<Loan> loans;

// Lado hijo
@ManyToOne
@JoinColumn(name = "user_id")
@JsonBackReference
private User user;
```

### 4. Tipos de Datos

✅ **Bueno:**
```java
private LocalDate loanDate;      // Para fechas
private LocalDateTime actionDate; // Para fecha y hora
private BigDecimal price;        // Para dinero
```

❌ **Malo:**
```java
private Date loanDate;           // Deprecated
private float price;             // Impreciso para dinero
```

## 🚨 Problemas Comunes y Soluciones

### Error: "Table 'user' doesn't exist"

**Causa**: "user" es palabra reservada en PostgreSQL

**Solución**:
```java
@Entity
@Table(name = "users")  // Usar nombre diferente
public class User {
```

### Error: "StackOverflowError" en JSON

**Causa**: Referencias circulares

**Solución**:
```java
@JsonManagedReference  // En el lado padre
@JsonBackReference     // En el lado hijo
```

### Error: "LazyInitializationException"

**Causa**: Acceso a relaciones lazy fuera de transacción

**Solución**:
```java
@OneToMany(fetch = FetchType.EAGER)  // Solo si es necesario
// O usar @Transactional en el servicio
```

### Error: "Detached entity passed to persist"

**Causa**: Intentar guardar entidad con ID ya asignado

**Solución**:
```java
// Usar merge() en lugar de save() para entidades existentes
entityManager.merge(entity);
```

## 🔧 Configuraciones Adicionales

### Auditoría Automática

Para agregar campos de auditoría automática:

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    // campos existentes...
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```

Y en la clase principal:
```java
@SpringBootApplication
@EnableJpaAuditing
public class Pib2Application {
    // ...
}
```

### Validaciones

Agregar validaciones a los campos:

```java
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}
```

## 📚 Conceptos Clave Aprendidos

- **Entidades JPA**: Clases que representan tablas de base de datos
- **Anotaciones de mapeo**: @Entity, @Table, @Id, @GeneratedValue
- **Relaciones**: @OneToMany, @ManyToOne, @JoinColumn
- **Lombok**: Reducción de código boilerplate
- **Jackson**: Manejo de referencias circulares en JSON
- **Cascade**: Propagación de operaciones a entidades relacionadas
- **Orphan removal**: Eliminación automática de entidades huérfanas

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Crear DTOs (Data Transfer Objects)
- Implementar mapeo entre entidades y DTOs
- Separar la capa de presentación de la capa de datos
- Validar datos de entrada

---

[**← Anterior: Configuración de Base de Datos**](02-configuracion-database.md) | [**Volver al Índice**](README.md) | [**Siguiente: DTOs y Mapeo →**](04-dtos-mapeo.md)