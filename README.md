# 📚 Sistema de Inventario - API REST con Spring Boot

Una API REST completa desarrollada con Spring Boot para gestionar un sistema de inventario que incluye usuarios, artículos, préstamos e historial.

## 🚀 Características

- **CRUD Completo**: Operaciones Create, Read, Update, Delete para todas las entidades
- **Arquitectura en Capas**: Controladores, Servicios, Repositorios y Entidades
- **Base de Datos**: Integración con PostgreSQL usando JPA/Hibernate
- **Validaciones**: Bean Validation y validaciones de negocio personalizadas
- **Manejo de Errores**: Respuestas de error consistentes y centralizadas
- **Testing**: Endpoints de monitoreo con Spring Boot Actuator
- **Documentación**: Tutorial completo paso a paso

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 17** o superior
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

## ⚙️ Configuración del Proyecto

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd pi_backend2
```

### 2. Configurar la Base de Datos

#### Crear la Base de Datos en PostgreSQL

```sql
-- Conectarse a PostgreSQL como superusuario
psql -U postgres

-- Crear la base de datos
CREATE DATABASE inventory_system;

-- Crear usuario (opcional)
CREATE USER inventory_user WITH PASSWORD 'tu_password';
GRANT ALL PRIVILEGES ON DATABASE inventory_system TO inventory_user;
```

#### Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
# Configuración de Base de Datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inventory_system
DB_USERNAME=inventory_user
DB_PASSWORD=tu_password

# Configuración de la Aplicación
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### 3. Instalar Dependencias

```bash
# Limpiar y compilar el proyecto
./mvnw clean compile

# Instalar dependencias
./mvnw dependency:resolve
```

## 🏃‍♂️ Ejecutar la Aplicación

### Opción 1: Usando Maven Wrapper (Recomendado)

```bash
# Ejecutar en modo desarrollo
./mvnw spring-boot:run
```

### Opción 2: Usando JAR compilado

```bash
# Compilar el proyecto
./mvnw clean package -DskipTests

# Ejecutar el JAR
java -jar target/pi_backend2-0.0.1-SNAPSHOT.jar
```

### Opción 3: Desde el IDE

1. Importar el proyecto como proyecto Maven
2. Configurar las variables de entorno
3. Ejecutar la clase `PiBackend2Application.java`

## 🔍 Verificar la Instalación

### 1. Verificar que la aplicación esté ejecutándose

```bash
curl http://localhost:8080/actuator/health
```

**Respuesta esperada:**
```json
{"status":"UP"}
```

### 2. Verificar endpoints principales

```bash
# Listar usuarios
curl http://localhost:8080/api/users

# Listar artículos
curl http://localhost:8080/api/items

# Listar préstamos
curl http://localhost:8080/api/loans
```

## 📚 Endpoints Disponibles

### 👥 Usuarios (`/api/users`)
- `GET /api/users` - Listar todos los usuarios
- `GET /api/users/{id}` - Obtener usuario por ID
- `POST /api/users` - Crear nuevo usuario
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

### 📦 Artículos (`/api/items`)
- `GET /api/items` - Listar todos los artículos
- `GET /api/items/{id}` - Obtener artículo por ID
- `POST /api/items` - Crear nuevo artículo
- `PUT /api/items/{id}` - Actualizar artículo
- `DELETE /api/items/{id}` - Eliminar artículo

### 🔄 Préstamos (`/api/loans`)
- `GET /api/loans` - Listar todos los préstamos
- `GET /api/loans/{id}` - Obtener préstamo por ID
- `POST /api/loans` - Crear nuevo préstamo
- `PUT /api/loans/{id}` - Actualizar préstamo
- `DELETE /api/loans/{id}` - Eliminar préstamo

### 📊 Monitoreo (`/actuator`)
- `GET /actuator/health` - Estado de la aplicación
- `GET /actuator/info` - Información de la aplicación
- `GET /actuator/metrics` - Métricas de la aplicación

## 🧪 Probar la API

### Crear un Usuario

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "email": "juan@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### Crear un Artículo

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell",
    "description": "Laptop para desarrollo",
    "category": "ELECTRONICS",
    "quantity": 5,
    "available": true
  }'
```

### Crear un Préstamo

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "itemId": 1,
    "quantity": 1,
    "loanDate": "2024-01-15",
    "expectedReturnDate": "2024-01-30"
  }'
```

## 🛠️ Scripts de Utilidad

### Script de Pruebas (PowerShell)

Ejecuta el script incluido para probar todos los endpoints:

```powershell
.\test-endpoints.ps1
```

## 📖 Documentación Completa

Para aprender cómo se construyó este proyecto paso a paso, consulta la documentación completa en la carpeta `doc/`:

- [**Tutorial Completo**](doc/README.md) - Guía paso a paso
- [Configuración Inicial](doc/01-configuracion-inicial.md)
- [Base de Datos y JPA](doc/02-configuracion-database.md)
- [Entidades y Modelos](doc/03-entidades-modelos.md)
- [DTOs y Mapeo](doc/04-dtos-mapeo.md)
- [Repositorios](doc/05-repositorios-acceso-datos.md)
- [Servicios](doc/06-servicios-logica-negocio.md)
- [Controladores REST](doc/07-controladores-rest.md)
- [Testing y Actuator](doc/08-testing-actuator.md)
- [Validaciones y Errores](doc/09-validacion-manejo-errores.md)

## 🐛 Solución de Problemas

### Error de Conexión a Base de Datos

```
Caused by: org.postgresql.util.PSQLException: Connection refused
```

**Solución:**
1. Verificar que PostgreSQL esté ejecutándose
2. Comprobar las credenciales en el archivo `.env`
3. Verificar que la base de datos existe

### Puerto ya en uso

```
Port 8080 was already in use
```

**Solución:**
1. Cambiar el puerto en `.env`: `SERVER_PORT=8081`
2. O terminar el proceso que usa el puerto 8080

### Error de compilación

```
Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Solución:**
1. Verificar que tienes Java 17+: `java -version`
2. Limpiar y recompilar: `./mvnw clean compile`

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

Desarrollado como proyecto educativo para aprender Spring Boot y desarrollo de APIs REST.

---

**¿Necesitas ayuda?** Consulta la [documentación completa](doc/README.md) o abre un issue en el repositorio.

