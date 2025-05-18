# Stage 1: Build the project using Maven
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

# Copy the pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the project
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the jar using a lightweight JDK
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar ./app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "app.jar"]
