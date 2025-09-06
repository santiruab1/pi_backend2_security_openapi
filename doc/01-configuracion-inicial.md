# 1. Configuración Inicial del Proyecto Spring Boot

## 🎯 Objetivos

En esta sección aprenderás a:
- Crear un proyecto Spring Boot desde cero
- Configurar las dependencias necesarias
- Entender la estructura básica del proyecto
- Configurar el archivo principal de la aplicación

## 📋 Prerrequisitos

- Java 21 instalado
- Maven 3.6+ instalado
- IDE de tu preferencia (IntelliJ IDEA, Eclipse, VS Code)
- Conocimientos básicos de Java

## 🚀 Creación del Proyecto

### Opción 1: Spring Initializr (Recomendado)

1. Ve a [Spring Initializr](https://start.spring.io/)
2. Configura los siguientes parámetros:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.5.4
   - **Group**: `com.example`
   - **Artifact**: `pib2`
   - **Name**: `pib2`
   - **Description**: `Demo project for Spring Boot`
   - **Package name**: `com.example.pib2`
   - **Packaging**: Jar
   - **Java**: 21

3. Agrega las siguientes dependencias:
   - Spring Web
   - Spring Data JPA
   - PostgreSQL Driver
   - Spring Boot DevTools
   - Lombok
   - Validation
   - Spring Boot Actuator
   - H2 Database (para testing)

4. Haz clic en "Generate" y descarga el proyecto
5. Extrae el archivo ZIP en tu directorio de trabajo

### Opción 2: Crear manualmente

Si prefieres crear el proyecto manualmente, sigue estos pasos:

1. Crea la estructura de directorios:
```
pi_backend2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── pib2/
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## 📄 Configuración del archivo pom.xml

El archivo `pom.xml` es el corazón de cualquier proyecto Maven. Define las dependencias, plugins y configuraciones necesarias.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Configuración del proyecto padre -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.4</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <!-- Información del proyecto -->
    <groupId>com.example</groupId>
    <artifactId>pib2</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>pib2</name>
    <description>Demo project for Spring Boot</description>
    
    <!-- Propiedades del proyecto -->
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <!-- Dependencias -->
    <dependencies>
        <!-- Spring Boot Starter Web: Para crear APIs REST -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Starter Data JPA: Para persistencia de datos -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Spring Boot Starter Validation: Para validación de datos -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- PostgreSQL Driver: Para conectar con PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- H2 Database: Base de datos en memoria para testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Spring Boot DevTools: Herramientas de desarrollo -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- Lombok: Para reducir código boilerplate -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Spring Boot Actuator: Para monitoreo y métricas -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Boot Starter Test: Para testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <!-- Plugins de construcción -->
    <build>
        <plugins>
            <!-- Plugin del compilador Maven -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            
            <!-- Plugin de Spring Boot Maven -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 🔍 Explicación de las Dependencias

### Dependencias Core de Spring Boot

1. **spring-boot-starter-web**
   - Incluye Spring MVC, Tomcat embebido, y Jackson
   - Permite crear APIs REST
   - Maneja peticiones HTTP

2. **spring-boot-starter-data-jpa**
   - Incluye Hibernate, Spring Data JPA
   - Facilita el trabajo con bases de datos
   - Proporciona repositorios automáticos

3. **spring-boot-starter-validation**
   - Incluye Bean Validation (JSR-303)
   - Permite validar datos de entrada
   - Integración con Spring MVC

### Dependencias de Base de Datos

4. **postgresql**
   - Driver JDBC para PostgreSQL
   - Permite conectar con bases de datos PostgreSQL
   - Scope `runtime` porque solo se necesita en ejecución

5. **h2**
   - Base de datos en memoria
   - Útil para testing y desarrollo
   - Scope `runtime`

### Dependencias de Desarrollo

6. **spring-boot-devtools**
   - Reinicio automático de la aplicación
   - LiveReload para el navegador
   - Configuraciones adicionales para desarrollo

7. **lombok**
   - Reduce código boilerplate
   - Genera automáticamente getters, setters, constructores
   - Mejora la legibilidad del código

### Dependencias de Monitoreo y Testing

8. **spring-boot-starter-actuator**
   - Endpoints de monitoreo (/health, /info, /metrics)
   - Información sobre el estado de la aplicación
   - Útil para producción

9. **spring-boot-starter-test**
   - Incluye JUnit, Mockito, AssertJ
   - Framework completo para testing
   - Scope `test`

## 📱 Clase Principal de la Aplicación

Crea el archivo `src/main/java/com/example/pib2/Pib2Application.java`:

```java
package com.example.pib2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Pib2Application {

    public static void main(String[] args) {
        SpringApplication.run(Pib2Application.class, args);
    }
}
```

### 🔍 Explicación de la Clase Principal

1. **@SpringBootApplication**
   - Anotación compuesta que incluye:
     - `@Configuration`: Marca la clase como fuente de configuración
     - `@EnableAutoConfiguration`: Habilita la configuración automática
     - `@ComponentScan`: Escanea componentes en el paquete actual y subpaquetes

2. **SpringApplication.run()**
   - Inicia la aplicación Spring Boot
   - Configura el contexto de Spring
   - Inicia el servidor embebido (Tomcat por defecto)

## 🏗️ Estructura Final del Proyecto

Después de la configuración inicial, tu proyecto debería tener esta estructura:

```
pi_backend2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── pib2/
│   │   │               └── Pib2Application.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── pib2/
│                       └── Pib2ApplicationTests.java
├── target/                    # Generado por Maven
├── .mvn/                     # Wrapper de Maven
├── mvnw                      # Script de Maven (Unix)
├── mvnw.cmd                  # Script de Maven (Windows)
├── pom.xml                   # Configuración de Maven
└── README.md                 # Documentación del proyecto
```

## ✅ Verificación de la Configuración

### 1. Compilar el proyecto

Ejecuta el siguiente comando en la raíz del proyecto:

```bash
./mvnw clean compile
```

**En Windows:**
```cmd
.\mvnw.cmd clean compile
```

### 2. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

**En Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

### 3. Verificar que la aplicación esté funcionando

Si todo está configurado correctamente, deberías ver en la consola:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.4)

2024-08-30 00:00:00.000  INFO 12345 --- [           main] com.example.pib2.Pib2Application        : Starting Pib2Application
2024-08-30 00:00:00.000  INFO 12345 --- [           main] com.example.pib2.Pib2Application        : Started Pib2Application in 2.345 seconds
```

La aplicación estará disponible en: `http://localhost:8080`

## 🚨 Problemas Comunes y Soluciones

### Error: "Java version not supported"
**Solución**: Verifica que tienes Java 21 instalado:
```bash
java -version
```

### Error: "Maven not found"
**Solución**: Usa el wrapper de Maven incluido:
```bash
./mvnw clean compile
```

### Error: "Port 8080 already in use"
**Solución**: Cambia el puerto en `application.properties`:
```properties
server.port=8081
```

## 📚 Conceptos Clave Aprendidos

- **Spring Boot**: Framework que simplifica el desarrollo de aplicaciones Spring
- **Maven**: Herramienta de gestión de dependencias y construcción
- **Starter Dependencies**: Dependencias preconfiguradas que incluyen todo lo necesario
- **Auto-configuration**: Configuración automática basada en las dependencias presentes
- **Embedded Server**: Servidor web incluido en la aplicación (no necesitas instalar Tomcat)

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Configurar la conexión a la base de datos
- Configurar JPA/Hibernate
- Manejar variables de entorno
- Configurar perfiles de Spring

---

[**← Anterior: Estructura del Proyecto**](00-estructura-proyecto.md) | [**Volver al Índice**](README.md) | [**Siguiente: Configuración de Base de Datos →**](02-configuracion-database.md)