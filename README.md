# 🎵 Eco Backend

**Eco Backend** es una aplicación Spring Boot que permite descargar música desde YouTube y procesarla automáticamente. La aplicación extrae el audio en formato MP3, gestiona metadatos y almacena las canciones en una base de datos PostgreSQL.

## 📋 Descripción

Eco Backend proporciona una API REST para:

- **Descargar música** desde YouTube usando URL
- **Procesar archivos de audio** (conversión a MP3, extracción de metadatos)
- **Gestionar una biblioteca musical** con metadatos
- **Gestionar álbumes** y sus portadas
- **Procesar listas de reproducción** de YouTube
- **Consultar artistas** y sus canciones

La aplicación utiliza herramientas como **yt-dlp** para descargar contenido y **ffmpeg** para el procesamiento de audio.

## 🛠️ Tecnologías

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC + Spring Data JPA
- **Base de datos**: PostgreSQL/MYSQL
- **Procesamiento de audio**: ffmpeg, yt-dlp
- **Lectura de metadatos MP3**: mp3agic
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
| `APP_COVER_PATH` | Ruta donde se almacenan las portadas de álbumes | `./music/covers/` |
| `DB_URL` | URL de conexión a la base de datos | `jdbc:postgresql://localhost:5432/eco` |
| `DB_USER` | Usuario de la base de datos | `eco` |
| `DB_PASS` | Contraseña de la base de datos | `eco` |
| `DB_TYPE` | Driver JDBC a utilizar | `org.postgresql.Driver` |

### Ejemplo: Ejecutar contenedor con variables de entorno

```bash
docker run -d \
  -e APP_LIBRARY_PATH=/app/music/library \
  -e APP_RAW_PATH=/app/music/raw \
  -e APP_COVER_PATH=/app/music/covers \
  -e DB_URL=jdbc:mysql://mysql:3306/eco_db \
  -e DB_USER=admin \
  -e DB_PASS=securepassword \
  -e DB_TYPE=com.mysql.cj.jdbc.Driver \
  -p 8080:8080 \
  -v /data/music/library:/app/music/library \
  -v /data/music/raw:/app/music/raw \
  -v /data/music/covers:/app/music/covers \
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
    image: jorgezarzuelo/eco-backend:latest
    restart: unless-stopped
    environment:
      APP_COVER_PATH: /app/music/covers
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
      - D:/docker/eco/data/music/covers:/app/music/covers
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
```

## 🔌 Endpoints Principales

- `GET /api/songs` - Obtener lista de canciones
- `GET /api/songs/{id}` - Obtener detalles de una canción
- `GET /api/songs/{id}/stream` - Stream MP3 por rangos (HTTP Range)
- `GET /api/songs/{id}/cover` - Obtener portada embebida del MP3
- `POST /api/download?url={youtube_url}` - Descargar canción/lista desde YouTube y procesar
- `GET /api/artists` - Obtener lista de artistas
- `GET /api/artists/{id}` - Obtener detalle de artista y canciones asociadas
- `GET /api/albums` - Obtener lista de álbumes
- `GET /api/albums/{id}` - Obtener detalle de álbum y canciones
- `GET /api/albums/{id}/cover` - Obtener portada de álbum

### DTOs principales de artistas y álbumes

- `ArtistListDto`: `id`, `name`
- `ArtistDetailDto`: `id`, `name`, `ownAlbums[]`, `collaborationSongs[]`
- `AlbumListDto`: `id`, `title`, `releaseYear`
- `AlbumDetailDto`: `id`, `title`, `releaseYear`, `artist`, `songs[]`
- `AlbumSummaryDto`: `id`, `title`, `releaseYear`, `artist`

## 📝 Configuración de la Base de Datos

La aplicación usa **Hibernate** con `ddl-auto=update`, lo que significa que las tablas se crearán y actualizarán automáticamente basándose en las entidades JPA.

### Conexión por defecto (local)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/eco
spring.datasource.username=eco
spring.datasource.password=eco
```

### Notas del modelo de artistas y canciones

- Relación `ManyToMany` entre canciones y artistas (`song_artist`).
- Cada canción guarda también un `mainArtist` para búsquedas/deduplicación rápida.
- Cada canción pertenece a un álbum (`ManyToOne` con `album`).
- Cada álbum pertenece a un artista (`ManyToOne` con `artist`) y contiene canciones (`OneToMany`).
- Detección de duplicados por `title + mainArtist`.

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
-v /ruta/local/covers:/app/music/covers
```

## 🔐 Notas de Seguridad

- En producción, **nunca uses las contraseñas por defecto**
- Configura credenciales seguras a través de variables de entorno
- Considera usar secrets en Docker Swarm o Kubernetes
- Valida las URLs de YouTube antes de procesarlas

---

**Autor**: Jorge Zarzuelo González  
**Licencia**: Verifica el archivo LICENSE
