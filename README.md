# 🎵 Eco Backend

**Eco Backend** es una aplicación Spring Boot que permite descargar música desde YouTube y procesarla automáticamente. La aplicación extrae el audio en formato MP3, gestiona metadatos y almacena las canciones en una base de datos PostgreSQL.

## 📋 Descripción

Eco Backend proporciona una API REST para:

- **Descargar música** desde YouTube usando URL
- **Procesar archivos de audio** (conversión a MP3, extracción de metadatos)
- **Gestionar una biblioteca musical** con metadatos
- **Procesar listas de reproducción** de YouTube

La aplicación utiliza herramientas como **yt-dlp** para descargar contenido y **ffmpeg** para el procesamiento de audio.

## 🛠️ Tecnologías

- **Lenguaje**: Java 21
- **Framework**: Spring Boot 4.0.5
- **Base de datos**: PostgreSQL
- **Procesamiento de audio**: ffmpeg, yt-dlp
- **Construcción**: Maven

## 🚀 Instalación y Uso

### Construir la aplicación

```bash
mvn clean package
```

### Ejecutar localmente

```bash
mvn spring-boot:run
```

### Construir imagen Docker

```bash
docker build -t eco-backend:latest .
```

## 🐳 Variables de Entorno

Cuando ejecutes el contenedor Docker, puedes configurar las siguientes variables de entorno:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `APP_LIBRARY_PATH` | Ruta donde se almacenan las canciones procesadas | `./music/library/` |
| `APP_RAW_PATH` | Ruta donde se descargan las canciones sin procesar | `./music/raw/` |
| `DB_URL` | URL de conexión a la base de datos | `jdbc:postgresql://localhost:5432/eco` |
| `DB_USER` | Usuario de la base de datos | `eco` |
| `DB_PASS` | Contraseña de la base de datos | `eco` |
| `DB_TYPE` | Driver JDBC a utilizar | `org.postgresql.Driver` |

### Ejemplo: Ejecutar contenedor con variables de entorno

```bash
docker run -d \
  -e APP_LIBRARY_PATH=/app/music/library \
  -e APP_RAW_PATH=/app/music/raw \
  -e DB_URL=jdbc:postgresql://postgres:5432/eco_db \
  -e DB_USER=admin \
  -e DB_PASS=securepassword \
  -e DB_TYPE=org.postgresql.Driver \
  -p 8080:8080 \
  -v /data/music/library:/app/music/library \
  -v /data/music/raw:/app/music/raw \
  eco-backend:latest
```

### Ejemplo: Docker Compose (PostgreSQL + backend)

```yaml
services:
  db:
    image: postgres:16-alpine
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U eco -d eco"]
      interval: 5s
      timeout: 5s
      retries: 5
    environment:
      POSTGRES_USER: eco
      POSTGRES_PASSWORD: eco
      POSTGRES_DB: eco
    ports:
      - "5432:5432"
    volumes:
      - D:/docker/eco/data/postgres:/var/lib/postgresql/data

  eco-backend:
    image: eco-backend:latest
    restart: unless-stopped
    environment:
      DB_URL: jdbc:postgresql://db:5432/eco
      DB_USER: eco
      DB_PASS: eco
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    volumes:
      - D:/docker/eco/data/music/library:/app/music/library
      - D:/docker/eco/data/music/raw:/app/music/raw
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/es/jorgezarzuelo/eco_backend/
│   │   ├── controller/          # Controladores REST
│   │   ├── service/             # Servicios de negocio
│   │   ├── repository/          # Acceso a datos
│   │   ├── model/               # Entidades JPA
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── EcoBackendApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## 🔌 Endpoints Principales

- `GET /api/songs` - Obtener lista de canciones
- `POST /api/download` - Descargar canción desde YouTube
- `GET /api/songs/{id}` - Obtener detalles de una canción

## 📝 Configuración de la Base de Datos

La aplicación usa **Hibernate** con `ddl-auto=update`, lo que significa que las tablas se crearán y actualizarán automáticamente basándose en las entidades JPA.

### Conexión por defecto (local)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/eco
spring.datasource.username=eco
spring.datasource.password=eco
```

## 🔧 Requisitos del Contenedor

El Dockerfile incluye las siguientes dependencias:

- **Java 21** (Eclipse Temurin)
- **ffmpeg** - Procesamiento de audio
- **yt-dlp** - Descarga de contenido de YouTube
- **Python 3** - Dependencia de yt-dlp
- **curl** - Descargas

## 📦 Volúmenes Docker

Es recomendable montar volúmenes para persistencia:

```bash
-v /ruta/local/library:/app/music/library
-v /ruta/local/raw:/app/music/raw
```

## 🔐 Notas de Seguridad

- En producción, **nunca uses las contraseñas por defecto**
- Configura credenciales seguras a través de variables de entorno
- Considera usar secrets en Docker Swarm o Kubernetes
- Valida las URLs de YouTube antes de procesarlas

---

**Autor**: Jorge Zarzuelo González  
**Licencia**: Verifica el archivo LICENSE
