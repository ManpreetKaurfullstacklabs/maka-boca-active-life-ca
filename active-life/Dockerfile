## Use a base image with OpenJDK
#FROM eclipse-temurin:17-jdk
#
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the JAR file into the container
#COPY target/boca-spring-boot-app-instance-0.0.1-SNAPSHOT.jar app.jar
#
## Expose the port (should match the application.properties server port)
#EXPOSE 8080
#
## Run the application
# ---- Stage 1: Build the app ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---- Stage 2: Run the app ----
FROM eclipse-temurin:17-jdk
RUN groupadd --system spring && useradd --system --create-home --gid spring spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
