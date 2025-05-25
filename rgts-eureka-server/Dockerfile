# Multi-stage build for rgts-pricing-service
FROM maven:3.9.7-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .

# Copy all module POMs
COPY rgts-common/pom.xml rgts-common/
COPY rgts-cache-manager/pom.xml rgts-cache-manager/
COPY rgts-config-server/pom.xml rgts-config-server/
COPY rgts-email-service/pom.xml rgts-email-service/
COPY rgts-eureka-server/pom.xml rgts-eureka-server/
COPY rgts-gateway-service/pom.xml rgts-gateway-service/
COPY rgts-holdings-service/pom.xml rgts-holdings-service/
COPY rgts-kafka-service/pom.xml rgts-kafka-service/
COPY rgts-notification-service/pom.xml rgts-notification-service/
COPY rgts-pricing-service/pom.xml rgts-pricing-service/
COPY rgts-proto-service/pom.xml rgts-proto-service/
COPY rgts-scheduler-service/pom.xml rgts-scheduler-service/
COPY rgts-user-service/pom.xml rgts-user-service/

# Download dependencies
RUN mvn dependency:go-offline -pl rgts-eureka-server -am

# Copy sources
COPY rgts-eureka-server/src rgts-eureka-server/src

# Package the target service and dependencies
RUN mvn clean package -pl rgts-eureka-server -am -DskipTests

# Runtime stage
FROM amazoncorretto:21.0.7-alpine

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/rgts-eureka-server/target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8761

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]