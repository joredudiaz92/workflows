# --- BUILD STAGE ---
FROM gradle:jdk-21-and-24-alpine AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test -x check

# --- RUNTIME STAGE ---
FROM openjdk:21-ea-21-jdk-slim
WORKDIR .
# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
