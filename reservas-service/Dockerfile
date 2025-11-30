# Etapa de construcción
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8081

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
