FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom padre y pom de TODOS los módulos (requerido por Maven multi-módulo)
COPY pom.xml ./pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY event-service/pom.xml ./event-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml
COPY ticket-service/pom.xml ./ticket-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml

# Descargar dependencias solo del módulo necesario
RUN mvn -pl api-gateway dependency:go-offline -q

# Copiar fuentes y compilar
COPY api-gateway/src ./api-gateway/src
RUN mvn -pl api-gateway package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S vivaeventos && adduser -S vivaeventos -G vivaeventos
USER vivaeventos
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
