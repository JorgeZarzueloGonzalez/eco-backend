FROM eclipse-temurin:21-jdk-jammy

RUN mkdir -p /app/music/raw /app/music/library

WORKDIR /app

COPY target/eco_backend-0.0.2-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y \
    ffmpeg \
    curl \
    python3 \
    && rm -rf /var/lib/apt/lists/*

# Instalar yt-dlp (binario oficial)
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp \
    -o /usr/local/bin/yt-dlp && \
    chmod +x /usr/local/bin/yt-dlp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]