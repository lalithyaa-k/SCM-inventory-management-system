# Multi-stage build for optimal image size
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven configuration first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Copy application properties
COPY --chown=appuser:appgroup src/main/resources/application.properties ./config/

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/api/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.location=file:./config/application.properties"]