# Stage 1: build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY app/pom.xml app/

RUN mvn -B dependency:go-offline -DskipTests || true

COPY . .
RUN mvn -B clean package -DskipTests

# Stage 2: run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN adduser -D -u 1000 appuser
USER appuser

COPY --from=builder /build/app/target/intensity-api.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
