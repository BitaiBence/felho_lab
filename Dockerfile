# Multi-stage Dockerfile for TeacherService (Spring Boot, Java 21)

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom and source, then build (skip tests for faster image build)
COPY pom.xml ./
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Optional JVM opts can be overridden at runtime
ENV JAVA_OPTS=""

# Install wget for docker-compose healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 3000

# Copy the built jar from the builder stage
COPY --from=build /workspace/target/*.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
