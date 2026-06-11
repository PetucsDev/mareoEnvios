# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar solo el pom primero para aprovechar la caché de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar fuentes y compilar (sin tests)
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy AS runtime

# Crear usuario no-root para ejecutar la aplicación
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /app

# Copiar solo el jar generado y asignar propiedad al usuario no-root
COPY --from=builder --chown=appuser:appgroup /app/target/mareo-envios-0.0.1-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
