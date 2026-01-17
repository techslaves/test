# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy only the pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application using a minimal JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR file from the build stage
COPY --from=build /app/target/git-server-1.0-SNAPSHOT.jar app.jar

# Change ownership of the application files to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]