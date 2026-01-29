# Étape 1: Build avec Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# Télécharger les dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build de l'application (skip tests pour accélérer)
RUN mvn clean package -DskipTests

# Étape 2: Image finale avec JRE uniquement
FROM eclipse-temurin:17-jre-alpine

# Informations du mainteneur
LABEL maintainer="microservices-ecommerce@bayerane.com"
LABEL description="Discovery Service - Eureka Server"
LABEL version="1.0.0"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/discovery-service-1.0.0.jar app.jar

# Créer le dossier logs
RUN mkdir -p logs && chown -R spring:spring /app

# Utiliser l'utilisateur non-root
USER spring:spring

# Exposer le port
EXPOSE 8761

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]