# Etapa 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notificacion-service-0.0.1-SNAPSHOT.jar app.jar

# Variables de entorno por defecto (se pueden sobrescribir)
ENV SPRING_PROFILES_ACTIVE=prod
ENV RABBITMQ_HOST=rabbitmq
ENV POSTGRES_HOST=postgres
ENV POSTGRES_DB=notificaciondb
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=123456

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
