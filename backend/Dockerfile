# syntax=docker/dockerfile:1.7
FROM maven:3.9.9-eclipse-temurin-17 AS build
RUN for alias in \
      "cn_emsign_ecc_root_ca__c3,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" \
      "cn_emsign_ecc_root_ca__g3,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]" \
      "cn_emsign_root_ca__c1,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" \
      "cn_emsign_root_ca__g1,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]"; do \
      if keytool -list -cacerts -storepass changeit -alias "$alias" >/dev/null 2>&1; then \
        keytool -delete -cacerts -storepass changeit -alias "$alias"; \
      fi; \
    done \
    && ! keytool -list -cacerts -storepass changeit | grep -qi emudhra
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache --upgrade ca-certificates
RUN for alias in \
      "cn_emsign_ecc_root_ca__c3,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" \
      "cn_emsign_ecc_root_ca__g3,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]" \
      "cn_emsign_root_ca__c1,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" \
      "cn_emsign_root_ca__g1,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]"; do \
      if keytool -list -cacerts -storepass changeit -alias "$alias" >/dev/null 2>&1; then \
        keytool -delete -cacerts -storepass changeit -alias "$alias"; \
      fi; \
    done \
    && ! keytool -list -cacerts -storepass changeit | grep -qi emudhra
RUN addgroup -S insurflow && adduser -S insurflow -G insurflow
WORKDIR /app
COPY --from=build /workspace/target/insurflow-api-1.0.0.jar app.jar
USER insurflow
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
