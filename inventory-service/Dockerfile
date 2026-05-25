# Multi-stage build for Inventory Service
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy parent POM
COPY pom.xml .

# Copy all module POMs (Maven needs to know about all modules)
COPY shared-common/pom.xml ./shared-common/pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml
COPY inventory-service/pom.xml ./inventory-service/pom.xml
COPY shipping-service/pom.xml ./shipping-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

# Copy shared-common source (needed as dependency)
COPY shared-common/src ./shared-common/src

# Copy inventory-service source
COPY inventory-service/src ./inventory-service/src

# Build the application
RUN mvn clean package -pl inventory-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/inventory-service/target/*.jar app.jar

# Expose the service port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
