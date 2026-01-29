# 🚀 Guide de Déploiement - Discovery Service

## 📋 Table des Matières

1. [Déploiement Local](#déploiement-local)
2. [Déploiement avec Docker](#déploiement-avec-docker)
3. [Déploiement en Production](#déploiement-en-production)
4. [Vérifications Post-Déploiement](#vérifications-post-déploiement)
5. [Monitoring](#monitoring)
6. [Troubleshooting](#troubleshooting)

---

## 🏠 Déploiement Local

### Méthode 1: Maven

```bash
# Build
mvn clean package

# Lancement
mvn spring-boot:run

# Ou avec un profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Méthode 2: JAR

```bash
# Build
mvn clean package

# Lancement
java -jar target/discovery-service-1.0.0.jar

# Avec profil
java -jar target/discovery-service-1.0.0.jar --spring.profiles.active=prod

# Avec options JVM
java -Xms256m -Xmx512m -jar target/discovery-service-1.0.0.jar
```

### Méthode 3: Scripts

```bash
# Rendre les scripts exécutables
chmod +x start.sh stop.sh

# Démarrage
./start.sh dev

# Arrêt
./stop.sh
```

---

## 🐳 Déploiement avec Docker

### Build de l'image

```bash
# Build
docker build -t microservices-ecommerce/discovery-service:1.0.0 .

# Vérification
docker images | grep discovery-service
```

### Lancement du conteneur

```bash
# Lancement simple
docker run -d \
  --name discovery-service \
  -p 8761:8761 \
  microservices/discovery-service:1.0.0

# Avec variables d'environnement
docker run -d \
  --name discovery-service \
  -p 8761:8761 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  -v $(pwd)/logs:/app/logs \
  microservices-ecommerce/discovery-service:1.0.0

# Voir les logs
docker logs -f discovery-service

# Arrêt
docker stop discovery-service

# Suppression
docker rm discovery-service
```

### Docker Compose

```bash
# Lancement
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêt
docker-compose down

# Rebuild et relancement
docker-compose up -d --build
```

---

## 🏭 Déploiement en Production

### Configuration Pré-Production

1. **Sécuriser Eureka avec authentification**

Décommenter dans `SecurityConfig.java` :

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

2. **Configurer les credentials**

Dans `application-prod.yml` :

```yaml
spring:
  security:
    user:
      name: admin
      password: ${EUREKA_PASSWORD:changeme}
```

3. **Mettre à jour les clients**

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://admin:${EUREKA_PASSWORD}@eureka-server:8761/eureka/
```

### Variables d'Environnement Production

```bash
# Profil
export SPRING_PROFILES_ACTIVE=prod

# Mot de passe Eureka
export EUREKA_PASSWORD=super_secure_password

# Options JVM
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Logging
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_LEVEL_EUREKA=WARN
```

### Déploiement Kubernetes (Exemple)

`deployment.yaml` :

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: discovery-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: discovery-service
  template:
    metadata:
      labels:
        app: discovery-service
    spec:
      containers:
      - name: discovery-service
        image: microservices/discovery-service:1.0.0
        ports:
        - containerPort: 8761
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: EUREKA_PASSWORD
          valueFrom:
            secretKeyRef:
              name: eureka-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: discovery-service
spec:
  selector:
    app: discovery-service
  ports:
  - port: 8761
    targetPort: 8761
  type: LoadBalancer
```

---

## ✅ Vérifications Post-Déploiement

### 1. Health Check

```bash
curl http://localhost:8761/actuator/health
```

Réponse attendue :
```json
{"status":"UP"}
```

### 2. Dashboard Eureka

Accéder à http://localhost:8761

Vérifier :
- ✅ Dashboard accessible
- ✅ Aucune erreur affichée
- ✅ Instances registered = 0 (avant enregistrement des services)

### 3. Endpoints Actuator

```bash
# Info
curl http://localhost:8761/actuator/info

# Métriques
curl http://localhost:8761/actuator/metrics

# Environnement
curl http://localhost:8761/actuator/env
```

### 4. Logs

```bash
# Vérifier les logs
tail -f logs/discovery-service.log

# Ou avec Docker
docker logs -f discovery-service
```

Rechercher :
- ✅ "Started DiscoveryServiceApplication"
- ✅ Aucune erreur de type ERROR ou FATAL
- ✅ "Setting the eureka configuration.."

---

## 📊 Monitoring

### Métriques JVM

```bash
curl http://localhost:8761/actuator/metrics/jvm.memory.used
curl http://localhost:8761/actuator/metrics/jvm.threads.live
curl http://localhost:8761/actuator/metrics/process.cpu.usage
```

### Prometheus (Optionnel)

Ajouter dans `pom.xml` :

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Endpoint : http://localhost:8761/actuator/prometheus

### Grafana Dashboard

Importer le dashboard Eureka :
- Dashboard ID: 4701
- URL: https://grafana.com/grafana/dashboards/4701

---

## 🐛 Troubleshooting

### Port 8761 déjà utilisé

```bash
# Trouver le processus
lsof -i :8761

# Tuer le processus
kill -9 <PID>
```

### Service ne démarre pas

1. Vérifier Java version
```bash
java -version  # Doit être >= 17
```

2. Vérifier les logs
```bash
tail -f logs/discovery-service.log
```

3. Augmenter la mémoire
```bash
export JAVA_OPTS="-Xms512m -Xmx1024m"
```

### Dashboard inaccessible

1. Vérifier que le service est démarré
```bash
curl http://localhost:8761/actuator/health
```

2. Vérifier la configuration de sécurité
```yaml
# Dans application.yml
spring:
  security:
    enabled: false  # Pour debug uniquement
```

### Services ne s'enregistrent pas

1. Vérifier l'URL Eureka dans les clients
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

2. Vérifier les logs des services clients

3. Attendre 30-60 secondes (délai normal)

### Performance dégradée

1. Augmenter la mémoire JVM
```bash
java -Xms1g -Xmx2g -jar app.jar
```

2. Activer le GC G1
```bash
java -XX:+UseG1GC -jar app.jar
```

3. Monitoring des ressources
```bash
# CPU et mémoire
top -p <PID>

# Threads JVM
jstack <PID>
```

---

## 📝 Checklist de Déploiement

- [ ] Java 17+ installé
- [ ] Variables d'environnement configurées
- [ ] Port 8761 disponible
- [ ] Build réussi (`mvn clean package`)
- [ ] Service démarré
- [ ] Health check OK
- [ ] Dashboard accessible
- [ ] Logs sans erreur
- [ ] Sécurité configurée (prod)
- [ ] Monitoring configuré
- [ ] Backup des logs configuré

---

## 🔗 Ressources

- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Eureka Documentation](https://github.com/Netflix/eureka/wiki)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)