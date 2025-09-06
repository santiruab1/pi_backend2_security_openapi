# 8. Spring Boot Actuator y Monitoreo

## 🎯 Objetivos

En esta sección aprenderás a:
- Configurar y usar Spring Boot Actuator
- Implementar endpoints de monitoreo
- Crear Health Indicators personalizados
- Configurar métricas de aplicación
- Monitorear el estado de la aplicación

## 📋 Prerrequisitos

- Controladores REST implementados
- Servicios y repositorios creados
- Aplicación Spring Boot funcionando
- Conocimientos básicos de monitoreo

## 🏥 Spring Boot Actuator

### ¿Qué es Spring Boot Actuator?

Spring Boot Actuator proporciona funcionalidades listas para producción que ayudan a monitorear y gestionar tu aplicación. Incluye endpoints para verificar el estado de la aplicación, métricas, información del entorno y más.

### Configuración de Actuator

En `application.properties` ya tenemos:

```properties
# Habilitar endpoints de Actuator
management.endpoints.web.exposure.include=health,info,metrics
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

### Endpoints Principales de Actuator

| Endpoint | Descripción | Ejemplo de Uso |
|----------|-------------|----------------|
| `/actuator/health` | Estado de la aplicación | Monitoreo de salud |
| `/actuator/info` | Información de la app | Versión, autor, etc. |
| `/actuator/metrics` | Métricas de rendimiento | CPU, memoria, requests |
| `/actuator/env` | Variables de entorno | Configuración actual |
| `/actuator/loggers` | Configuración de logs | Cambiar niveles de log |
| `/actuator/beans` | Beans de Spring | Debugging de contexto |

### Configuración Avanzada de Endpoints

```properties
# Exponer todos los endpoints (usar con cuidado en producción)
management.endpoints.web.exposure.include=*

# Excluir endpoints específicos
management.endpoints.web.exposure.exclude=env,beans

# Cambiar el path base de actuator
management.endpoints.web.base-path=/management

# Configurar puerto diferente para actuator
management.server.port=9090

# Configurar seguridad para endpoints
management.endpoint.health.roles=ADMIN
management.endpoint.info.roles=USER,ADMIN
```

## 🔍 Health Indicators

### Health Indicators Integrados

Spring Boot incluye varios health indicators automáticos:

- **DataSourceHealthIndicator**: Estado de la base de datos
- **DiskSpaceHealthIndicator**: Espacio en disco
- **RedisHealthIndicator**: Estado de Redis (si está configurado)
- **MailHealthIndicator**: Estado del servidor de correo

### Health Indicators Personalizados

Crea `src/main/java/com/example/pib2/health/DatabaseHealthIndicator.java`:

```java
package com.example.pib2.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection successful")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection failed")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

### Health Indicator para Servicios Externos

Crea `src/main/java/com/example/pib2/health/ExternalServiceHealthIndicator.java`:

```java
package com.example.pib2.health;

import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String externalServiceUrl = "https://api.external-service.com/health";

    @Override
    public Health health() {
        try {
            // Intentar conectar al servicio externo
            String response = restTemplate.getForObject(externalServiceUrl, String.class);
            
            return Health.up()
                    .withDetail("service", "External API")
                    .withDetail("url", externalServiceUrl)
                    .withDetail("response", response)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "External API")
                    .withDetail("url", externalServiceUrl)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

## 📊 Métricas Personalizadas

### Configuración de Métricas

Crea `src/main/java/com/example/pib2/metrics/CustomMetrics.java`:

```java
package com.example.pib2.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

    private final Counter userCreationCounter;
    private final Counter loanCreationCounter;
    private final Timer requestTimer;

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.userCreationCounter = Counter.builder("users.created")
                .description("Number of users created")
                .register(meterRegistry);
        
        this.loanCreationCounter = Counter.builder("loans.created")
                .description("Number of loans created")
                .register(meterRegistry);
        
        this.requestTimer = Timer.builder("api.requests")
                .description("API request duration")
                .register(meterRegistry);
    }

    public void incrementUserCreation() {
        userCreationCounter.increment();
    }

    public void incrementLoanCreation() {
        loanCreationCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(requestTimer);
    }
}
```

### Uso de Métricas en Servicios

Modifica `src/main/java/com/example/pib2/service/UserService.java`:

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomMetrics customMetrics;

    public User save(User user) {
        User savedUser = userRepository.save(user);
        customMetrics.incrementUserCreation(); // Incrementar métrica
        return savedUser;
    }
    
    // ... resto de métodos
}
```

## 🔧 Configuración de Info Endpoint

### Información Estática

En `application.properties`:

```properties
# Información básica de la aplicación
info.app.name=@project.name@
info.app.version=@project.version@
info.app.description=@project.description@
info.app.encoding=@project.build.sourceEncoding@
info.app.java.version=@java.version@

# Información del equipo
info.team.name=Development Team
info.team.email=dev-team@example.com
info.team.lead=John Doe

# Información del entorno
info.environment.name=development
info.environment.database=PostgreSQL
info.environment.profile=@spring.profiles.active@
```

### Información Dinámica

Crea `src/main/java/com/example/pib2/info/CustomInfoContributor.java`:

```java
package com.example.pib2.info;

import org.springframework.boot.actuator.info.Info;
import org.springframework.boot.actuator.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> customInfo = new HashMap<>();
        
        // Información de tiempo de ejecución
        customInfo.put("startup-time", LocalDateTime.now());
        customInfo.put("uptime", getUptime());
        
        // Información de la aplicación
        customInfo.put("active-profiles", getActiveProfiles());
        customInfo.put("total-memory", Runtime.getRuntime().totalMemory());
        customInfo.put("free-memory", Runtime.getRuntime().freeMemory());
        
        builder.withDetail("runtime", customInfo);
    }

    private String getUptime() {
        long uptime = System.currentTimeMillis() - 
                     java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        return String.format("%d minutes", uptime / (1000 * 60));
    }

    private String[] getActiveProfiles() {
        return org.springframework.core.env.Environment.class.isInstance(this) ? 
               new String[]{"default"} : new String[]{"development"};
    }
}
```

## 🚀 Verificación de Actuator

### 1. Iniciar la Aplicación

```bash
./mvnw spring-boot:run
```

### 2. Verificar Endpoints

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Información de la aplicación
curl http://localhost:8080/actuator/info

# Métricas disponibles
curl http://localhost:8080/actuator/metrics

# Métrica específica
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 3. Respuestas Esperadas

**Health Endpoint:**
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
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

**Info Endpoint:**
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
  },
  "runtime": {
    "startup-time": "2024-01-15T10:30:00",
    "uptime": "15 minutes",
    "total-memory": 536870912,
    "free-memory": 123456789
  }
}
```

## 🔒 Seguridad en Actuator

### Configuración de Seguridad

En `application.properties`:

```properties
# Configurar seguridad para endpoints sensibles
management.endpoint.health.show-details=when-authorized
management.endpoint.info.enabled=true
management.endpoint.metrics.enabled=true

# Configurar roles requeridos
management.endpoint.env.roles=ADMIN
management.endpoint.beans.roles=ADMIN
management.endpoint.configprops.roles=ADMIN

# Deshabilitar endpoints sensibles en producción
management.endpoint.shutdown.enabled=false
management.endpoint.restart.enabled=false
```

### Configuración con Spring Security

```java
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(requests -> 
                requests.requestMatchers(EndpointRequest.to("health", "info"))
                       .permitAll()
                       .anyRequest()
                       .hasRole("ADMIN")
            );
        return http.build();
    }
}
```

## 🚨 Problemas Comunes y Soluciones

### Error: "Actuator endpoints not accessible"

**Causa**: Endpoints no expuestos

**Solución**: Verificar configuración
```properties
management.endpoints.web.exposure.include=health,info,metrics
```

### Error: "Health shows DOWN status"

**Causa**: Problemas de conectividad

**Solución**: Verificar health indicators
```bash
curl http://localhost:8080/actuator/health
```

### Error: "Custom metrics not appearing"

**Causa**: Métricas no registradas correctamente

**Solución**: Verificar registro en MeterRegistry
```java
@Component
public class MetricsConfig {
    public MetricsConfig(MeterRegistry registry) {
        // Registrar métricas aquí
    }
}
```

## 🎨 Mejores Prácticas

### 1. Seguridad en Producción

```properties
# Solo exponer endpoints necesarios
management.endpoints.web.exposure.include=health,info,metrics

# Usar puerto diferente para actuator
management.server.port=9090

# Configurar autenticación
management.endpoint.health.show-details=when-authorized
```

### 2. Monitoreo Proactivo

```java
@Component
public class HealthMonitor {
    
    @EventListener
    public void handleHealthChange(HealthChangedEvent event) {
        if (event.getStatus() == Status.DOWN) {
            // Enviar alerta
            alertService.sendAlert("Application health is DOWN");
        }
    }
}
```

### 3. Métricas Significativas

```java
// Métricas de negocio
Counter.builder("business.loans.approved")
       .description("Number of approved loans")
       .tag("type", "business")
       .register(meterRegistry);

// Métricas de rendimiento
Timer.builder("database.query.time")
     .description("Database query execution time")
     .register(meterRegistry);
```

## 📚 Conceptos Clave Aprendidos

- **Spring Boot Actuator**: Framework de monitoreo y gestión
- **Health Indicators**: Verificadores de estado de componentes
- **Métricas**: Mediciones de rendimiento y uso
- **Info Endpoint**: Información de la aplicación
- **Custom Metrics**: Métricas personalizadas de negocio
- **Security**: Protección de endpoints sensibles
- **Monitoring**: Supervisión proactiva de aplicaciones

## 🎯 Próximos Pasos

En la siguiente sección aprenderás a:
- Implementar validación de datos con Bean Validation
- Crear manejo centralizado de errores
- Configurar excepciones personalizadas
- Implementar validaciones de negocio
- Manejar errores de forma consistente

---

[**← Anterior: Controladores REST**](07-controladores-rest.md) | [**Volver al Índice**](README.md) | [**Siguiente: Validación y Manejo de Errores →**](09-validacion-manejo-errores.md)