# 🔐 Auth Service

## 📋 Description

Le Auth Service est responsable de l'authentification des utilisateurs, de la génération de tokens JWT et de la gestion des comptes utilisateurs dans l'architecture micro-services.

## 🎯 Fonctionnalités

- ✅ Authentification par email/mot de passe
- ✅ Génération de tokens JWT
- ✅ Enregistrement de nouveaux utilisateurs
- ✅ Validation de tokens
- ✅ Hashage sécurisé des mots de passe (BCrypt)
- ✅ Gestion des rôles (USER, ADMIN)
- ✅ Documentation Swagger/OpenAPI

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL 13+
- Discovery Service (Eureka) en cours d'exécution

### Installation

1. **Créer la base de données**

```sql
CREATE DATABASE auth_db;
```

2. **Configuration**

Vérifier/modifier `src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres
```

3. **Build et lancement**

```bash
# Build
mvn clean package

# Lancement
mvn spring-boot:run

# Ou via JAR
java -jar target/auth-service-1.0.0.jar
```

### Vérification

- **Health check** : http://localhost:8081/actuator/health
- **Swagger UI** : http://localhost:8081/swagger-ui.html
- **API Docs** : http://localhost:8081/api-docs

## 📊 Endpoints

### Authentification

#### POST /auth/login
Authentifie un utilisateur et retourne un token JWT.

**Request:**
```json
{
  "email": "admin@microservices.com",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Connexion réussie",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "email": "admin@microservices.com",
    "role": "ADMIN",
    "expiresIn": 86400000
  }
}
```

#### POST /auth/register
Enregistre un nouvel utilisateur.

**Request:**
```json
{
  "email": "newuser@example.com",
  "password": "Password123",
  "confirmPassword": "Password123"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Utilisateur enregistré avec succès",
  "data": {
    "userId": "uuid-here",
    "email": "newuser@example.com",
    "role": "USER",
    "enabled": true,
    "message": "Utilisateur enregistré avec succès"
  }
}
```

#### GET /auth/validate
Valide un token JWT.

**Request:**
```
GET /auth/validate?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token valide",
  "data": true
}
```

## 🗄️ Base de Données

### Table: users

| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID | Identifiant unique |
| email | VARCHAR(255) | Email (unique) |
| password | VARCHAR | Mot de passe hashé (BCrypt) |
| role | VARCHAR(20) | Rôle (USER, ADMIN) |
| enabled | BOOLEAN | Compte activé |
| created_at | TIMESTAMP | Date de création |
| updated_at | TIMESTAMP | Date de modification |

### Utilisateurs de Test

| Email | Mot de passe | Rôle | Enabled |
|-------|--------------|------|---------|
| admin@microservices.com | admin123 | ADMIN | ✅ |
| user@microservices.com | user123 | USER | ✅ |
| disabled@microservices.com | disabled123 | USER | ❌ |

## 🔒 Sécurité

### JWT Configuration

- **Secret** : Clé de 256 bits (configurable via `jwt.secret`)
- **Expiration** : 24 heures (configurable via `jwt.expiration`)
- **Algorithme** : HS256

### Password Encoding

- **Algorithme** : BCrypt
- **Strength** : 10 rounds

### Claims JWT

Le token contient les claims suivants :
```json
{
  "sub": "user-id",
  "userId": "uuid",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

## 🧪 Tests

### Lancer les tests

```bash
# Tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=AuthServiceTest

# Avec couverture
mvn clean test jacoco:report
```

### Tests Disponibles

- ✅ Tests unitaires du service
- ✅ Tests d'intégration du contrôleur
- ✅ Tests de validation
- ✅ Tests de sécurité

## 📈 Monitoring

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé du service |
| `/actuator/info` | Informations sur le service |
| `/actuator/metrics` | Métriques de performance |
| `/actuator/env` | Variables d'environnement |
| `/actuator/loggers` | Configuration des logs |

### Métriques Personnalisées

```bash
# Nombre d'authentifications réussies
curl http://localhost:8081/actuator/metrics/auth.login.success

# Nombre d'enregistrements
curl http://localhost:8081/actuator/metrics/auth.register.total
```

## 🔧 Configuration

### Variables d'Environnement

```bash
# Base de données
DATABASE_URL=jdbc:postgresql://localhost:5432/auth_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Profil
SPRING_PROFILES_ACTIVE=prod
```

### Profils Spring

- **dev** : Développement (logs debug, SQL visible)
- **prod** : Production (logs minimaux, sécurité renforcée)
- **test** : Tests (base H2 en mémoire)

## 🐛 Résolution de Problèmes

### Erreur de connexion à PostgreSQL

```bash
# Vérifier que PostgreSQL est démarré
sudo systemctl status postgresql

# Vérifier la base de données
psql -U postgres -d auth_db -c "\dt"
```

### Port 8081 déjà utilisé

```bash
# Trouver le processus
lsof -i :8081

# Tuer le processus
kill -9 <PID>
```

### Erreur JWT Invalid

- Vérifier que le secret JWT est identique dans Auth Service et Gateway
- Vérifier que le token n'est pas expiré
- Vérifier le format du header : `Authorization: Bearer <token>`

## 📚 Documentation

- **Swagger UI** : http://localhost:8081/swagger-ui.html
- **API Docs JSON** : http://localhost:8081/api-docs
- **Eureka Dashboard** : http://localhost:8761

## 🔄 Intégration avec les Autres Services

### Via API Gateway

Tous les endpoints doivent être accessibles via la Gateway :

```bash
# Login via Gateway
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}'
```

### Validation de Token

Les autres services peuvent valider un token en appelant :

```bash
GET http://localhost:8081/auth/validate?token=<jwt-token>
```

Ou en utilisant le même secret JWT pour validation locale.

## 🚀 Déploiement

### Docker

```bash
# Build image
docker build -t auth-service:1.0.0 .

# Run container
docker run -d \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/auth_db \
  auth-service:1.0.0
```

### Docker Compose

Voir le fichier `docker-compose.yml` à la racine du projet.

## 👥 Auteurs

Microservices Team

## 📄 Licence

Copyright © 2024 - Tous droits réservés