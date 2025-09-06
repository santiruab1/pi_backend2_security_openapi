# Tutorial: Construyendo una API REST con Spring Boot

## 📚 Guía Completa para Desarrollar un Sistema de Inventario

Este tutorial te guiará paso a paso en la construcción de una API REST completa utilizando Spring Boot. Aprenderás a crear un sistema de inventario funcional que incluye gestión de usuarios, items, préstamos e historial.

## 🎯 Objetivos del Tutorial

Al finalizar este tutorial, serás capaz de:
- Configurar un proyecto Spring Boot desde cero
- Implementar una arquitectura REST bien estructurada
- Trabajar con bases de datos usando JPA/Hibernate
- Crear entidades, DTOs, servicios y controladores
- Implementar operaciones CRUD completas
- Configurar endpoints de monitoreo con Actuator
- Probar tu API de manera efectiva

## 📋 Contenido del Tutorial

### 0. [Estructura del Proyecto](00-estructura-proyecto.md)
- Estructura de carpetas del proyecto Spring Boot
- Organización de paquetes en Java
- Convenciones de nomenclatura
- Mejores prácticas de organización

### 1. [Configuración Inicial del Proyecto](01-configuracion-inicial.md)
- Creación del proyecto Spring Boot
- Estructura de directorios
- Configuración del archivo `pom.xml`
- Configuración básica de la aplicación

### 2. [Configuración de Base de Datos y JPA](02-configuracion-database.md)
- Configuración de PostgreSQL
- Propiedades de conexión
- Configuración de JPA/Hibernate
- Variables de entorno

### 3. [Creación de Entidades](03-entidades-modelos.md)
- Diseño del modelo de datos
- Entidad User (Usuario)
- Entidad Item (Artículo)
- Entidad Loan (Préstamo)
- Entidad LoanHistory (Historial de Préstamos)
- Relaciones entre entidades

### 4. [DTOs y Mapeo de Datos](04-dtos-mapeo.md)
- ¿Qué son los DTOs y por qué usarlos?
- Creación de DTOs para cada entidad
- Métodos de conversión (toDTO/toEntity)
- Mejores prácticas de mapeo

### 5. [Repositorios y Acceso a Datos](05-repositorios-acceso-datos.md)
- Spring Data JPA y repositorios
- Operaciones CRUD automáticas
- Consultas derivadas y personalizadas
- Mejores prácticas de acceso a datos

### 6. [Servicios y Lógica de Negocio](06-servicios-logica-negocio.md)
- Patrón de servicios en Spring
- Implementación de servicios para cada entidad
- Inyección de dependencias
- Manejo de excepciones

### 7. [Controladores REST](07-controladores-rest.md)
- Fundamentos de REST
- Creación de controladores
- Anotaciones de Spring Web
- Implementación de endpoints CRUD
- Manejo de respuestas HTTP

### 8. [Actuator y Monitoreo](08-actuator-monitoreo.md)
- Configuración de Spring Boot Actuator
- Endpoints de salud e información
- Health Indicators personalizados
- Métricas y monitoreo de aplicaciones

### 9. [Validación y Manejo de Errores](09-validacion-manejo-errores.md)
- Bean Validation con anotaciones
- Manejo centralizado de errores
- Excepciones personalizadas
- Validaciones de negocio

## 🚀 Proyecto de Referencia

Este tutorial está basado en un proyecto real de sistema de inventario que incluye:

**Entidades principales:**
- **Users**: Gestión de usuarios del sistema
- **Items**: Catálogo de artículos disponibles
- **Loans**: Registro de préstamos de artículos
- **LoanHistory**: Historial de acciones sobre préstamos

**Funcionalidades:**
- CRUD completo para todas las entidades
- Relaciones entre entidades
- Endpoints de monitoreo
- Validación de datos
- Manejo de errores

## 📁 Estructura del Proyecto Final

```
pi_backend2/
├── src/
│   ├── main/
│   │   ├── java/com/example/pib2/
│   │   │   ├── controllers/     # Controladores REST
│   │   │   ├── models/
│   │   │   │   ├── entities/    # Entidades JPA
│   │   │   │   └── dtos/        # Data Transfer Objects
│   │   │   ├── servicios/       # Lógica de negocio
│   │   │   └── Pib2Application.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── doc/                         # Documentación del tutorial
├── pom.xml                      # Dependencias Maven
└── test-endpoints.ps1           # Script de pruebas
```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.x**: Framework principal
- **Spring Web**: Para crear APIs REST
- **Spring Data JPA**: Para persistencia de datos
- **PostgreSQL**: Base de datos
- **Spring Boot Actuator**: Monitoreo y métricas
- **Maven**: Gestión de dependencias

## 📝 Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:
- Java 21 o superior
- Maven 3.6+
- PostgreSQL (o acceso a una base de datos PostgreSQL)
- Un IDE como IntelliJ IDEA, Eclipse o VS Code
- Conocimientos básicos de Java y programación orientada a objetos

## 🎓 Metodología de Aprendizaje

Cada sección del tutorial incluye:

- **Explicación teórica**: Conceptos fundamentales
- **Código de ejemplo**: Implementación práctica
- **Explicación línea por línea**: Detalles de cada componente
- **Ejercicios prácticos**: Para reforzar el aprendizaje
- **Consejos y mejores prácticas**: Experiencia del mundo real

---

¡Comienza tu viaje en el desarrollo de APIs REST con Spring Boot! 🚀

[**Siguiente: Configuración Inicial del Proyecto →**](01-configuracion-inicial.md)