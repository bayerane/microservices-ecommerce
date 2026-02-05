# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# Télécharger les dépendances
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="microservices-ecommerce@bayerane.com"
LABEL description="API Gateway - Point d'entrée unique"
LABEL version="1.0.0"

# Créer utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/api-gateway-1.0.0.jar app.jar

# Créer dossier logs
RUN mkdir -p logs && chown -R spring:spring /app

USER spring:spring

# Exposer le port
EXPOSE 8080

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV EUREKA_SERVER_URL=http://discovery-service:8761/eureka/

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]