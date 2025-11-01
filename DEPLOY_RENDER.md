# Guía de Deployment en Render con Docker

Esta guía te ayudará a desplegar tu aplicación Spring Boot en Render usando Docker.

## Prerrequisitos

1. Cuenta en [Render](https://render.com)
2. Repositorio Git (GitHub, GitLab o Bitbucket) con tu código
3. Dockerfile en la raíz del proyecto (ya incluido)

## Opción 1: Deployment usando render.yaml (Recomendado)

1. **Conecta tu repositorio a Render:**
   - Ve a tu dashboard en Render
   - Haz clic en "New +" → "Blueprint"
   - Conecta tu repositorio Git
   - Render detectará automáticamente el archivo `render.yaml`

2. **Configura las variables de entorno:**
   - Ve a tu servicio en Render
   - En la sección "Environment", configura:
     - `DB_URL`: URL de tu base de datos PostgreSQL (Render puede crear una automáticamente)
     - `DB_USERNAME`: Usuario de la base de datos
     - `DB_PASSWORD`: Contraseña de la base de datos
     - `jwt.secret`: Secret key para JWT (puede ser generado por Render)

3. **Si usas la base de datos de Render:**
   - Render creará automáticamente la base de datos PostgreSQL
   - Las variables `DB_URL`, `DB_USERNAME`, y `DB_PASSWORD` se configurarán automáticamente si conectas el servicio a la base de datos

## Opción 2: Deployment Manual

1. **Crea un nuevo Web Service:**
   - Ve a tu dashboard en Render
   - Haz clic en "New +" → "Web Service"
   - Conecta tu repositorio Git

2. **Configuración del servicio:**
   - **Name**: `pib2-backend` (o el nombre que prefieras)
   - **Environment**: `Docker`
   - **Dockerfile Path**: `./Dockerfile`
   - **Docker Context**: `.`
   - **Build Command**: (dejar vacío, Docker maneja el build)
   - **Start Command**: (dejar vacío, Dockerfile define el entrypoint)

3. **Variables de entorno:**
   Configura las siguientes variables en la sección "Environment":
   ```
   PORT=8080
   DB_URL=jdbc:postgresql://<host>:<port>/<database>
   DB_USERNAME=<tu_usuario>
   DB_PASSWORD=<tu_contraseña>
   jwt.secret=<tu_secret_key_segura>
   SPRING_PROFILES_ACTIVE=prod
   ```

4. **Base de datos PostgreSQL:**
   - Si necesitas crear una base de datos en Render:
     - Ve a "New +" → "PostgreSQL"
     - Una vez creada, Render te proporcionará automáticamente las variables de entorno
   - Conecta el servicio web a la base de datos para que las variables se sincronicen

5. **Health Check:**
   - Render usará automáticamente `/actuator/health` para verificar que tu aplicación está funcionando

## Configuración adicional recomendada

### Variables de entorno en producción:

Asegúrate de configurar estas variables de entorno en Render:

- `DB_URL`: URL completa de conexión a PostgreSQL
- `DB_USERNAME`: Usuario de la base de datos
- `DB_PASSWORD`: Contraseña de la base de datos
- `jwt.secret`: Una clave secreta segura (puedes generarla con: `openssl rand -base64 64`)
- `PORT`: Render lo establece automáticamente, pero puedes usar `8080` como fallback

### Seguridad:

1. **JWT Secret**: Usa una clave secreta fuerte en producción. Render puede generar una automáticamente.
2. **Base de datos**: Asegúrate de que tu base de datos esté en la misma región que tu servicio web para reducir la latencia.

## Verificación del deployment

Una vez desplegado, puedes verificar:

1. **Health check**: `https://tu-servicio.onrender.com/actuator/health`
2. **Swagger UI**: `https://tu-servicio.onrender.com/swagger-ui.html`
3. **API Info**: `https://tu-servicio.onrender.com/actuator/info`

## Notas importantes

- **Primer deploy**: El primer build puede tomar varios minutos ya que Docker descarga todas las dependencias.
- **Auto-deploy**: Render automáticamente redesplegará cuando hagas push a la rama principal (normalmente `main` o `master`).
- **Logs**: Puedes ver los logs en tiempo real en el dashboard de Render.
- **Free tier**: El plan gratuito de Render puede hacer "spin down" después de 15 minutos de inactividad. El primer request puede tomar más tiempo.

## Troubleshooting

Si encuentras problemas:

1. **Verifica los logs**: Revisa los logs en el dashboard de Render para ver errores específicos.
2. **Variables de entorno**: Asegúrate de que todas las variables de entorno estén configuradas correctamente.
3. **Base de datos**: Verifica que la conexión a la base de datos sea correcta.
4. **Puerto**: Render establece automáticamente la variable `PORT`. Tu aplicación debe usarla (ya está configurado en el Dockerfile).

