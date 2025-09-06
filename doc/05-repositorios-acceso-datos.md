# 5. Repositorios y Acceso a Datos

## 🎯 Objetivos

En esta sección aprenderás a:
- Entender qué es Spring Data JPA
- Crear repositorios con JpaRepository
- Implementar operaciones CRUD automáticas
- Crear consultas derivadas de nombres de métodos
- Aplicar el patrón Repository
- Configurar consultas personalizadas con @Query

## 📋 Prerrequisitos

- Entidades JPA creadas
- Configuración de base de datos completada
- Conocimientos básicos de JPA/Hibernate
- Comprensión de interfaces en Java

## 🗄️ ¿Qué es Spring Data JPA?

**Spring Data JPA** es una abstracción que simplifica el acceso a datos proporcionando:

### Características Principales

- **Implementación automática**: No necesitas escribir código de implementación
- **Métodos CRUD predefinidos**: Operaciones básicas ya incluidas
- **Consultas derivadas**: Genera consultas basadas en nombres de métodos
- **Soporte para paginación**: Manejo automático de grandes conjuntos de datos
- **Auditoría**: Seguimiento automático de cambios
- **Transacciones**: Gestión automática de transacciones

### Ventajas de JpaRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // ¡No necesitas implementar nada!
    // Spring Data JPA genera automáticamente:
    // - findAll()
    // - findById(Long id)
    // - save(User user)
    // - deleteById(Long id)
    // - count()
    // - existsById(Long id)
    // Y muchos más...
}
```

### Jerarquía de Interfaces

```
Repository<T, ID>
    ↓
CrudRepository<T, ID>
    ↓
PagingAndSortingRepository<T, ID>
    ↓
JpaRepository<T, ID>
```

## 📁 Estructura de Repositorios

### Crear el Paquete

Primero, crea la estructura de carpetas:

```
src/main/java/com/example/pib2/
└── repositories/
    ├── UserRepository.java
    ├── ItemRepository.java
    ├── LoanRepository.java
    └── LoanHistoryRepository.java
```

## 👤 UserRepository (Repositorio de Usuarios)

Crea el archivo `src/main/java/com/example/pib2/repositories/UserRepository.java`:

```java
package com.example.pib2.repositories;

import com.example.pib2.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ========================================
    // MÉTODOS AUTOMÁTICOS HEREDADOS
    // ========================================
    // List<User> findAll()
    // Optional<User> findById(Long id)
    // User save(User user)
    // void deleteById(Long id)
    // long count()
    // boolean existsById(Long id)
    // void delete(User user)
    // void deleteAll()
    
    // ========================================
    // CONSULTAS DERIVADAS
    // ========================================
    
    // Buscar por username exacto
    Optional<User> findByUsername(String username);
    
    // Buscar por email exacto
    Optional<User> findByEmail(String email);
    
    // Buscar por rol
    List<User> findByRole(String role);
    
    // Buscar por username que contenga texto (ignorando mayúsculas)
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    // Verificar si existe username
    boolean existsByUsername(String username);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Buscar por username y email
    Optional<User> findByUsernameAndEmail(String username, String email);
    
    // ========================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ========================================
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.username LIKE %:username%")
    List<User> findUsersByRoleAndUsername(@Param("role") String role, @Param("username") String username);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
    
    // Consulta nativa SQL
    @Query(value = "SELECT * FROM users WHERE created_at > NOW() - INTERVAL 30 DAY", nativeQuery = true)
    List<User> findRecentUsers();
}
```

### 🔍 Análisis del UserRepository

#### Herencia de JpaRepository
```java
JpaRepository<User, Long>
//            ↑     ↑
//         Entidad  Tipo del ID
```

#### Métodos Automáticos Disponibles

| Método | Descripción | Ejemplo de Uso |
|--------|-------------|----------------|
| `findAll()` | Obtiene todos los usuarios | `List<User> users = userRepository.findAll();` |
| `findById(Long id)` | Busca por ID | `Optional<User> user = userRepository.findById(1L);` |
| `save(User user)` | Guarda o actualiza | `User saved = userRepository.save(user);` |
| `deleteById(Long id)` | Elimina por ID | `userRepository.deleteById(1L);` |
| `count()` | Cuenta registros | `long total = userRepository.count();` |
| `existsById(Long id)` | Verifica existencia | `boolean exists = userRepository.existsById(1L);` |

## 📦 ItemRepository (Repositorio de Artículos)

Crea el archivo `src/main/java/com/example/pib2/repositories/ItemRepository.java`:

```java
package com.example.pib2.repositories;

import com.example.pib2.models.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // ========================================
    // CONSULTAS DERIVADAS
    // ========================================
    
    // Buscar por nombre exacto
    Optional<Item> findByName(String name);
    
    // Buscar por nombre que contenga texto
    List<Item> findByNameContaining(String name);
    
    // Buscar por nombre ignorando mayúsculas
    List<Item> findByNameContainingIgnoreCase(String name);
    
    // Buscar por cantidad mayor que
    List<Item> findByQuantityGreaterThan(int quantity);
    
    // Buscar por cantidad menor que
    List<Item> findByQuantityLessThan(int quantity);
    
    // Buscar por cantidad entre valores
    List<Item> findByQuantityBetween(int min, int max);
    
    // Buscar por nombre y cantidad
    List<Item> findByNameAndQuantityGreaterThan(String name, int quantity);
    
    // Buscar por descripción que contenga texto (ignorando mayúsculas)
    List<Item> findByDescriptionContainingIgnoreCase(String description);
    
    // Contar items con cantidad menor que
    long countByQuantityLessThan(int quantity);
    
    // Verificar si existe item con nombre
    boolean existsByName(String name);
    
    // Buscar items disponibles (cantidad > 0)
    List<Item> findByQuantityGreaterThanOrderByNameAsc(int quantity);
    
    // ========================================
    // CONSULTAS PERSONALIZADAS
    // ========================================
    
    @Query("SELECT i FROM Item i WHERE i.quantity > 0 AND i.name LIKE %:searchTerm%")
    List<Item> findAvailableItemsByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT i FROM Item i WHERE i.quantity < :threshold ORDER BY i.quantity ASC")
    List<Item> findLowStockItems(@Param("threshold") int threshold);
    
    @Query("SELECT SUM(i.quantity) FROM Item i")
    Long getTotalQuantity();
    
    // Consulta nativa para estadísticas
    @Query(value = "SELECT AVG(quantity) FROM items", nativeQuery = true)
    Double getAverageQuantity();
}
```

## 📋 LoanRepository (Repositorio de Préstamos)

Crea el archivo `src/main/java/com/example/pib2/repositories/LoanRepository.java`:

```java
package com.example.pib2.repositories;

import com.example.pib2.models.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    // ========================================
    // CONSULTAS DERIVADAS
    // ========================================
    
    // Buscar préstamos por usuario
    List<Loan> findByUserId(Long userId);
    
    // Buscar préstamos por artículo
    List<Loan> findByItemId(Long itemId);
    
    // Buscar préstamos devueltos/no devueltos
    List<Loan> findByReturned(boolean returned);
    
    // Buscar préstamos activos de un usuario
    List<Loan> findByUserIdAndReturned(Long userId, boolean returned);
    
    // Buscar préstamos por fecha de préstamo
    List<Loan> findByLoanDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Buscar préstamos vencidos (fecha de devolución pasada y no devueltos)
    List<Loan> findByReturnDateBeforeAndReturnedFalse(LocalDateTime date);
    
    // Contar préstamos activos de un usuario
    long countByUserIdAndReturnedFalse(Long userId);
    
    // Verificar si un usuario tiene préstamos activos
    boolean existsByUserIdAndReturnedFalse(Long userId);
    
    // Verificar si un artículo está prestado
    boolean existsByItemIdAndReturnedFalse(Long itemId);
    
    // ========================================
    // CONSULTAS PERSONALIZADAS
    // ========================================
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.returned = false")
    List<Loan> findActiveLoansByUser(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Loan l WHERE l.returnDate < :currentDate AND l.returned = false")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId")
    long countTotalLoansByUser(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.item WHERE l.returned = false")
    List<Loan> findActiveLoansWithDetails();
    
    // Estadísticas
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.loanDate >= :startDate")
    long countLoansFromDate(@Param("startDate") LocalDateTime startDate);
}
```

## 📊 LoanHistoryRepository (Repositorio de Historial)

Crea el archivo `src/main/java/com/example/pib2/repositories/LoanHistoryRepository.java`:

```java
package com.example.pib2.repositories;

import com.example.pib2.models.entities.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {
    
    // ========================================
    // CONSULTAS DERIVADAS
    // ========================================
    
    // Buscar historial por préstamo
    List<LoanHistory> findByLoanId(Long loanId);
    
    // Buscar por acción específica
    List<LoanHistory> findByAction(String action);
    
    // Buscar por rango de fechas
    List<LoanHistory> findByActionDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Buscar por préstamo y acción
    List<LoanHistory> findByLoanIdAndAction(Long loanId, String action);
    
    // Buscar historial ordenado por fecha
    List<LoanHistory> findByLoanIdOrderByActionDateDesc(Long loanId);
    
    // Buscar acciones recientes
    List<LoanHistory> findByActionDateAfterOrderByActionDateDesc(LocalDateTime date);
    
    // ========================================
    // CONSULTAS PERSONALIZADAS
    // ========================================
    
    @Query("SELECT lh FROM LoanHistory lh WHERE lh.loan.user.id = :userId ORDER BY lh.actionDate DESC")
    List<LoanHistory> findHistoryByUser(@Param("userId") Long userId);
    
    @Query("SELECT lh FROM LoanHistory lh WHERE lh.action = :action AND lh.actionDate >= :fromDate")
    List<LoanHistory> findRecentActionHistory(@Param("action") String action, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(lh) FROM LoanHistory lh WHERE lh.action = :action AND lh.actionDate >= :fromDate")
    long countActionsSince(@Param("action") String action, @Param("fromDate") LocalDateTime fromDate);
    
    // Auditoría completa de un préstamo
    @Query("SELECT lh FROM LoanHistory lh JOIN FETCH lh.loan WHERE lh.loan.id = :loanId ORDER BY lh.actionDate ASC")
    List<LoanHistory> findCompleteAuditTrail(@Param("loanId") Long loanId);
}
```

## 🔍 Consultas Derivadas - Palabras Clave

### Palabras Clave Principales

| Palabra Clave | Descripción | Ejemplo |
|---------------|-------------|----------|
| `findBy` | Buscar registros | `findByUsername(String username)` |
| `countBy` | Contar registros | `countByRole(String role)` |
| `existsBy` | Verificar existencia | `existsByEmail(String email)` |
| `deleteBy` | Eliminar registros | `deleteByUsername(String username)` |

### Operadores de Comparación

| Operador | Descripción | Ejemplo |
|----------|-------------|----------|
| `GreaterThan` | Mayor que | `findByQuantityGreaterThan(int quantity)` |
| `LessThan` | Menor que | `findByQuantityLessThan(int quantity)` |
| `Between` | Entre valores | `findByQuantityBetween(int min, int max)` |
| `Like` | Coincidencia parcial | `findByNameLike(String pattern)` |
| `Containing` | Contiene texto | `findByNameContaining(String text)` |
| `IgnoreCase` | Ignorar mayúsculas | `findByNameIgnoreCase(String name)` |
| `OrderBy` | Ordenar resultados | `findByRoleOrderByUsernameAsc(String role)` |

### Operadores Lógicos

| Operador | Descripción | Ejemplo |
|----------|-------------|----------|
| `And` | Y lógico | `findByUsernameAndEmail(String username, String email)` |
| `Or` | O lógico | `findByUsernameOrEmail(String username, String email)` |
| `Not` | Negación | `findByUsernameNot(String username)` |
| `In` | En lista | `findByRoleIn(List<String> roles)` |
| `NotIn` | No en lista | `findByRoleNotIn(List<String> roles)` |

## 📝 Consultas Personalizadas con @Query

### JPQL (Java Persistence Query Language)

```java
// Consulta JPQL básica
@Query("SELECT u FROM User u WHERE u.role = :role")
List<User> findUsersByRole(@Param("role") String role);

// Consulta JPQL con JOIN
@Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.item WHERE l.returned = false")
List<Loan> findActiveLoansWithDetails();

// Consulta JPQL con funciones agregadas
@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
long countByRole(@Param("role") String role);
```

### SQL Nativo

```java
// Consulta SQL nativa
@Query(value = "SELECT * FROM users WHERE created_at > NOW() - INTERVAL 30 DAY", nativeQuery = true)
List<User> findRecentUsers();

// Consulta SQL nativa con parámetros
@Query(value = "SELECT AVG(quantity) FROM items WHERE name LIKE %:name%", nativeQuery = true)
Double getAverageQuantityByName(@Param("name") String name);
```

## ✅ Verificación de Repositorios

### 1. Verificar Estructura de Archivos

```bash
# Verificar que los archivos existen
ls src/main/java/com/example/pib2/repositories/
```

Deberías ver:
```
UserRepository.java
ItemRepository.java
LoanRepository.java
LoanHistoryRepository.java
```

### 2. Compilar el Proyecto

```bash
# Compilar para verificar sintaxis
./mvnw compile
```

### 3. Ejecutar la Aplicación

```bash
# Ejecutar la aplicación
./mvnw spring-boot:run
```

### 4. Verificar en Logs

Busca en los logs mensajes como:
```
Hibernate: create table users (...)
Hibernate: create table items (...)
Hibernate: create table loans (...)
Hibernate: create table loan_history (...)
```

## 🚨 Problemas Comunes y Soluciones

### Error: "No qualifying bean of type repository found"

**Problema**: Spring no encuentra el repositorio

**Solución**:
```java
// Agregar @Repository a la interfaz
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ...
}

// O habilitar escaneo de repositorios en la clase principal
@SpringBootApplication
@EnableJpaRepositories("com.example.pib2.repositories")
public class Pib2Application {
    // ...
}
```

### Error: "Invalid derived query"

**Problema**: Nombre de método incorrecto

**Solución**:
```java
// ❌ Incorrecto
List<User> findByUserName(String username); // Campo se llama 'username', no 'userName'

// ✅ Correcto
List<User> findByUsername(String username);
```

### Error: "Could not resolve parameter"

**Problema**: Falta @Param en consulta personalizada

**Solución**:
```java
// ❌ Incorrecto
@Query("SELECT u FROM User u WHERE u.role = :role")
List<User> findUsersByRole(String role);

// ✅ Correcto
@Query("SELECT u FROM User u WHERE u.role = :role")
List<User> findUsersByRole(@Param("role") String role);
```

## 🎯 Mejores Prácticas

### 1. **Nomenclatura Consistente**
```java
// ✅ Bueno: Nombres descriptivos
List<User> findByUsernameContainingIgnoreCase(String username);
boolean existsByEmailAndUsernameNot(String email, String username);

// ❌ Malo: Nombres ambiguos
List<User> findByName(String name); // ¿username o fullName?
List<User> findUsers(String param); // ¿qué parámetro?
```

### 2. **Usar Optional para Resultados Únicos**
```java
// ✅ Bueno: Manejo seguro de nulos
Optional<User> findByUsername(String username);
Optional<User> findByEmail(String email);

// ❌ Malo: Puede retornar null
User findByUsername(String username);
```

### 3. **Consultas Eficientes**
```java
// ✅ Bueno: JOIN FETCH para evitar N+1
@Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.item")
List<Loan> findAllWithDetails();

// ❌ Malo: Carga perezosa puede causar N+1
List<Loan> findAll(); // Luego acceder a loan.getUser().getUsername()
```

### 4. **Validación de Parámetros**
```java
// En el servicio que usa el repositorio
public Optional<User> findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
        return Optional.empty();
    }
    return userRepository.findByUsername(username.trim());
}
```

### 5. **Documentación de Consultas Complejas**
```java
/**
 * Encuentra préstamos vencidos que no han sido devueltos.
 * Un préstamo se considera vencido si la fecha de devolución
 * es anterior a la fecha actual y el campo 'returned' es false.
 * 
 * @param currentDate fecha actual para comparar
 * @return lista de préstamos vencidos
 */
@Query("SELECT l FROM Loan l WHERE l.returnDate < :currentDate AND l.returned = false")
List<Loan> findOverdueLoans(@Param("currentDate") LocalDateTime currentDate);
```

## 🔑 Conceptos Clave Aprendidos

1. **Spring Data JPA**: Abstracción que simplifica el acceso a datos
2. **JpaRepository**: Interfaz que proporciona métodos CRUD automáticos
3. **Consultas Derivadas**: Generación automática basada en nombres de métodos
4. **@Query**: Consultas personalizadas con JPQL o SQL nativo
5. **@Repository**: Anotación para marcar componentes de acceso a datos
6. **Optional**: Manejo seguro de resultados que pueden ser nulos
7. **JOIN FETCH**: Optimización para evitar el problema N+1

## 🚀 Próximos Pasos

En el siguiente tutorial aprenderás sobre:
- **Servicios y Lógica de Negocio**: Implementar la capa de servicios
- **Inyección de Dependencias**: Usar repositorios en servicios
- **Transacciones**: Gestión automática de transacciones
- **Validaciones de Negocio**: Reglas específicas del dominio
- **Manejo de Excepciones**: Gestión de errores en la capa de datos

---

**📚 Recursos Adicionales:**
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [Custom Queries](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query)

**🔗 Enlaces Relacionados:**
- [← 4. DTOs y Mapeo de Datos](04-dtos-mapeo.md)
- [→ 6. Servicios y Lógica de Negocio](06-servicios-logica-negocio.md)
- [📋 Índice Principal](README.md)