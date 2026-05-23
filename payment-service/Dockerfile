# Multi-stage build for Payment Service
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent POM and shared-common module first for better caching
COPY pom.xml .
COPY shared-common ./shared-common

# Copy payment-service source
COPY payment-service/pom.xml ./payment-service/
COPY payment-service/src ./payment-service/src

# Build the application
RUN mvn clean package -pl payment-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/payment-service/target/*.jar app.jar

# Expose the service port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
