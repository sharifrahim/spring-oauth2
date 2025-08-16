# Stage 1: Build the application with Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline
COPY src src
RUN mvn package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
