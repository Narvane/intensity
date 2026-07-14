# Multi-stage build for Intensity API (DT-07, DT-08)
# Build context must be the repository root (see api-ci.yml) so openapi/ is available.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY api/mvnw api/.mvn api/pom.xml ./api/
COPY openapi ./openapi
WORKDIR /workspace/api
RUN chmod +x mvnw
COPY api/src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S intensity && adduser -S intensity -G intensity
USER intensity
COPY --from=build /workspace/api/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
