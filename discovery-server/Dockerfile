FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml ./pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY event-service/pom.xml ./event-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml
COPY ticket-service/pom.xml ./ticket-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

RUN mvn -pl discovery-server dependency:go-offline -q

COPY discovery-server/src ./discovery-server/src
RUN mvn -pl discovery-server package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S vivaeventos && adduser -S vivaeventos -G vivaeventos
USER vivaeventos
COPY --from=builder /app/discovery-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]