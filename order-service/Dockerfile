# Multi-stage build for Order Service
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent POM and shared-common module first for better caching
COPY pom.xml .
COPY shared-common ./shared-common

# Copy order-service source
COPY order-service/pom.xml ./order-service/
COPY order-service/src ./order-service/src

# Build the application
RUN mvn clean package -pl order-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/order-service/target/*.jar app.jar

# Expose the service port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
