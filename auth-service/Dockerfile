# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copier le parent pom (nécessaire pour la résolution des dépendances)
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# Télécharger les dépendances
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build du JAR
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="microservices-ecommerce@bayerane.com"
LABEL description="Auth Service - Gestion des identités et JWT"
LABEL version="1.0.0"

# Créer utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copier le JAR généré
COPY --from=build /app/target/auth-service-1.0.0.jar app.jar

# Créer dossier logs et donner les droits
RUN mkdir -p logs && chown -R spring:spring /app

USER spring:spring

# Port exposé par l'Auth Service
EXPOSE 8081

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV EUREKA_SERVER_URL=http://discovery-service:8761/eureka/

# Health check pour Docker
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]