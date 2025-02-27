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
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk
RUN groupadd --system spring && useradd --system --create-home --gid spring spring
RUN #apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
