# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle shadowJar --no-daemon

# Runtime stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# Install curl for healthchecks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/build/libs/study-tool-backend.jar app.jar

# Create non-root user
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Create placeholder for Vertex AI key file so that file bind-mount succeeds on Linux
RUN touch /app/vertex-ai-key.json

USER appuser

EXPOSE 8080
CMD ["java", "-jar", "app.jar"] 
