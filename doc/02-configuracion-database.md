# 2. Configuración de Base de Datos y JPA

## 🎯 Objetivos

En esta sección aprenderás a:
- Configurar la conexión a PostgreSQL
- Entender las propiedades de JPA/Hibernate
- Configurar variables de entorno para seguridad
- Configurar Actuator para monitoreo
- Manejar diferentes perfiles de configuración

## 📋 Prerrequisitos

- Proyecto Spring Boot configurado (sección anterior)
- PostgreSQL instalado o acceso a una base de datos en la nube (ej: Supabase)
- Conocimientos básicos de SQL

## 🗄️ Configuración de Base de Datos

### Archivo application.properties

El archivo `src/main/resources/application.properties` es donde configuramos todas las propiedades de nuestra aplicación.

```properties
# Nombre de la aplicación
spring.application.name=pib2

# Configuración de la base de datos PostgreSQL (Supabase)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración de JPA/Hibernate para producción
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Habilitar endpoints de Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
management.info.env.enabled=true

# Información personalizada para /actuator/info
info.app.name=My REST API
info.app.version=1.0.0
info.app.description=A sample REST API built with Spring Boot
info.app.author.name=John Doe
info.app.author.email=john.doe@example.com
info.app.author.organization=Example Corp
```

## 🔍 Explicación Detallada de las Configuraciones

### 1. Configuración de DataSource

```properties
# URL de conexión a la base de datos
spring.datasource.url=${DB_URL}
# Usuario de la base de datos
spring.datasource.username=${DB_USERNAME}
# Contraseña de la base de datos
spring.datasource.password=${DB_PASSWORD}
# Driver JDBC para PostgreSQL
spring.datasource.driver-class-name=org.postgresql.Driver
```

**¿Por qué usar variables de entorno?**
- **Seguridad**: Las credenciales no se almacenan en el código
- **Flexibilidad**: Diferentes configuraciones para diferentes entornos
- **Mejores prácticas**: Siguiendo el patrón 12-factor app

### 2. Configuración de JPA/Hibernate

```properties
# Dialecto específico para PostgreSQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```
- **Dialecto**: Le dice a Hibernate cómo generar SQL específico para PostgreSQL
- **Optimización**: Usa características específicas de PostgreSQL

```properties
# Estrategia de creación/actualización de esquema
spring.jpa.hibernate.ddl-auto=update
```

**Opciones disponibles:**
- `none`: No hace nada (recomendado para producción)
- `validate`: Solo valida el esquema
- `update`: Actualiza el esquema si es necesario
- `create`: Crea el esquema, destruyendo datos previos
- `create-drop`: Crea al inicio, destruye al final

```properties
# Mostrar consultas SQL en la consola
spring.jpa.show-sql=true
# Formatear las consultas SQL para mejor legibilidad
spring.jpa.properties.hibernate.format_sql=true
```
- **Desarrollo**: Útil para debugging y aprendizaje
- **Producción**: Desactivar para mejor rendimiento

```properties
# Diferir la inicialización del DataSource
spring.jpa.defer-datasource-initialization=true
```
- **Propósito**: Permite que Hibernate cree las tablas antes de ejecutar scripts SQL
- **Útil**: Cuando tienes archivos `data.sql` para datos iniciales

### 3. Configuración de Actuator

```properties
# Exponer endpoints específicos
management.endpoints.web.exposure.include=health,info
# Mostrar detalles completos del health check
management.endpoint.health.show-details=always
# Habilitar información de variables de entorno
management.info.env.enabled=true
```

**Endpoints disponibles:**
- `/actuator/health`: Estado de la aplicación y dependencias
- `/actuator/info`: Información sobre la aplicación
- `/actuator/metrics`: Métricas de la aplicación
- `/actuator/env`: Variables de entorno

### 4. Información Personalizada

```properties
info.app.name=My REST API
info.app.version=1.0.0
info.app.description=A sample REST API built with Spring Boot
info.app.author.name=John Doe
info.app.author.email=john.doe@example.com
info.app.author.organization=Example Corp
```

Esta información aparecerá en el endpoint `/actuator/info`.

## 🌍 Variables de Entorno

### Configuración Local (Desarrollo)

Crea un archivo `.env` en la raíz del proyecto (NO lo subas a Git):

```bash
# .env
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui
```

### Configuración para Supabase

Si usas Supabase, tus variables serían:

```bash
# .env
DB_URL=jdbc:postgresql://db.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_de_supabase
```

### Configuración en el Sistema

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/inventory_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="tu_password"
```

**Linux/Mac:**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/inventory_db"
export DB_USERNAME="postgres"
export DB_PASSWORD="tu_password"
```

### Configuración en IDE

**IntelliJ IDEA:**
1. Ve a Run → Edit Configurations
2. En Environment Variables, agrega:
   - `DB_URL=jdbc:postgresql://localhost:5432/inventory_db`
   - `DB_USERNAME=postgres`
   - `DB_PASSWORD=tu_password`

**VS Code:**
Crea un archivo `.vscode/launch.json`:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch Pib2Application",
            "request": "launch",
            "mainClass": "com.example.pib2.Pib2Application",
            "env": {
                "DB_URL": "jdbc:postgresql://localhost:5432/inventory_db",
                "DB_USERNAME": "postgres",
                "DB_PASSWORD": "tu_password"
            }
        }
    ]
}
```

## 🏗️ Configuración de PostgreSQL Local

### Instalación de PostgreSQL

**Windows:**
1. Descarga PostgreSQL desde [postgresql.org](https://www.postgresql.org/download/)
2. Ejecuta el instalador
3. Configura la contraseña para el usuario `postgres`
4. Anota el puerto (por defecto 5432)

**Mac (con Homebrew):**
```bash
brew install postgresql
brew services start postgresql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### Creación de la Base de Datos

```sql
-- Conectar como usuario postgres
psql -U postgres

-- Crear la base de datos
CREATE DATABASE inventory_db;

-- Crear un usuario específico (opcional)
CREATE USER inventory_user WITH PASSWORD 'tu_password';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;

-- Salir
\q
```

## 📊 Perfiles de Spring

### application-dev.properties (Desarrollo)

```properties
# Configuración para desarrollo
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Base de datos H2 en memoria para desarrollo rápido
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

### application-prod.properties (Producción)

```properties
# Configuración para producción
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
logging.level.org.hibernate.SQL=WARN

# Configuración de pool de conexiones
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

### Activar Perfiles

**En application.properties:**
```properties
spring.profiles.active=dev
```

**Como variable de entorno:**
```bash
SPRING_PROFILES_ACTIVE=prod
```

**Al ejecutar la aplicación:**
```bash
java -jar app.jar --spring.profiles.active=prod
```

## ✅ Verificación de la Configuración

### 1. Verificar Conexión a la Base de Datos

Ejecuta la aplicación:

```bash
./mvnw spring-boot:run
```

Busca en los logs:

```
2024-08-30 10:00:00.000  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2024-08-30 10:00:00.000  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
```

### 2. Verificar Endpoints de Actuator

**Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

**Información de la App:**
```bash
curl http://localhost:8080/actuator/info
```

Respuesta esperada:
```json
{
  "app": {
    "name": "My REST API",
    "version": "1.0.0",
    "description": "A sample REST API built with Spring Boot",
    "author": {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "organization": "Example Corp"
    }
  }
}
```

## 🚨 Problemas Comunes y Soluciones

### Error: "Failed to configure a DataSource"

**Causa**: Variables de entorno no configuradas

**Solución**:
1. Verifica que las variables estén configuradas:
   ```bash
   echo $DB_URL
   echo $DB_USERNAME
   echo $DB_PASSWORD
   ```

2. O configura valores por defecto:
   ```properties
   spring.datasource.url=${DB_URL:jdbc:h2:mem:testdb}
   spring.datasource.username=${DB_USERNAME:sa}
   spring.datasource.password=${DB_PASSWORD:}
   ```

### Error: "Connection refused"

**Causa**: PostgreSQL no está ejecutándose

**Solución**:
```bash
# Windows
net start postgresql-x64-13

# Linux/Mac
sudo systemctl start postgresql
# o
brew services start postgresql
```

### Error: "Authentication failed"

**Causa**: Credenciales incorrectas

**Solución**:
1. Verifica las credenciales en PostgreSQL
2. Resetea la contraseña si es necesario:
   ```sql
   ALTER USER postgres PASSWORD 'nueva_password';
   ```

### Error: "Database does not exist"

**Causa**: La base de datos no fue creada

**Solución**:
```sql
CREATE DATABASE inventory_db;
```

## 🔒 Mejores Prácticas de Seguridad

### 1. Nunca hardcodear credenciales

❌ **Malo:**
```properties
spring.datasource.password=mi_password_secreto
```

✅ **Bueno:**
```properties
spring.datasource.password=${DB_PASSWORD}
```

### 2. Usar .gitignore

Agrega al `.gitignore`:
```
.env
*.env
application-local.properties
```

### 3. Configuración por entorno

- **Desarrollo**: H2 en memoria o PostgreSQL local
- **Testing**: H2 en memoria
- **Producción**: PostgreSQL con SSL

### 4. Pool de conexiones

```properties
# Configuración del pool de conexiones
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## 📚 Conceptos Clave Aprendidos

- **DataSource**: Configuración de conexión a la base de datos
- **JPA/Hibernate**: ORM para mapeo objeto-relacional
- **Variables de entorno**: Configuración externa segura
- **Perfiles de Spring**: Configuraciones específicas por entorno
- **Actuator**: Monitoreo y métricas de la aplicación
- **Pool de conexiones**: Gestión eficiente de conexiones a BD

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Crear entidades JPA
- Definir relaciones entre entidades
- Usar anotaciones de validación
- Configurar auditoría automática

---

[**← Anterior: Configuración Inicial**](01-configuracion-inicial.md) | [**Volver al Índice**](README.md) | [**Siguiente: Entidades y Modelos →**](03-entidades-modelos.md)