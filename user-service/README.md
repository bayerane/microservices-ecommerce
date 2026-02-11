# 👤 User Service

## 📋 Description

Le User Service gère les profils utilisateurs complets dans l'architecture micro-services. Il fournit un CRUD complet pour les informations personnelles des utilisateurs et s'intègre avec l'Auth Service pour la gestion des mots de passe.

## 🎯 Fonctionnalités

- ✅ CRUD complet des utilisateurs
- ✅ Gestion des profils utilisateurs
- ✅ Mise à jour des informations personnelles
- ✅ Changement de mot de passe (via Auth Service)
- ✅ Recherche d'utilisateurs (ADMIN)
- ✅ Pagination et tri
- ✅ Permissions basées sur les rôles
- ✅ Documentation Swagger/OpenAPI
- ✅ Communication avec Auth Service (Feign)

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL 13+
- Discovery Service en cours d'exécution
- API Gateway en cours d'exécution
- Auth Service en cours d'exécution (optionnel mais recommandé)

### Installation

1. **Créer la base de données**

```sql
CREATE DATABASE user_db;
```

2. **Configuration**

Vérifier `src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user_db
    username: postgres
    password: postgres
```

3. **Build et lancement**

```bash
# Build
mvn clean package

# Lancement
mvn spring-boot:run
```

### Vérification

- **Health check** : http://localhost:8082/actuator/health
- **Swagger UI** : http://localhost:8082/swagger-ui.html
- **API Docs** : http://localhost:8082/api-docs

## 📊 Endpoints

### Profil Utilisateur

#### GET /users/profile
Récupère le profil de l'utilisateur connecté.

**Headers requis:**
```
Authorization: Bearer <jwt-token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Profil récupéré avec succès",
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "phone": "+33612345678",
    "address": "123 Rue Example",
    "city": "Paris",
    "country": "France",
    "postalCode": "75001",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-15T14:30:00"
  }
}
```

### Gestion Utilisateurs

#### POST /users
Crée un nouvel utilisateur (ADMIN ou self-registration).

**Request:**
```json
{
  "email": "newuser@example.com",
  "password": "Password123",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+33687654321",
  "address": "456 Avenue Test",
  "city": "Lyon",
  "country": "France",
  "postalCode": "69001"
}
```

#### GET /users/{id}
Récupère un utilisateur par ID (propriétaire ou ADMIN).

#### GET /users
Liste tous les utilisateurs avec pagination (ADMIN uniquement).

**Query Parameters:**
- `page` : Numéro de page (défaut: 0)
- `size` : Taille de page (défaut: 10)
- `sortBy` : Champ de tri (défaut: createdAt)
- `sortDirection` : ASC ou DESC (défaut: DESC)

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

#### GET /users/search
Recherche des utilisateurs (ADMIN uniquement).

**Query Parameters:**
- `query` : Terme de recherche (nom, prénom, email)
- `page` : Numéro de page
- `size` : Taille de page

#### PUT /users/{id}
Met à jour un utilisateur (propriétaire ou ADMIN).

**Request:**
```json
{
  "firstName": "UpdatedName",
  "city": "Marseille",
  "phone": "+33699887766"
}
```

#### PUT /users/{id}/password
Change le mot de passe (propriétaire uniquement).

**Request:**
```json
{
  "currentPassword": "OldPassword123",
  "newPassword": "NewPassword456",
  "confirmPassword": "NewPassword456"
}
```

#### DELETE /users/{id}
Supprime un utilisateur (ADMIN uniquement, pas son propre compte).

## 🔒 Permissions

| Endpoint | USER | ADMIN |
|----------|------|-------|
| GET /users/profile | ✅ Own | ✅ Own |
| GET /users/{id} | ✅ Own | ✅ All |
| GET /users | ❌ | ✅ |
| GET /users/search | ❌ | ✅ |
| POST /users | ✅ | ✅ |
| PUT /users/{id} | ✅ Own | ✅ All |
| PUT /users/{id}/password | ✅ Own | ✅ Own |
| DELETE /users/{id} | ❌ | ✅ (not self) |

## 🗄️ Base de Données

### Table: users

| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID | Identifiant unique |
| email | VARCHAR(255) | Email (unique) |
| first_name | VARCHAR(100) | Prénom |
| last_name | VARCHAR(100) | Nom |
| phone | VARCHAR(20) | Téléphone |
| address | VARCHAR(500) | Adresse |
| city | VARCHAR(100) | Ville |
| country | VARCHAR(100) | Pays |
| postal_code | VARCHAR(20) | Code postal |
| created_at | TIMESTAMP | Date de création |
| updated_at | TIMESTAMP | Date de modification |

## 🔗 Intégration avec Auth Service

Le User Service communique avec l'Auth Service via **OpenFeign** pour :

- ✅ Vérifier les credentials
- ✅ Mettre à jour les mots de passe
- ✅ Valider l'authentification

### Circuit Breaker

Un **Circuit Breaker** (Resilience4j) est configuré pour gérer les pannes temporaires de l'Auth Service :

```yaml
resilience4j:
  circuitbreaker:
    instances:
      auth-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
```

## 🔐 Contexte de Sécurité

Le service utilise les headers ajoutés par la Gateway :

```java
// Headers automatiquement ajoutés par la Gateway
X-User-Id: uuid-de-l-utilisateur
X-User-Email: email@example.com
X-User-Role: USER|ADMIN
```

Ces headers sont extraits via `SecurityContextUtil` :

```java
UUID userId = securityContext.getCurrentUserId();
String email = securityContext.getCurrentUserEmail();
Role role = securityContext.getCurrentUserRole();
boolean isAdmin = securityContext.isCurrentUserAdmin();
```

## 🚀 Démarrage Rapide

### Utilisation des scripts (Recommandé)

Des scripts automatisés sont fournis pour faciliter la gestion locale :

```bash
# Rendre les scripts exécutables
chmod +x *.sh

# Démarrer le service
./start.sh dev

# Arrêter le service proprement
./stop.sh

```

## 🐳 Docker & Déploiement

Le service est prêt pour la conteneurisation avec une image optimisée.

### Dockerfile (Build Multi-stage)

Le Dockerfile utilise une étape de build Maven suivie d'une étape d'exécution JRE Alpine pour minimiser le poids de l'image (env. 150MB).

### Docker Compose

```bash
# Lancer le service avec sa base de données et Eureka
docker-compose up -d

```

## 🧪 Tests

### Lancer les tests

```bash
# Tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=UserControllerTest
mvn test -Dtest=UserServiceTest

# Avec couverture
mvn clean test jacoco:report
```

### Scénarios de test

```bash
# 1. Créer un token via Gateway
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

# 2. Récupérer son profil
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"

# 3. Mettre à jour son profil
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"city":"Marseille"}'

# 4. Lister les utilisateurs (ADMIN)
curl http://localhost:8080/api/users?page=0&size=10 \
  -H "Authorization: Bearer $TOKEN"
```

## 📈 Monitoring

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé |
| `/actuator/info` | Informations |
| `/actuator/metrics` | Métriques |
| `/actuator/circuitbreakers` | État des circuit breakers |

### Circuit Breaker Status

```bash
curl http://localhost:8082/actuator/circuitbreakers
```

## 🐛 Résolution de Problèmes

### Port 8082 déjà utilisé

```bash
lsof -i :8082
kill -9 <PID>
```

### Erreur "X-User-Id header missing"

**Cause** : Requête directe au User Service sans passer par la Gateway.

**Solution** : Toujours accéder via la Gateway : `http://localhost:8080/api/users/...`

### Communication avec Auth Service échoue

**Vérifications** :
1. Auth Service est démarré
2. Eureka enregistre bien auth-service
3. Circuit breaker status : `curl http://localhost:8082/actuator/circuitbreakers`

### Erreur 403 Forbidden

**Causes possibles** :
1. USER essaie d'accéder à un endpoint ADMIN
2. Utilisateur essaie d'accéder au profil d'un autre utilisateur

**Solution** : Vérifier les permissions et le rôle dans le token JWT.

## 🚀 Déploiement

### Docker

```bash
docker build -t user-service:1.0.0 .

docker run -d \
  -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/user_db \
  user-service:1.0.0
```

### Via Gateway

**Important** : En production, tous les accès doivent passer par la Gateway :

```
Client -> Gateway (8080) -> User Service (8082)
```

Ne jamais exposer directement le port 8082 en production.

## 📚 Documentation

- **Swagger UI** : http://localhost:8082/swagger-ui.html
- **Via Gateway** : http://localhost:8080/api/users (avec token)
- **Eureka Dashboard** : http://localhost:8761

## 👥 Auteurs

Baye Rane

## 📄 Licence

Copyright © 2026 - Tous droits réservés