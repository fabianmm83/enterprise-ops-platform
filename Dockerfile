# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar Maven Wrapper y pom.xml primero (cache layer)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos al wrapper
RUN chmod +x mvnw

# Descargar dependencias (aprovecha cache de Docker)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Build sin tests (los corremos en CI, no en el build de imagen)
RUN ./mvnw clean package -DskipTests

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR del stage anterior
COPY --from=build /app/target/*.jar app.jar

# Metadata
LABEL org.opencontainers.image.title="Enterprise Operations Platform"
LABEL org.opencontainers.image.description="Backend empresarial con Spring Boot 4"
LABEL org.opencontainers.image.authors="Fabian Moreno Monroy"

# Exponer puerto (Cloud Run lo sobrescribe con $PORT)
EXPOSE 8080

# Configurar JVM para contenedores
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]