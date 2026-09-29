FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S insurflow && adduser -S insurflow -G insurflow
WORKDIR /app
COPY --from=build /workspace/target/insurflow-api-1.0.0.jar app.jar
USER insurflow
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
