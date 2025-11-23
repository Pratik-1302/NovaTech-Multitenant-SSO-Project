# =============================================================================
# Multi-stage Dockerfile for NovaTech SSO Management System
# =============================================================================
# Stage 1: Build the application
# =============================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# FIX: Grant execution permission to mvnw (Windows doesn't set this)
RUN chmod +x mvnw

# Download dependencies (cached layer if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# =============================================================================
# Stage 2: Run the application
# =============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/service-app-0.0.1-SNAPSHOT.jar app.jar

# Create a non-root user for security
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
USER appuser

# Expose port (Render will override with $PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/login || exit 1

# CRITICAL FIX: Transform Render's DATABASE_URL (postgresql://) to JDBC format (jdbc:postgresql://)
# Render provides: postgresql://user:pass@host/db
# Spring Boot needs: jdbc:postgresql://user:pass@host/db
ENTRYPOINT ["sh", "-c", "export SPRING_DATASOURCE_URL=$(echo $DATABASE_URL | sed 's/^postgres.*:\\/\\//jdbc:postgresql:\\/\\//') && java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
