# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .
# Si vous avez un parent pom au niveau supérieur
COPY ../pom.xml ../pom.xml 

# Cache des dépendances
RUN mvn dependency:go-offline -B

# Compilation du code source
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="microservices-ecommerce@bayerane.com"
LABEL description="User Service - Gestion des profils et CRUD utilisateurs"

# Utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Récupération du JAR compilé
COPY --from=build /app/target/user-service-1.0.0.jar app.jar

# Gestion des logs
RUN mkdir -p logs && chown -R spring:spring /app
USER spring:spring

EXPOSE 8082

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck Docker
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]