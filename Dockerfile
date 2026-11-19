# Stage 1: Build with Maven and JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache Maven dependencies first
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source code and package executable JAR
COPY src ./src
RUN mvn -B clean package -DskipTests

# Stage 2: Minimal JRE 17 Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /app/target/dataset-operator-api-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]