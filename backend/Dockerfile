# Multi-stage build para backend Grails
# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle gradle
COPY gradlew gradlew
COPY gradle.properties gradle.properties
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Copiar código fuente
COPY grails-app grails-app
COPY src src

# Dar permisos de ejecución a gradlew
RUN chmod +x gradlew

# Construir la aplicación
RUN ./gradlew clean assemble --no-daemon

# Stage 2: Runtime
FROM openjdk:17-slim

WORKDIR /app

# Instalar dependencias necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar el JAR desde el stage de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Crear directorio para archivos payload
RUN mkdir -p /app/payload

# Exponer puerto 8080
EXPOSE 8080

# Configurar variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=production
ENV DB_HOST=172.16.1.202
ENV DB_PORT=3306
ENV DB_NAME=secah2014
ENV DB_USER=root
ENV DB_PASSWORD=C14R1741

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
