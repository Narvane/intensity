# Multi-stage build for Intensity API (DT-07, DT-08)
# Build context must be the repository root (see api-ci.yml) so openapi/ is available.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/api
# Copy one path at a time — multi-source COPY flattens directory contents.
COPY api/mvnw ./mvnw
COPY api/.mvn ./.mvn
COPY api/pom.xml ./pom.xml
COPY openapi /workspace/openapi
RUN chmod +x mvnw
COPY api/src ./src
RUN ./mvnw -B -DskipTests package \
	&& test -f target/classes/static/openapi.yaml \
	&& echo "Packaged openapi.yaml OK"

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S intensity && adduser -S intensity -G intensity
USER intensity
COPY --from=build /workspace/api/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
