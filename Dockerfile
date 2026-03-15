# # Build stage with JDK 21
# FROM maven:3.9.9-eclipse-temurin-21 AS build
# WORKDIR /app

# COPY pom.xml .
# COPY mvnw .
# COPY .mvn .mvn

# # Make mvnw executable
# RUN chmod +x mvnw

# # Download dependencies
# RUN ./mvnw dependency:go-offline -B

# COPY src src

# # Package the application
# RUN ./mvnw package -DskipTests

# # Run stage with JRE 21
# FROM eclipse-temurin:21-jre-alpine
# WORKDIR /app

# COPY --from=build /app/target/*.jar app.jar

# EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "app.jar"]

# Build stage with JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests

# Run stage with JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Use shell form so $PORT is expanded at runtime
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]