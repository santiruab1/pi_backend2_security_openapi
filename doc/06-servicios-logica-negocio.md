# 6. Servicios y Lógica de Negocio

## 🎯 Objetivos

En esta sección aprenderás a:
- Entender la arquitectura de capas en Spring Boot
- Implementar servicios con lógica de negocio
- Aplicar el patrón de inyección de dependencias
- Manejar transacciones y operaciones complejas
- Separar responsabilidades entre capas
- Coordinar operaciones entre múltiples repositorios

## 📋 Prerrequisitos

- Entidades JPA creadas
- DTOs implementados
- Repositorios configurados
- Conocimientos básicos de Spring Framework
- Comprensión de patrones de diseño

## 🏗️ Arquitectura de Capas

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  UserController │  │  ItemController │  │ LoanController│ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE LÓGICA DE NEGOCIO                │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   UserService   │  │   ItemService   │  │ LoanService │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                  CAPA DE ACCESO A DATOS                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ UserRepository  │  │ ItemRepository  │  │LoanRepository│ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PERSISTENCIA                    │
│                      (Base de Datos)                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │     users       │  │      items      │  │    loans    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Responsabilidades por Capa

#### 1. **Capa de Presentación (Controllers)**
- Recibir peticiones HTTP
- Validar datos de entrada
- Convertir entre DTOs y Entidades
- Devolver respuestas HTTP

#### 2. **Capa de Lógica de Negocio (Services)**
- Implementar reglas de negocio
- Coordinar operaciones entre repositorios
- Manejar transacciones
- Validaciones complejas
- Aplicar políticas empresariales

#### 3. **Capa de Acceso a Datos (Repositories)**
- Operaciones CRUD básicas
- Consultas personalizadas
- Abstracción de la base de datos

#### 4. **Capa de Persistencia (Database)**
- Almacenamiento físico de datos
- Integridad referencial
- Índices y optimizaciones

## 🔧 ¿Qué es un Servicio?

Un **Servicio** es una clase que contiene la lógica de negocio de la aplicación. Los servicios:

### Características Principales
- **Coordinan operaciones**: Entre múltiples repositorios
- **Implementan reglas de negocio**: Validaciones específicas del dominio
- **Manejan transacciones**: Operaciones atómicas
- **Proporcionan API limpia**: Para los controladores
- **Encapsulan complejidad**: Ocultan detalles de implementación

### Patrón de Servicio

```java
@Service  // Marca la clase como un componente de servicio
@Transactional  // Habilita manejo automático de transacciones
public class UserService {
    
    @Autowired  // Inyección de dependencias
    private UserRepository userRepository;
    
    // Métodos que encapsulan lógica de negocio
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
```

## 📁 Estructura de Servicios

### Crear el Paquete

Primero, crea la estructura de carpetas:

```
src/main/java/com/example/pib2/
└── services/
    ├── UserService.java
    ├── ItemService.java
    ├── LoanService.java
    └── LoanHistoryService.java
```

## 👤 UserService (Servicio de Usuarios)

Crea el archivo `src/main/java/com/example/pib2/services/UserService.java`:

```java
package com.example.pib2.services;

import com.example.pib2.models.entities.User;
import com.example.pib2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================
    
    public List<User> findAll() {
        // Lógica: Solo usuarios activos
        return userRepository.findAll().stream()
            .filter(user -> !user.isDeleted())
            .collect(Collectors.toList());
    }
    
    public Optional<User> findById(Long id) {
        // Lógica: Validar que el ID no sea nulo
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        // Lógica de negocio: Validaciones y encriptación
        validateUser(user);
        
        if (user.getId() == null) {
            // Nuevo usuario: encriptar password y verificar unicidad
            validateUniqueFields(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setDeleted(false);
        } else {
            // Actualización: mantener password si no se cambió
            User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existing.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            user.setCreatedAt(existing.getCreatedAt());
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        // Lógica: Verificar que no tenga préstamos activos
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        if (hasActiveLoans(user)) {
            throw new BusinessException("Cannot delete user with active loans");
        }
        
        // Soft delete
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // ========================================
    // LÓGICA DE NEGOCIO ESPECÍFICA
    // ========================================
    
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }
    
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
    
    public boolean canBorrow(Long userId) {
        User user = findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Regla de negocio: Máximo 3 préstamos activos
        long activeLoanCount = user.getLoans().stream()
            .filter(loan -> !loan.isReturned())
            .count();
            
        return activeLoanCount < 3;
    }
    
    public List<User> findUsersByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByRole(role.toUpperCase());
    }
    
    public long countActiveUsers() {
        return userRepository.findAll().stream()
            .filter(user -> !user.isDeleted())
            .count();
    }
    
    // ========================================
    // MÉTODOS DE VALIDACIÓN PRIVADOS
    // ========================================
    
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (user.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        
        if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Valid email is required");
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER"); // Rol por defecto
        }
    }
    
    private void validateUniqueFields(User user) {
        // Verificar username único
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        // Verificar email único
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already exists");
        }
    }
    
    private boolean hasActiveLoans(User user) {
        return user.getLoans().stream()
            .anyMatch(loan -> !loan.isReturned());
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
```

### 🔍 Análisis del UserService

#### Anotaciones Importantes

```java
@Service
// - Marca la clase como un componente de Spring
// - Permite la inyección de dependencias
// - Habilita la gestión de transacciones
// - Facilita el testing con mocks

@Transactional
// - Habilita manejo automático de transacciones
// - Rollback automático en caso de excepción
// - Puede aplicarse a nivel de clase o método
```

#### Inyección de Dependencias

```java
@Autowired
private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;

// Spring automáticamente inyecta las instancias
// No necesitas crear los objetos manualmente
```

#### Métodos de Servicio

| Método | Propósito | Lógica de Negocio |
|--------|-----------|-------------------|
| `findAll()` | Obtener usuarios activos | Filtrar usuarios eliminados |
| `findById(Long id)` | Buscar usuario específico | Validar ID positivo |
| `save(User user)` | Crear/actualizar usuario | Validaciones, encriptación, unicidad |
| `deleteById(Long id)` | Eliminar usuario | Verificar préstamos activos, soft delete |
| `canBorrow(Long userId)` | Verificar capacidad de préstamo | Límite de 3 préstamos activos |

## 📦 ItemService (Servicio de Artículos)

Crea el archivo `src/main/java/com/example/pib2/services/ItemService.java`:

```java
package com.example.pib2.services;

import com.example.pib2.models.entities.Item;
import com.example.pib2.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================
    
    public List<Item> findAll() {
        return itemRepository.findAll();
    }
    
    public Optional<Item> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return itemRepository.findById(id);
    }
    
    public Item save(Item item) {
        validateItem(item);
        
        if (item.getId() == null) {
            // Nuevo item
            validateUniqueName(item.getName());
            item.setCreatedAt(LocalDateTime.now());
        } else {
            // Actualización
            Item existing = itemRepository.findById(item.getId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
            item.setCreatedAt(existing.getCreatedAt());
            item.setUpdatedAt(LocalDateTime.now());
        }
        
        return itemRepository.save(item);
    }
    
    public void deleteById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
            
        // Verificar que no esté prestado
        if (isCurrentlyLoaned(item)) {
            throw new BusinessException("Cannot delete item that is currently loaned");
        }
        
        itemRepository.deleteById(id);
    }
    
    // ========================================
    // LÓGICA DE NEGOCIO ESPECÍFICA
    // ========================================
    
    public List<Item> findAvailableItems() {
        return itemRepository.findByQuantityGreaterThan(0);
    }
    
    public List<Item> findLowStockItems(int threshold) {
        return itemRepository.findByQuantityLessThan(threshold);
    }
    
    public boolean isAvailable(Long itemId, int requestedQuantity) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent() && item.get().getQuantity() >= requestedQuantity;
    }
    
    public void reserveQuantity(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
            
        if (item.getQuantity() < quantity) {
            throw new BusinessException("Insufficient quantity available. Available: " 
                + item.getQuantity() + ", Requested: " + quantity);
        }
        
        item.setQuantity(item.getQuantity() - quantity);
        itemRepository.save(item);
    }
    
    public void releaseQuantity(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
            
        item.setQuantity(item.getQuantity() + quantity);
        itemRepository.save(item);
    }
    
    public List<Item> searchItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        
        String term = searchTerm.trim().toLowerCase();
        return itemRepository.findAll().stream()
            .filter(item -> 
                item.getName().toLowerCase().contains(term) ||
                item.getDescription().toLowerCase().contains(term)
            )
            .collect(Collectors.toList());
    }
    
    public long getTotalInventoryValue() {
        return itemRepository.findAll().stream()
            .mapToLong(item -> item.getQuantity())
            .sum();
    }
    
    // ========================================
    // MÉTODOS DE VALIDACIÓN PRIVADOS
    // ========================================
    
    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        
        if (item.getName().length() < 2) {
            throw new IllegalArgumentException("Item name must be at least 2 characters");
        }
        
        if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Item description is required");
        }
        
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
    
    private void validateUniqueName(String name) {
        if (itemRepository.existsByName(name)) {
            throw new BusinessException("Item with this name already exists");
        }
    }
    
    private boolean isCurrentlyLoaned(Item item) {
        // Verificar si el item tiene préstamos activos
        return item.getLoans().stream()
            .anyMatch(loan -> !loan.isReturned());
    }
}
```

## 📋 LoanService (Servicio de Préstamos)

Crea el archivo `src/main/java/com/example/pib2/services/LoanService.java`:

```java
package com.example.pib2.services;

import com.example.pib2.models.entities.Loan;
import com.example.pib2.models.entities.User;
import com.example.pib2.models.entities.Item;
import com.example.pib2.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanService {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private LoanHistoryService loanHistoryService;
    
    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================
    
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }
    
    public Optional<Loan> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return loanRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        Loan loan = loanRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Loan not found"));
            
        if (!loan.isReturned()) {
            throw new BusinessException("Cannot delete active loan");
        }
        
        loanRepository.deleteById(id);
    }
    
    // ========================================
    // LÓGICA DE NEGOCIO COMPLEJA
    // ========================================
    
    public Loan createLoan(Long userId, Long itemId, LocalDateTime returnDate) {
        // Validaciones de entrada
        validateLoanRequest(userId, itemId, returnDate);
        
        // Obtener entidades
        User user = userService.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        Item item = itemService.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        
        // Validaciones de negocio
        validateLoanEligibility(user, item);
        
        // Crear préstamo
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setItem(item);
        loan.setLoanDate(LocalDateTime.now());
        loan.setReturnDate(returnDate);
        loan.setReturned(false);
        
        // Reservar cantidad del item
        itemService.reserveQuantity(itemId, 1);
        
        // Guardar préstamo
        Loan savedLoan = loanRepository.save(loan);
        
        // Crear registro de historial
        loanHistoryService.createHistory(savedLoan.getId(), "CREATED", 
            "Loan created for user: " + user.getUsername() + ", item: " + item.getName());
        
        return savedLoan;
    }
    
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new EntityNotFoundException("Loan not found"));
        
        if (loan.isReturned()) {
            throw new BusinessException("Loan is already returned");
        }
        
        // Marcar como devuelto
        loan.setReturned(true);
        loan.setActualReturnDate(LocalDateTime.now());
        
        // Liberar cantidad del item
        itemService.releaseQuantity(loan.getItem().getId(), 1);
        
        // Guardar cambios
        Loan savedLoan = loanRepository.save(loan);
        
        // Crear registro de historial
        loanHistoryService.createHistory(savedLoan.getId(), "RETURNED", 
            "Loan returned by user: " + loan.getUser().getUsername());
        
        return savedLoan;
    }
    
    public List<Loan> findActiveLoans() {
        return loanRepository.findByReturned(false);
    }
    
    public List<Loan> findLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }
    
    public List<Loan> findActiveLoansByUser(Long userId) {
        return loanRepository.findByUserIdAndReturned(userId, false);
    }
    
    public List<Loan> findOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();
        return loanRepository.findByReturnDateBeforeAndReturnedFalse(now);
    }
    
    public List<Loan> findLoansDueSoon(int days) {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(days);
        return loanRepository.findByReturnDateBeforeAndReturnedFalse(futureDate)
            .stream()
            .filter(loan -> loan.getReturnDate().isAfter(LocalDateTime.now()))
            .collect(Collectors.toList());
    }
    
    public long countActiveLoans() {
        return loanRepository.countByReturnedFalse();
    }
    
    public long countOverdueLoans() {
        return findOverdueLoans().size();
    }
    
    // ========================================
    // MÉTODOS DE VALIDACIÓN PRIVADOS
    // ========================================
    
    private void validateLoanRequest(Long userId, Long itemId, LocalDateTime returnDate) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        
        if (itemId == null || itemId <= 0) {
            throw new IllegalArgumentException("Valid item ID is required");
        }
        
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date is required");
        }
        
        if (returnDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Return date cannot be in the past");
        }
        
        // Máximo 30 días de préstamo
        if (returnDate.isAfter(LocalDateTime.now().plusDays(30))) {
            throw new IllegalArgumentException("Loan period cannot exceed 30 days");
        }
    }
    
    private void validateLoanEligibility(User user, Item item) {
        // Verificar que el usuario puede pedir prestado
        if (!userService.canBorrow(user.getId())) {
            throw new BusinessException("User has reached maximum loan limit (3 active loans)");
        }
        
        // Verificar disponibilidad del item
        if (!itemService.isAvailable(item.getId(), 1)) {
            throw new BusinessException("Item is not available for loan");
        }
        
        // Verificar que el usuario no tenga préstamos vencidos
        List<Loan> userOverdueLoans = loanRepository.findByUserId(user.getId())
            .stream()
            .filter(loan -> !loan.isReturned() && 
                loan.getReturnDate().isBefore(LocalDateTime.now()))
            .collect(Collectors.toList());
            
        if (!userOverdueLoans.isEmpty()) {
            throw new BusinessException("User has overdue loans and cannot borrow new items");
        }
    }
}
```

## 📊 LoanHistoryService (Servicio de Historial)

Crea el archivo `src/main/java/com/example/pib2/services/LoanHistoryService.java`:

```java
package com.example.pib2.services;

import com.example.pib2.models.entities.LoanHistory;
import com.example.pib2.models.entities.Loan;
import com.example.pib2.repositories.LoanHistoryRepository;
import com.example.pib2.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanHistoryService {
    
    @Autowired
    private LoanHistoryRepository loanHistoryRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================
    
    public List<LoanHistory> findAll() {
        return loanHistoryRepository.findAll();
    }
    
    public Optional<LoanHistory> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return loanHistoryRepository.findById(id);
    }
    
    // ========================================
    // LÓGICA DE NEGOCIO ESPECÍFICA
    // ========================================
    
    public LoanHistory createHistory(Long loanId, String action) {
        return createHistory(loanId, action, null);
    }
    
    public LoanHistory createHistory(Long loanId, String action, String details) {
        // Validaciones
        if (loanId == null || loanId <= 0) {
            throw new IllegalArgumentException("Valid loan ID is required");
        }
        
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action is required");
        }
        
        // Verificar que el préstamo existe
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new EntityNotFoundException("Loan not found"));
        
        // Crear registro de historial
        LoanHistory history = new LoanHistory();
        history.setLoan(loan);
        history.setAction(action.toUpperCase());
        history.setActionDate(LocalDateTime.now());
        history.setDetails(details);
        
        return loanHistoryRepository.save(history);
    }
    
    public List<LoanHistory> findHistoryByLoan(Long loanId) {
        return loanHistoryRepository.findByLoanIdOrderByActionDateDesc(loanId);
    }
    
    public List<LoanHistory> findHistoryByAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return List.of();
        }
        return loanHistoryRepository.findByAction(action.toUpperCase());
    }
    
    public List<LoanHistory> findRecentHistory(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return loanHistoryRepository.findByActionDateAfterOrderByActionDateDesc(fromDate);
    }
    
    public List<LoanHistory> findHistoryByUser(Long userId) {
        return loanHistoryRepository.findHistoryByUser(userId);
    }
    
    public long countActionsSince(String action, int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return loanHistoryRepository.countActionsSince(action.toUpperCase(), fromDate);
    }
    
    public List<LoanHistory> getCompleteAuditTrail(Long loanId) {
        return loanHistoryRepository.findCompleteAuditTrail(loanId);
    }
}
```

## 🔄 Manejo de Transacciones

### ¿Qué son las Transacciones?

Una **transacción** es una unidad de trabajo que debe ejecutarse completamente o no ejecutarse en absoluto.

### Anotación @Transactional

```java
@Service
@Transactional  // Nivel de clase: todas las operaciones son transaccionales
public class LoanService {
    
    @Transactional(readOnly = true)  // Solo lectura: optimización
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }
    
    @Transactional(rollbackFor = Exception.class)  // Rollback para cualquier excepción
    public Loan createLoan(Long userId, Long itemId, LocalDateTime returnDate) {
        // Si cualquier operación falla, se hace rollback de todo
        User user = userService.findById(userId).orElseThrow(...);
        Item item = itemService.findById(itemId).orElseThrow(...);
        
        itemService.reserveQuantity(itemId, 1);  // Operación 1
        Loan loan = loanRepository.save(newLoan);  // Operación 2
        loanHistoryService.createHistory(loan.getId(), "CREATED");  // Operación 3
        
        return loan;
    }
}
```

### Propagación de Transacciones

```java
@Transactional(propagation = Propagation.REQUIRED)  // Por defecto
public void method1() {
    // Se une a transacción existente o crea una nueva
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void method2() {
    // Siempre crea una nueva transacción
}

@Transactional(propagation = Propagation.SUPPORTS)
public void method3() {
    // Se ejecuta en transacción si existe, sino sin transacción
}
```

## 🎯 Inyección de Dependencias

### Tipos de Inyección

#### 1. Inyección por Campo (Field Injection)
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Inyección directa
}
```

#### 2. Inyección por Constructor (Recomendada)
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

#### 3. Inyección por Setter
```java
@Service
public class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Ventajas de la Inyección por Constructor

- **Inmutabilidad**: Campos pueden ser `final`
- **Testing**: Fácil inyección de mocks
- **Validación**: Falla rápido si faltan dependencias
- **Claridad**: Dependencias explícitas

## 🚨 Excepciones Personalizadas

Crea el archivo `src/main/java/com/example/pib2/exceptions/BusinessException.java`:

```java
package com.example.pib2.exceptions;

public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Uso de Excepciones en Servicios

```java
@Service
public class UserService {
    
    public User save(User user) {
        try {
            validateUser(user);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("User data violates business rules", e);
        } catch (Exception e) {
            throw new BusinessException("Unexpected error saving user", e);
        }
    }
}
```

## ✅ Verificación de Servicios

### 1. Verificar Estructura de Archivos

```bash
# Verificar que los archivos existen
ls src/main/java/com/example/pib2/services/
```

Deberías ver:
```
UserService.java
ItemService.java
LoanService.java
LoanHistoryService.java
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
Started Pib2Application in X.XXX seconds
No active profile set, falling back to default profiles: default
```

## 🚨 Problemas Comunes y Soluciones

### Error: "Circular dependency"

**Problema**: Servicios se referencian mutuamente

**Solución**:
```java
// ❌ Problemático: Dependencia circular
@Service
public class UserService {
    @Autowired
    private LoanService loanService;  // LoanService también usa UserService
}

// ✅ Solución: Usar @Lazy o reestructurar
@Service
public class UserService {
    @Autowired
    @Lazy
    private LoanService loanService;
}
```

### Error: "No qualifying bean found"

**Problema**: Spring no encuentra el servicio

**Solución**:
```java
// Verificar que la clase esté anotada
@Service  // ← Esta anotación es necesaria
public class UserService {
    // ...
}

// Verificar que esté en el paquete correcto
// com.example.pib2.services (debe estar bajo com.example.pib2)
```

### Error: "Transaction rolled back"

**Problema**: Excepción no controlada en transacción

**Solución**:
```java
@Transactional
public User save(User user) {
    try {
        validateUser(user);
        return userRepository.save(user);
    } catch (ValidationException e) {
        // Manejar excepción específica
        throw new BusinessException("Validation failed: " + e.getMessage());
    }
}
```

## 🎯 Mejores Prácticas

### 1. **Separación de Responsabilidades**
```java
// ✅ Bueno: Cada servicio tiene una responsabilidad clara
@Service
public class UserService {
    // Solo lógica relacionada con usuarios
}

@Service
public class LoanService {
    // Solo lógica relacionada con préstamos
    @Autowired
    private UserService userService;  // Delega validaciones de usuario
}
```

### 2. **Validaciones en Capas**
```java
// Controlador: Validaciones de formato
@PostMapping
public ResponseEntity<User> create(@Valid @RequestBody UserDTO userDTO) {
    // @Valid valida anotaciones de Bean Validation
}

// Servicio: Validaciones de negocio
@Service
public class UserService {
    public User save(User user) {
        validateBusinessRules(user);  // Reglas específicas del dominio
        return userRepository.save(user);
    }
}
```

### 3. **Manejo Consistente de Errores**
```java
@Service
public class UserService {
    
    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();  // No lanzar excepción para casos esperados
        }
        return userRepository.findById(id);
    }
    
    public User getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
}
```

### 4. **Logging Apropiado**
```java
@Service
@Slf4j  // Lombok para logging
public class LoanService {
    
    public Loan createLoan(Long userId, Long itemId, LocalDateTime returnDate) {
        log.info("Creating loan for user {} and item {}", userId, itemId);
        
        try {
            Loan loan = performLoanCreation(userId, itemId, returnDate);
            log.info("Loan created successfully with id {}", loan.getId());
            return loan;
        } catch (BusinessException e) {
            log.warn("Business rule violation creating loan: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating loan", e);
            throw new BusinessException("Failed to create loan", e);
        }
    }
}
```

### 5. **Testing de Servicios**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        User result = userService.save(user);
        
        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }
}
```

## 🔑 Conceptos Clave Aprendidos

1. **Servicios**: Capa que contiene la lógica de negocio
2. **@Service**: Anotación para marcar componentes de servicio
3. **@Transactional**: Manejo automático de transacciones
4. **Inyección de Dependencias**: Inversión de control con Spring
5. **Separación de Responsabilidades**: Cada capa tiene un propósito específico
6. **Validaciones de Negocio**: Reglas específicas del dominio
7. **Coordinación de Servicios**: Orquestación de operaciones complejas
8. **Manejo de Excepciones**: Gestión consistente de errores

## 🚀 Próximos Pasos

En el siguiente tutorial aprenderás sobre:
- **Controladores REST**: Crear APIs para exponer servicios
- **DTOs en Controladores**: Conversión entre DTOs y entidades
- **Validación de Entrada**: Bean Validation en endpoints
- **Manejo de Respuestas**: ResponseEntity y códigos de estado
- **Documentación de APIs**: Swagger/OpenAPI

---

**📚 Recursos Adicionales:**
- [Spring Framework Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Spring Boot Features](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html)
- [Transaction Management](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)

**🔗 Enlaces Relacionados:**
- [← 5. Repositorios y Acceso a Datos](05-repositorios-acceso-datos.md)
- [→ 7. Controladores REST](07-controladores-rest.md)
- [📋 Índice Principal](README.md)