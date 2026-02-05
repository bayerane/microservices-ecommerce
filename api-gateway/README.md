# 🌐 API Gateway

## 📋 Description

L'API Gateway est le point d'entrée unique de l'architecture micro-services. Il route toutes les requêtes vers les services appropriés, gère l'authentification JWT et enrichit les requêtes avec le contexte utilisateur.

## 🎯 Fonctionnalités

- ✅ Point d'entrée unique (port 8080)
- ✅ Routage intelligent vers les micro-services
- ✅ Validation JWT centralisée
- ✅ Enrichissement des requêtes (headers X-User-*)
- ✅ Load balancing via Eureka
- ✅ Configuration CORS
- ✅ Logging centralisé des requêtes
- ✅ Retry automatique en cas d'échec
- ✅ Circuit breaker (Resilience4j)
- ✅ Gestion d'erreurs globale

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.8+
- Discovery Service (Eureka) en cours d'exécution
- Auth Service en cours d'exécution (recommandé)

### Installation

```bash
# Build
mvn clean package

# Lancement
mvn spring-boot:run

# Ou via JAR
java -jar target/api-gateway-1.0.0.jar
```

### Vérification

- **Health check** : http://localhost:8080/actuator/health
- **Routes** : http://localhost:8080/actuator/gateway/routes
- **Metrics** : http://localhost:8080/actuator/metrics

## 📊 Routes Configurées

### Tableau des Routes

| Path Pattern | Service Cible | Auth Required | Description |
|--------------|---------------|---------------|-------------|
| `/api/auth/login` | auth-service | ❌ | Connexion |
| `/api/auth/register` | auth-service | ❌ | Enregistrement |
| `/api/auth/**` | auth-service | ✅ | Autres endpoints auth |
| `/api/users/**` | user-service | ✅ | Gestion utilisateurs |
| `/api/orders/**` | order-service | ✅ | Gestion commandes |
| `/eureka/**` | discovery-service | ❌ | Dashboard Eureka |

### Exemples d'Utilisation

#### Login (sans authentification)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@microservices.com",
    "password": "admin123"
  }'
```

**Réponse:**
```json
{
  "success": true,
  "message": "Connexion réussie",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": "uuid-here",
    "email": "admin@microservices.com",
    "role": "ADMIN",
    "expiresIn": 86400000
  }
}
```

#### Accès à une ressource protégée

```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

La Gateway va :
1. Valider le token JWT
2. Extraire userId, email, role
3. Ajouter les headers :
   - `X-User-Id: uuid-here`
   - `X-User-Email: admin@microservices.com`
   - `X-User-Role: ADMIN`
4. Router vers `user-service`

## 🔒 Sécurité JWT

### Flux d'Authentification

```
Client
  │
  │ 1. POST /api/auth/login
  ↓
Gateway (port 8080)
  │
  │ 2. Route vers auth-service
  ↓
Auth Service (port 8081)
  │
  │ 3. Valide credentials
  │ 4. Génère JWT token
  ↓
Client (stocke le token)
  │
  │ 5. GET /api/users/profile
  │    Header: Authorization: Bearer <token>
  ↓
Gateway
  │
  │ 6. Extrait et valide JWT
  │ 7. Extrait claims (userId, email, role)
  │ 8. Ajoute headers X-User-*
  ↓
User Service (reçoit les headers)
```

### Headers Ajoutés par la Gateway

Pour chaque requête authentifiée, la Gateway ajoute automatiquement :

```
X-User-Id: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
X-User-Email: admin@microservices.com
X-User-Role: ADMIN
```

Les services downstream peuvent utiliser ces headers pour identifier l'utilisateur sans re-valider le JWT.

## 🔄 Load Balancing

La Gateway utilise **Spring Cloud LoadBalancer** avec Eureka pour distribuer les requêtes :

```yaml
# URI avec load balancing
uri: lb://user-service
```

Si plusieurs instances de `user-service` sont enregistrées dans Eureka, la Gateway distribuera automatiquement les requêtes entre elles (round-robin par défaut).

## 🌍 Configuration CORS

### Origines Autorisées

En développement :
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)
- `http://localhost:*` (tous ports localhost)

En production, restreindre aux domaines spécifiques.

### Headers Exposés

```
Authorization
X-User-Id
X-User-Email
X-User-Role
```

## 🔧 Configuration

### Variables d'Environnement

```bash
# Profil Spring
SPRING_PROFILES_ACTIVE=prod

# JWT (même secret que Auth Service)
JWT_SECRET=your-secret-key-here

# Eureka
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Timeouts
GATEWAY_CONNECT_TIMEOUT=5000
GATEWAY_RESPONSE_TIMEOUT=10s
```

### Profils Spring

- **dev** : Logs debug, timeouts longs
- **prod** : Logs minimal, timeouts courts, sécurité renforcée
- **test** : Sans Eureka, pour tests isolés

## 📈 Monitoring et Observabilité

### Endpoints Actuator

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé |
| `/actuator/info` | Informations sur l'application |
| `/actuator/metrics` | Métriques de performance |
| `/actuator/gateway/routes` | Liste des routes configurées |
| `/actuator/gateway/filters` | Liste des filtres actifs |
| `/actuator/env` | Variables d'environnement |

### Visualiser les Routes

```bash
curl http://localhost:8080/actuator/gateway/routes | jq
```

**Réponse:**
```json
[
  {
    "route_id": "auth-service-public",
    "route_definition": {
      "id": "auth-service-public",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/api/auth/login"
          }
        }
      ],
      "uri": "lb://auth-service"
    }
  }
]
```

### Métriques Importantes

```bash
# Nombre total de requêtes
curl http://localhost:8080/actuator/metrics/http.server.requests

# Latence des requêtes
curl http://localhost:8080/actuator/metrics/gateway.requests

# Circuit breaker status
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state
```

## 🧪 Tests

### Tests Unitaires

```bash
# Lancer tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=JwtAuthenticationFilterTest

# Avec couverture
mvn clean test jacoco:report
```

### Tests d'Intégration

```bash
# Test de bout en bout
# 1. Démarrer tous les services
# 2. Login via Gateway
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}'

# 3. Récupérer le token
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 4. Tester une route protégée
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

## 🐛 Résolution de Problèmes

### Port 8080 déjà utilisé

```bash
# Trouver le processus
lsof -i :8080

# Tuer le processus
kill -9 <PID>
```

### Erreur 503 Service Unavailable

**Cause** : Le service cible n'est pas enregistré dans Eureka.

**Solution** :
1. Vérifier que le service est démarré
2. Vérifier Eureka Dashboard : http://localhost:8761
3. Attendre 30-60 secondes pour l'enregistrement

### Erreur 401 Unauthorized

**Causes possibles** :
1. Token manquant ou invalide
2. Secret JWT différent entre Gateway et Auth Service
3. Token expiré

**Solution** :
```bash
# Vérifier le token
echo "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." | cut -d. -f2 | base64 -d

# Vérifier que les secrets JWT correspondent
# Gateway: src/main/resources/application.yml
# Auth Service: src/main/resources/application.yml
```

### Logs de Debug

```bash
# Activer les logs debug
curl -X POST http://localhost:8080/actuator/loggers/com.microservices.gateway \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'

# Voir les logs en temps réel
tail -f logs/api-gateway.log
```

### CORS Errors

**Symptôme** : Erreur CORS dans le navigateur

**Solution** :
1. Vérifier les origines autorisées dans `CorsConfig.java`
2. Ajouter l'origine du client :
```java
corsConfig.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:3000",
    "https://your-frontend-domain.com"
));
```

## 🚀 Déploiement

### Docker

```bash
# Build image
docker build -t api-gateway:1.0.0 .

# Run container
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=your-secret \
  -e EUREKA_SERVER_URL=http://eureka:8761/eureka/ \
  api-gateway:1.0.0
```

### Kubernetes

```yaml
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
spec:
  type: LoadBalancer
  ports:
  - port: 8080
    targetPort: 8080
  selector:
    app: api-gateway
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 2
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: api-gateway:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
```

## 📊 Performances

### Recommandations

- **Instances** : Au moins 2 instances en production
- **CPU** : 1-2 cores par instance
- **RAM** : 512MB - 1GB par instance
- **Timeout** : Connect 3-5s, Response 5-10s
- **Pool connexions** : 100-500 selon charge

### Optimisations

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          type: ELASTIC
          max-connections: 500
        connect-timeout: 3000
        response-timeout: 5s
```

## 🔗 Intégration avec les Services

### Pour les Développeurs Frontend

**URL de base** : `http://localhost:8080`

**Workflow** :
1. Login via `/api/auth/login`
2. Stocker le token (localStorage, cookie)
3. Ajouter le token dans toutes les requêtes :
```javascript
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### Pour les Développeurs Backend

**Récupération du contexte utilisateur** :

```java
@GetMapping("/profile")
public ResponseEntity<UserDTO> getProfile(
    @RequestHeader("X-User-Id") String userId,
    @RequestHeader("X-User-Role") String role
) {
    // Utiliser userId et role
    return userService.getUserById(userId);
}
```

## 📚 Documentation

- **Spring Cloud Gateway** : https://spring.io/projects/spring-cloud-gateway
- **Circuit Breaker** : https://resilience4j.readme.io/
- **Load Balancer** : https://spring.io/guides/gs/spring-cloud-loadbalancer/

## 👥 Auteurs

Baye Rane

## 📄 Licence

Copyright © 2026 - Tous droits réservés