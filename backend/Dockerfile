# Use a Maven image to build the Spring Boot application

FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
RUN mvn dependency:go-offline dependency:resolve || true
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use an OpenJDK image for running the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/*.jar backend-app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "backend-app.jar"]
