# 🚀 Microservices Architecture - Complete Project

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13%2B-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-green)

> Architecture microservices complète avec Spring Boot, Spring Cloud, JWT Authentication, Service Discovery, API Gateway et communication inter-services.

---

## 📋 Table des Matières

- [Vue d'Ensemble](#-vue-densemble)
- [Architecture](#-architecture)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Structure du Projet](#-structure-du-projet)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Démarrage](#-démarrage)
- [Utilisation](#-utilisation)
- [API Documentation](#-api-documentation)
- [Tests](#-tests)
- [Monitoring](#-monitoring)
- [Déploiement](#-déploiement)
- [Troubleshooting](#-troubleshooting)
- [Contribuer](#-contribuer)
- [License](#-license)

---

## 🎯 Vue d'Ensemble

Cette architecture microservices complète implémente les meilleures pratiques de développement avec Spring Boot et Spring Cloud. Le projet comprend :

- ✅ **Service Discovery** (Eureka) pour l'enregistrement automatique des services
- ✅ **API Gateway** pour le routage centralisé et la sécurité
- ✅ **Auth Service** pour l'authentification JWT
- ✅ **User Service** pour la gestion des utilisateurs
- ✅ **Order Service** pour la gestion des commandes
- ✅ **Common Library** pour le code partagé
- ✅ **Communication inter-services** avec Feign Client
- ✅ **Circuit Breaker** avec Resilience4j
- ✅ **Documentation API** avec Swagger/OpenAPI

---

## 🏗️ Architecture

### Diagramme d'Architecture

```
                                  ┌─────────────────┐
                                  │   PostgreSQL    │
                                  │   (3 databases) │
                                  └────────┬────────┘
                                           │
                ┌──────────────────────────┴─────────────────────────┐
                │                                                    │
                │                                                    │
    ┌───────────▼──────────┐                           ┌─────────────▼────────┐
    │  Discovery Service   │                           │    API Gateway       │
    │   (Eureka Server)    │◄──────────────────────────│  (Port 8080)         │
    │   Port 8761          │    Service Registry       │  JWT Validation      │
    └──────────────────────┘                           │  Routing             │
                                                       └──────────┬───────────┘
                                                                  │
                    ┌─────────────────────────────────────────────┼─────────────────────┐
                    │                                             │                     │
         ┌──────────▼──────────┐                    ┌─────────────▼────────┐  ┌─────────▼──────────┐
         │   Auth Service      │                    │   User Service       │  │  Order Service     │
         │   Port 8081         │◄───────Feign───────│   Port 8082          │◄─┤  Port 8083         │
         │  - Login/Register   │                    │  - CRUD Users        │  │  - CRUD Orders     │
         │  - JWT Generation   │                    │  - User Profile      │  │  - Order Status    │
         │  - Token Validation │                    │  - Role Management   │  │  - User Orders     │
         └─────────────────────┘                    └──────────────────────┘  └────────────────────┘
```

### Flux de Communication

```
Client Request
    │
    ▼
[API Gateway :8080]
    │
    ├──► [Auth Service :8081] ──► JWT Token
    │
    ├──► [User Service :8082] ──► User Data
    │         │
    │         └──► [Auth Service] (Feign)
    │
    └──► [Order Service :8083] ──► Order Data
              │
              └──► [User Service] (Feign)
```

---

## ✨ Fonctionnalités

### 🔐 Auth Service
- ✅ Inscription utilisateur avec validation
- ✅ Authentification par email/mot de passe
- ✅ Génération de tokens JWT (HS256)
- ✅ Validation et renouvellement de tokens
- ✅ Gestion des rôles (USER, ADMIN)
- ✅ Hashage sécurisé des mots de passe (BCrypt)
- ✅ Expiration configurable des tokens (24h)

### 👤 User Service
- ✅ CRUD complet des utilisateurs
- ✅ Gestion des profils utilisateurs
- ✅ Recherche et filtrage
- ✅ Modification du mot de passe
- ✅ Permissions basées sur les rôles
- ✅ Validation des données (Bean Validation)
- ✅ Communication avec Auth Service (Feign)

### 📦 Order Service
- ✅ CRUD complet des commandes
- ✅ Gestion des statuts (PENDING → CONFIRMED → SHIPPED → DELIVERED)
- ✅ Transitions de statut validées
- ✅ Annulation de commandes
- ✅ Génération de numéros uniques (ORD-YYYYMMDD-XXXXXX)
- ✅ Statistiques (comptage, montant total)
- ✅ Recherche avancée (par utilisateur, statut, dates)
- ✅ Communication avec User Service (Feign)
- ✅ Circuit Breaker avec fallback

### 🌐 API Gateway
- ✅ Point d'entrée unique pour tous les services
- ✅ Routage intelligent vers les microservices
- ✅ Validation JWT centralisée
- ✅ Configuration CORS
- ✅ Load balancing automatique
- ✅ Retry et timeout configurables

### 🔍 Discovery Service
- ✅ Enregistrement automatique des services
- ✅ Health checks
- ✅ Dashboard Eureka
- ✅ Service discovery dynamique

---

## 🛠️ Technologies

### Backend Framework
- **Spring Boot 4.0.2** - Framework principal
- **Spring Cloud 2025.1.0** - Microservices patterns
- **Spring Security** - Sécurité et authentification
- **Spring Data JPA** - Accès aux données
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway

### Database
- **PostgreSQL 17** - Base de données principale (Production)
- **H2 Database** - Base de données en mémoire (Tests)

### Security
- **JWT (JSON Web Tokens)** - Authentification stateless
- **BCrypt** - Hashage des mots de passe
- **OAuth2 Resource Server** - Validation JWT

### Communication
- **Spring Cloud OpenFeign** - Client HTTP déclaratif
- **Resilience4j** - Circuit Breaker, Retry, Rate Limiter

### Documentation
- **SpringDoc OpenAPI 3** - Documentation API automatique
- **Swagger UI** - Interface interactive pour tester les APIs

### Monitoring & Observability
- **Spring Boot Actuator** - Health checks, metrics
- **Micrometer** - Métriques applicatives

### Build & Dependency Management
- **Maven 3.9.6** - Gestion des dépendances
- **Lombok** - Réduction du code boilerplate

### Java
- **Java 17** - LTS version

---

## 📁 Structure du Projet

```
microservices-backend/
│
├── 📄 pom.xml (parent)
├── 📘 README.md
├── 📜 .gitignore
│
├── 📦 common-lib/                    # Bibliothèque partagée
│   ├── pom.xml
│   └── src/
|       ├── main/
|       |   └── java/com/microservices/common/
│       |       ├── dto/
│       |       │   ├── ApiResponse.java
│       |       │   ├── ErrorResponse.java
│       |       │   ├── PageResponse.java
│       |       │   └── ValidationErrorResponse.jav
│       |       ├── exception/
│       |       │   ├── BusinessException.java
│       |       │   ├── ResourceNotFoundException.java
│       |       │   ├── UnauthorizedException.java
│       |       │   ├── ForbiddenException.java
│       |       │   ├── BadRequestException.java
│       |       │   └── InternalServerException.java
│       |       ├── enums/
│       |       │   ├── Role.java
│       |       │   ├── OrderStatus.java
│       |       │   └── ErrorCode.java
│       |       ├── util/
│       |       │   ├── DateUtil.java
│       |       │   ├── ValidationUtil.java
│       |       │   └── StringUtil.java
│       |       └── constant/
│       |           ├── AppConstants.java
│       |           └── SecurityConstants.java
|       └── test/
|           └── java/com/microservices/common/
|               └── util/
|                   ├── DateUtilTest.java
|                   └── ValidationUtilTest.java
│
├── 🔍 discovery-service/             # Service Discovery (Eureka)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/microservices/discovery/
│       │   │   └── DiscoveryServiceApplication.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│
├── 🌐 api-gateway/                   # API Gateway
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/microservices/gateway/
│       │   │   ├── GatewayApplication.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── CorsConfig.java
│       │   │   ├── filter/
│       │   │   │   └── JwtAuthenticationFilter.java
│       │   │   └── util/
│       │   │       └── JwtUtil.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│
├── 🔐 auth-service/                  # Service d'authentification
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/microservices/auth/
│       │   │   ├── AuthServiceApplication.java
│       │   │   ├── controller/
│       │   │   │   └── AuthController.java
│       │   │   ├── service/
│       │   │   │   ├── AuthService.java
│       │   │   │   └── impl/
│       │   │   │       └── AuthServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java
│       │   │   ├── entity/
│       │   │   │   └── User.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── LoginResponse.java
│       │   │   │   └── RegisterRequest.java
│       │   │   ├── security/
│       │   │   │   ├── JwtUtil.java
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── UserDetailsServiceImpl.java
│       │   │   ├── config/
│       │   │   │   └── OpenApiConfig.java
│       │   │   └── exception/
│       │   │       └── GlobalExceptionHandler.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── data.sql
│       └── test/
│
├── 👤 user-service/                  # Service de gestion des utilisateurs
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/microservices/user/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── controller/
│       │   │   │   └── UserController.java
│       │   │   ├── service/
│       │   │   │   ├── UserService.java
│       │   │   │   └── impl/
│       │   │   │       └── UserServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java
│       │   │   ├── entity/
│       │   │   │   └── User.java
│       │   │   ├── dto/
│       │   │   │   ├── UserDTO.java
│       │   │   │   ├── UserCreateRequest.java
│       │   │   │   ├── UserUpdateRequest.java
│       │   │   │   └── PasswordUpdateRequest.java
│       │   │   ├── mapper/
│       │   │   │   └── UserMapper.java
│       │   │   ├── security/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── SecurityContextUtil.java
│       │   │   ├── config/
│       │   │   │   ├── OpenApiConfig.java
│       │   │   │   └── FeignConfig.java
│       │   │   └── exception/
│       │   │       └── GlobalExceptionHandler.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── data.sql
│       └── test/
│
└── 📦 order-service/                 # Service de gestion des commandes
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/microservices/order/
        │   │   ├── OrderServiceApplication.java
        │   │   ├── controller/
        │   │   │   └── OrderController.java
        │   │   ├── service/
        │   │   │   ├── OrderService.java
        │   │   │   └── impl/
        │   │   │       └── OrderServiceImpl.java
        │   │   ├── repository/
        │   │   │   └── OrderRepository.java
        │   │   ├── entity/
        │   │   │   └── Order.java
        │   │   ├── dto/
        │   │   │   ├── OrderDTO.java
        │   │   │   ├── OrderCreateRequest.java
        │   │   │   ├── OrderUpdateRequest.java
        │   │   │   └── UserDTO.java
        │   │   ├── mapper/
        │   │   │   └── OrderMapper.java
        │   │   ├── client/
        │   │   │   └── UserServiceClient.java
        │   │   ├── security/
        │   │   │   ├── SecurityConfig.java
        │   │   │   └── SecurityContextUtil.java
        │   │   ├── config/
        │   │   │   ├── OpenApiConfig.java
        │   │   │   └── FeignConfig.java
        │   │   └── exception/
        │   │       └── GlobalExceptionHandler.java
        │   └── resources/
        │       ├── application.yml
        │       └── data.sql
        └── test/
```

---

## 📋 Prérequis

### Logiciels Requis

| Logiciel | Version Minimale | Commande de Vérification |
|----------|------------------|--------------------------|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| PostgreSQL | 13+ | `psql --version` |
| Git | 2.30+ | `git --version` |

### Configuration Système Recommandée

- **RAM** : Minimum 8 GB (16 GB recommandé)
- **CPU** : 4 cœurs minimum
- **Espace disque** : 5 GB minimum
- **Système d'exploitation** : Windows 10+, macOS 10.15+, Linux (Ubuntu 20.04+)

---

## 🚀 Installation

### 1. Cloner le Projet

```bash
git clone https://github.com/votre-username/microservices-backend.git
cd microservices-backend
```

### 2. Configuration PostgreSQL

#### Installation PostgreSQL

**Windows**
```bash
# Via Chocolatey
choco install postgresql

# Ou télécharger depuis
# https://www.postgresql.org/download/windows/
```

**macOS**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Création des Bases de Données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer les bases de données
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;

# Vérifier
\l

# Quitter
\q
```

#### Script SQL Complet (Optionnel)

```sql
-- Suppression des bases existantes (ATTENTION: efface toutes les données)
DROP DATABASE IF EXISTS auth_db;
DROP DATABASE IF EXISTS user_db;
DROP DATABASE IF EXISTS order_db;

-- Création des bases
CREATE DATABASE auth_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE user_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE order_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Vérification
\l
```

### 3. Configuration des Variables d'Environnement

#### Créer le fichier `.env`

Le projet inclut un fichier `.env.example` avec toutes les variables d'environnement disponibles.

**Étapes :**

1. **Copier le fichier example**
```bash
cp .env.example .env
```

2. **Mettre à jour les valeurs selon votre environnement**

Les variables principales à configurer :

```bash
# =====================================================
# Configuration PostgreSQL
# =====================================================
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=changeme  # ⚠️ Changer votre mot de passe
POSTGRES_DB_AUTH=auth_db
POSTGRES_DB_USER=user_db
POSTGRES_DB_ORDER=order_db

# =====================================================
# Configuration JWT (Auth Service)
# =====================================================
JWT_SECRET=changeme_generate_a_secure_key  # ⚠️ OBLIGATOIRE à générer
JWT_EXPIRATION=86400000      # 24 heures en millisecondes
JWT_REFRESH_EXPIRATION=604800000  # 7 jours

# =====================================================
# Ports des Services
# =====================================================
DISCOVERY_SERVICE_PORT=8761
API_GATEWAY_PORT=8080
AUTH_SERVICE_PORT=8081
USER_SERVICE_PORT=8082
ORDER_SERVICE_PORT=8083

# =====================================================
# Configuration Profil Spring
# =====================================================
SPRING_PROFILES_ACTIVE=dev
# Options: dev, test, prod
```

3. **Générer une clé JWT sécurisée (fortement recommandé)**

```bash
# Générer une clé de 32 caractères en base64
openssl rand -base64 32
```

Remplacer la valeur de `JWT_SECRET` par le résultat :

```bash
# Exemple de résultat :
# xC3mKp9jL2wQ8zAe5bR1vS4dF6gH7iU9jK0lM1nO2pP3qR4sT5u
```

4. **Vérifier la configuration**

```bash
# Vérifier que le .env est dans .gitignore
cat .gitignore | grep "\.env"
```

Vous devez voir :
```
.env
.env.local
.env.*.local
!.env.example
```

#### Fichier `.env.example` (Versionnage)

✅ **Le fichier `.env.example` DOIT être versionné** pour permettre aux nouveaux développeurs de démarrer rapidement.

```bash
# ✅ Ajouter à Git
git add .env.example
git commit -m "docs: add environment variables example"

# ❌ NE PAS ajouter le .env réel
git status  # Devrait afficher ".env" en rouge/ignoré
```

### 4. Build du Projet

```bash
# Build complet de tous les modules
mvn clean install

# Ou build dans l'ordre (si erreur)
mvn clean install -N
cd common-lib && mvn clean install && cd ..
cd discovery-service && mvn clean install && cd ..
cd api-gateway && mvn clean install && cd ..
cd auth-service && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd order-service && mvn clean install && cd ..
```

### 5. Vérification du Build

Chaque service doit avoir un JAR dans `target/` :

```bash
ls -la */target/*.jar
```

Attendu :
```
discovery-service/target/discovery-service-1.0.0.jar
api-gateway/target/api-gateway-1.0.0.jar
auth-service/target/auth-service-1.0.0.jar
user-service/target/user-service-1.0.0.jar
order-service/target/order-service-1.0.0.jar
```

---

## 🚀 Démarrage

### Ordre de Démarrage des Services

⚠️ **IMPORTANT** : Les services doivent être démarrés dans cet ordre :

1. **Discovery Service** (Eureka) - Port 8761
2. **Auth Service** - Port 8081
3. **User Service** - Port 8082
4. **Order Service** - Port 8083
5. **API Gateway** - Port 8080

### Terminal 1 : Discovery Service (Eureka)

```bash
cd discovery-service
mvn spring-boot:run
```

Attendre le message :
```
Tomcat started on port(s): 8761
```

✅ Vérifier : http://localhost:8761

### Terminal 2 : Auth Service

```bash
cd auth-service
mvn spring-boot:run
```

Attendre :
```
Tomcat started on port(s): 8081
```

✅ Vérifier dans Eureka : http://localhost:8761

### Terminal 3 : User Service

```bash
cd user-service
mvn spring-boot:run
```

Attendre :
```
Tomcat started on port(s): 8082
```

### Terminal 4 : Order Service

```bash
cd order-service
mvn spring-boot:run
```

Attendre :
```
Tomcat started on port(s): 8083
```

### Terminal 5 : API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

Attendre :
```
Tomcat started on port(s): 8080
```

### Vérification de Tous les Services

```bash
# 1. Eureka Dashboard
curl http://localhost:8761
# Devrait afficher le dashboard HTML

# 2. Health Check Auth Service
curl http://localhost:8081/actuator/health
# {"status":"UP"}

# 3. Health Check User Service
curl http://localhost:8082/actuator/health
# {"status":"UP"}

# 4. Health Check Order Service
curl http://localhost:8083/actuator/health
# {"status":"UP"}

# 5. Health Check API Gateway
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

### Arrêt des Services

```bash
# Ctrl + C dans chaque terminal
# Ou avec pkill :
pkill -f "spring-boot:run"
```

---

## 🧪 Tests

### Exécuter Tous les Tests

```bash
# Tests unitaires de tous les modules
mvn clean test

# Avec rapport de couverture
mvn clean test jacoco:report
```

### Tests par Service

```bash
# Tests Auth Service
cd auth-service
mvn clean test

# Tests User Service
cd user-service
mvn clean test

# Tests Order Service
cd order-service
mvn clean test

# Tests API Gateway
cd api-gateway
mvn clean test
```

### Tests d'Intégration

```bash
# Tests d'intégration (require les services en cours d'exécution)
mvn clean verify

# Ou avec profil spécifique
mvn clean verify -P integration-test
```

### Tests avec Maven Surefire

```bash
# Générer rapport Surefire
mvn clean test surefire-report:report

# Voir le rapport
cat target/site/surefire-report.html
```

### Exemples de Tests Unitaires

**Auth Service - AuthServiceTest.java**
```java
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    
    @Test
    void testUserRegistration() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("securePassword123");
        
        // Act
        AuthResponse response = authService.register(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getToken());
    }
    
    @Test
    void testUserLogin() {
        // Test login avec credentials valides
    }
    
    @Test
    void testInvalidToken() {
        // Test avec token invalide
    }
}
```

**User Service - UserServiceTest.java**
```java
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    
    @Test
    void testGetUserById() {
        // Arrange
        Long userId = 1L;
        
        // Act
        UserDTO user = userService.getUserById(userId);
        
        // Assert
        assertNotNull(user);
        assertEquals(userId, user.getId());
    }
    
    @Test
    void testCreateUser() {
        // Test création utilisateur
    }
    
    @Test
    void testUpdateUser() {
        // Test mise à jour utilisateur
    }
}
```

### Profils de Test

```bash
# Tests avec profil 'test' (H2 Database)
mvn clean test -P test

# Tests avec profil 'dev' (PostgreSQL)
mvn clean test -P dev
```

---

## 📚 API Documentation

### 1. Accéder à Swagger UI

Une fois tous les services lancés :

- **Auth Service** : http://localhost:8081/swagger-ui.html
- **User Service** : http://localhost:8082/swagger-ui.html
- **Order Service** : http://localhost:8083/swagger-ui.html
- **API Gateway** : http://localhost:8080/swagger-ui.html

### 2. Auth Service Endpoints

#### Inscription (Register)

```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe"
}

Response 201:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Connexion (Login)

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "userId": 1,
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

#### Valider Token

```bash
POST /api/auth/validate
Authorization: Bearer {token}

Response 200:
{
  "valid": true,
  "userId": 1,
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

### 3. User Service Endpoints

#### Obtenir Tous les Utilisateurs

```bash
GET /api/users
Authorization: Bearer {token}

Response 200:
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "createdAt": "2026-01-25T10:30:00Z"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0
}
```

#### Obtenir un Utilisateur par ID

```bash
GET /api/users/{id}
Authorization: Bearer {token}

Response 200:
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+33612345678",
  "address": "123 Rue de la Paix",
  "createdAt": "2026-01-25T10:30:00Z",
  "updatedAt": "2026-01-26T15:45:00Z"
}
```

#### Créer un Utilisateur

```bash
POST /api/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "newuser@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+33687654321",
  "address": "456 Avenue des Champs"
}

Response 201:
{
  "id": 2,
  "email": "newuser@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "createdAt": "2026-01-26T16:00:00Z"
}
```

#### Mettre à Jour un Utilisateur

```bash
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "Janet",
  "lastName": "Smith",
  "phone": "+33612345678"
}

Response 200:
{
  "id": 2,
  "email": "newuser@example.com",
  "firstName": "Janet",
  "lastName": "Smith",
  "updatedAt": "2026-01-26T16:15:00Z"
}
```

#### Supprimer un Utilisateur

```bash
DELETE /api/users/{id}
Authorization: Bearer {token}

Response 204: No Content
```

### 4. Order Service Endpoints

#### Obtenir Toutes les Commandes

```bash
GET /api/orders
Authorization: Bearer {token}

Response 200:
{
  "content": [
    {
      "id": 1,
      "orderNumber": "ORD-20260126-000001",
      "userId": 1,
      "totalAmount": 250.50,
      "status": "CONFIRMED",
      "createdAt": "2026-01-25T14:30:00Z"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0
}
```

#### Obtenir une Commande par ID

```bash
GET /api/orders/{id}
Authorization: Bearer {token}

Response 200:
{
  "id": 1,
  "orderNumber": "ORD-20260126-000001",
  "userId": 1,
  "userName": "John Doe",
  "totalAmount": 250.50,
  "status": "CONFIRMED",
  "items": [
    {
      "productId": 101,
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 250.50
    }
  ],
  "createdAt": "2026-01-25T14:30:00Z",
  "updatedAt": "2026-01-26T10:00:00Z"
}
```

#### Créer une Commande

```bash
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 101,
      "quantity": 2,
      "unitPrice": 125.25
    }
  ],
  "totalAmount": 250.50,
  "shippingAddress": "123 Rue de la Paix, Paris"
}

Response 201:
{
  "id": 2,
  "orderNumber": "ORD-20260126-000002",
  "userId": 1,
  "totalAmount": 250.50,
  "status": "PENDING",
  "createdAt": "2026-01-26T16:30:00Z"
}
```

#### Changer le Statut d'une Commande

```bash
PATCH /api/orders/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "CONFIRMED"
}

Response 200:
{
  "id": 1,
  "orderNumber": "ORD-20260126-000001",
  "status": "CONFIRMED",
  "updatedAt": "2026-01-26T16:35:00Z"
}
```

**Transitions de Statut Valides :**
- PENDING → CONFIRMED
- CONFIRMED → SHIPPED
- SHIPPED → DELIVERED
- PENDING → CANCELLED
- CONFIRMED → CANCELLED

#### Annuler une Commande

```bash
DELETE /api/orders/{id}
Authorization: Bearer {token}

Response 204: No Content
```

#### Obtenir les Commandes par Utilisateur

```bash
GET /api/orders/user/{userId}
Authorization: Bearer {token}

Response 200:
{
  "content": [
    {
      "id": 1,
      "orderNumber": "ORD-20260126-000001",
      "totalAmount": 250.50,
      "status": "DELIVERED"
    }
  ],
  "totalElements": 5,
  "totalPages": 1
}
```

### 5. Exemple de Flux Complet (curl)

```bash
# 1. Inscription
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123!",
    "firstName": "Test",
    "lastName": "User"
  }')

TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.token')
echo "Token: $TOKEN"

# 2. Obtenir le profil utilisateur
curl -s -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN" | jq

# 3. Créer une commande
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "totalAmount": 150.00,
    "items": [{"productId": 1, "quantity": 1, "unitPrice": 150.00}]
  }')

ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.id')
echo "Order ID: $ORDER_ID"

# 4. Mettre à jour le statut
curl -s -X PATCH http://localhost:8080/api/orders/$ORDER_ID/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}' | jq

# 5. Obtenir la commande
curl -s -X GET http://localhost:8080/api/orders/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN" | jq
```

### 6. Documentation OpenAPI (JSON)

```bash
# OpenAPI JSON pour Auth Service
curl http://localhost:8081/v3/api-docs

# OpenAPI JSON pour User Service
curl http://localhost:8082/v3/api-docs

# OpenAPI JSON pour Order Service
curl http://localhost:8083/v3/api-docs

# OpenAPI JSON via API Gateway
curl http://localhost:8080/v3/api-docs
```

### 7. Postman Collection

Importez cette collection dans Postman :

```json
{
  "info": {
    "name": "Microservices API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/register",
            "body": {
              "mode": "raw",
              "raw": "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}"
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/login",
            "body": {
              "mode": "raw",
              "raw": "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}"
            }
          }
        }
      ]
    },
    {
      "name": "Users",
      "item": [
        {
          "name": "Get All Users",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/api/users",
            "header": [
              {"key": "Authorization", "value": "Bearer {{token}}"}
            ]
          }
        }
      ]
    }
  ]
}
```

---

## 📊 Monitoring

### 1. Spring Boot Actuator

Spring Boot Actuator expose des endpoints pour monitorer l'application :

#### Health Check

```bash
# Vérifier la santé d'un service
curl http://localhost:8081/actuator/health

# Réponse détaillée
curl http://localhost:8081/actuator/health?details=true
```

Réponse :
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1000000000,
        "free": 500000000,
        "threshold": 10000000
      }
    },
    "livenessState": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    }
  }
}
```

#### Informations sur l'Application

```bash
# Infos générales
curl http://localhost:8081/actuator/info

# Application name, version, description
```

#### Métriques

```bash
# Lister toutes les métriques disponibles
curl http://localhost:8081/actuator/metrics

# Détails d'une métrique spécifique
curl http://localhost:8081/actuator/metrics/jvm.memory.used

# Métriques HTTP
curl http://localhost:8081/actuator/metrics/http.server.requests

# Nombre de requêtes par endpoint
curl http://localhost:8081/actuator/metrics/http.server.requests?tag=uri:/api/users
```

Réponse métriques :
```json
{
  "name": "jvm.memory.used",
  "description": "The amount of used memory",
  "baseUnit": "bytes",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 1073741824
    }
  ],
  "availableTags": [
    {
      "tag": "area",
      "values": ["heap", "nonheap"]
    }
  ]
}
```

#### Environnement

```bash
# Afficher les variables d'environnement configurées
curl http://localhost:8081/actuator/env

# Voir une propriété spécifique
curl http://localhost:8081/actuator/env/POSTGRES_HOST
```

### 2. Monitoring Dashboard Eureka

**URL** : http://localhost:8761

Affiche :
- ✅ Liste de tous les services enregistrés
- ✅ Statut UP/DOWN de chaque service
- ✅ Nombre d'instances par service
- ✅ Dernière heartbeat
- ✅ Port et URL de chaque instance

### 3. Circuit Breaker Status

Voir l'état des Circuit Breakers (Resilience4j) :

```bash
# Lister les circuit breakers
curl http://localhost:8081/actuator/circuitbreakers

# Détails d'un circuit breaker
curl http://localhost:8081/actuator/circuitbreakers/userServiceClient
```

Réponse :
```json
{
  "statuses": [
    {
      "circuitBreakerName": "userServiceClient",
      "state": "CLOSED",
      "failureRate": 0.0,
      "slowCallRate": 0.0,
      "bufferedCalls": 5,
      "failedCalls": 0,
      "slowCalls": 0,
      "successfulCalls": 5
    }
  ]
}
```

### 4. Logs en Temps Réel

#### Logs avec Tail

```bash
# Voir les logs du service Auth en temps réel
tail -f auth-service/target/*.log

# Filtrer par mot-clé
tail -f auth-service/target/*.log | grep "ERROR"

# Voir les logs des 5 derniers services en parallèle
tail -f discovery-service/target/*.log &
tail -f auth-service/target/*.log &
tail -f user-service/target/*.log &
tail -f order-service/target/*.log &
tail -f api-gateway/target/*.log &
```

#### Configuration du Logging

Modifier `application.yaml` pour chaque service :

```yaml
logging:
  level:
    root: INFO
    com.microservices: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### 5. Prometheus Metrics (Optionnel)

Pour exporter les métriques vers Prometheus :

1. **Ajouter la dépendance** :
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

2. **Activer l'endpoint** :
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
```

3. **Accéder aux métriques** :
```bash
curl http://localhost:8081/actuator/prometheus
```

4. **Configuration Prometheus** (`prometheus.yml`) :
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'microservices'
    static_configs:
      - targets: ['localhost:8081', 'localhost:8082', 'localhost:8083']
```

### 6. Dashboard Monitoring (Commandes Utiles)

```bash
# Vérifier tous les services
./start-services.sh --status

# Voir les logs en temps réel (mode background)
./start-services.sh --logs

# Vérifier les ports occupés
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :8083
lsof -i :8761

# Vérifier la consommation mémoire
ps aux | grep "spring-boot:run"

# Vérifier la base de données
psql -U postgres -c "SELECT datname, pg_size_pretty(pg_database_size(datname)) FROM pg_database WHERE datname LIKE '%_db';"
```

---

## 🔧 Troubleshooting

### ❌ Problèmes de Démarrage

#### Port Déjà Utilisé

**Erreur** :
```
Failed to bind to port xxxx
Address already in use
```

**Solutions** :
```bash
# 1. Trouver le processus qui utilise le port
lsof -i :8080
lsof -i :8081

# 2. Tuer le processus
kill -9 <PID>

# 3. Ou attendre que le port se libère (quelques secondes)
sleep 10 && ./start-services.sh

# 4. Ou modifier les ports dans .env
nano .env
# Changer les ports pour éviter les conflits
```

#### PostgreSQL Non Accessible

**Erreur** :
```
Connection to localhost:5432 refused
Cannot connect to database
```

**Solutions** :
```bash
# 1. Vérifier que PostgreSQL est démarré
sudo systemctl status postgresql

# 2. Démarrer PostgreSQL
sudo systemctl start postgresql

# 3. Vérifier la connexion
psql -U postgres -h localhost

# 4. Vérifier que les bases existent
psql -U postgres -l | grep "_db"

# 5. Recréer les bases si nécessaire
psql -U postgres << EOF
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;
EOF
```

#### Java Version Incorrecte

**Erreur** :
```
Java version does not match
Unsupported Java version
```

**Solutions** :
```bash
# 1. Vérifier la version
java -version

# 2. Définir JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
java -version

# 3. Ou installer Java 17
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Maven Build Échoue

**Erreur** :
```
BUILD FAILURE
Compilation errors
```

**Solutions** :
```bash
# 1. Nettoyer et reconstruire
mvn clean
mvn install -DskipTests

# 2. Forcer la réindexation Maven
rm -rf ~/.m2/repository
mvn clean install

# 3. Vérifier les dépendances
mvn dependency:tree

# 4. Compiler avec verbose
mvn clean install -X
```

### ❌ Problèmes de Connexion Entre Services

#### Service Non Trouvé (Service Discovery)

**Erreur** :
```
UnknownHostException: Unknown host
Service not found in Eureka
```

**Solutions** :
```bash
# 1. Vérifier que Eureka est démarré
curl http://localhost:8761

# 2. Vérifier que le service est enregistré
curl http://localhost:8761/eureka/apps

# 3. Vérifier la configuration Eureka dans application.yaml
# Doit contenir:
# eureka:
#   client:
#     serviceUrl:
#       defaultZone: http://localhost:8761/eureka/

# 4. Attendre que le service soit enregistré (peut prendre 30 sec)
sleep 30

# 5. Vérifier les logs du service
tail -f auth-service/target/*.log | grep "Registered"
```

#### Timeout Feign Client

**Erreur** :
```
feign.RetryableException: Read timed out
Connection timeout
```

**Solutions** :
```bash
# 1. Augmenter les timeouts dans .env
FEIGN_CONNECT_TIMEOUT=30000
FEIGN_READ_TIMEOUT=30000
FEIGN_WRITE_TIMEOUT=30000

# 2. Vérifier que le service cible est actif
curl http://localhost:8082/actuator/health

# 3. Vérifier les logs du service cible
tail -f user-service/target/*.log

# 4. Vérifier la réseau/firewall
ping localhost
```

### ❌ Problèmes d'Authentification JWT

#### Token Invalid/Expired

**Erreur** :
```
Unauthorized
Invalid token
Token expired
```

**Solutions** :
```bash
# 1. Générer un nouveau token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'

# 2. Vérifier que le token est correct
# Le token doit être au format: Bearer <token>
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/users

# 3. Vérifier l'expiration du token
# JWT_EXPIRATION dans .env (défaut: 86400000 = 24h)

# 4. Vérifier la clé JWT_SECRET
# La même clé doit être utilisée partout
grep JWT_SECRET .env

# 5. Générer une nouvelle clé
openssl rand -base64 32
# Mettre à jour .env et redémarrer les services
```

#### CORS Errors

**Erreur** :
```
Access-Control-Allow-Origin header missing
CORS policy blocked
```

**Solutions** :
```bash
# 1. Vérifier la configuration CORS dans application.yaml
# Doit contenir:
# cors:
#   allowed-origins: http://localhost:3000,http://localhost:4200

# 2. Vérifier les en-têtes de réponse
curl -i -X OPTIONS http://localhost:8080/api/users

# 3. Ajouter l'origin dans .env
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080
```

### ❌ Problèmes de Base de Données

#### Connection Pool Exhausted

**Erreur** :
```
HikariPool - Connection is not available
Cannot get a connection
```

**Solutions** :
```bash
# 1. Augmenter la taille du pool dans .env
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=30
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=10

# 2. Vérifier les connexions actives
psql -U postgres -c "SELECT datname, count(*) FROM pg_stat_activity GROUP BY datname;"

# 3. Tuer les connexions inactives
psql -U postgres << EOF
SELECT pg_terminate_backend(pid) FROM pg_stat_activity 
WHERE datname = 'auth_db' AND state = 'idle';
EOF

# 4. Redémarrer les services
./start-services.sh --stop
./start-services.sh
```

#### Migration Database Fail

**Erreur** :
```
Flyway validation failed
Schema version mismatch
```

**Solutions** :
```bash
# 1. Vérifier les migrations Flyway
ls -la src/main/resources/db/migration/

# 2. Réparer les migrations
psql -U postgres -d auth_db << EOF
DELETE FROM flyway_schema_history WHERE success = false;
EOF

# 3. Recréer la base de données
psql -U postgres << EOF
DROP DATABASE auth_db;
CREATE DATABASE auth_db;
EOF

# 4. Redémarrer le service (Flyway migrera automatiquement)
cd auth-service && mvn spring-boot:run
```

### ❌ Problèmes de Performance

#### High Memory Usage

**Problème** :
```
Memory: 2GB+
Slow responses
Out of memory errors
```

**Solutions** :
```bash
# 1. Vérifier la consommation mémoire
ps aux --sort=-%mem | grep spring

# 2. Limiter la mémoire JVM
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn spring-boot:run

# 3. Profiler l'application
# Ajouter jfr (Java Flight Recorder) dans pom.xml

# 4. Vérifier les logs pour les fuites mémoire
grep "OutOfMemory" auth-service/target/*.log

# 5. Redémarrer les services
./start-services.sh --stop
./start-services.sh
```

#### Slow Queries

**Problème** :
```
Slow database queries
Slow API responses (> 1s)
```

**Solutions** :
```bash
# 1. Activer le SQL logging dans .env
JPA_SHOW_SQL=true
JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# 2. Voir les logs SQL
tail -f auth-service/target/*.log | grep "SELECT\|UPDATE\|INSERT"

# 3. Activer l'analyse des requêtes lentes
# Ajouter dans application.yaml:
# spring:
#   jpa:
#     properties:
#       hibernate:
#         generate_statistics: true

# 4. Créer des indexes si nécessaire
psql -U postgres -d auth_db << EOF
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_order_user_id ON orders(user_id);
EOF

# 5. Utiliser un cache (Redis)
# Voir la section optionnelle du .env
```

### ❌ Problèmes Courants Quick Fix

| Erreur | Cause | Solution |
|--------|-------|----------|
| `Connection refused` | PostgreSQL arrêté | `sudo systemctl start postgresql` |
| `Port already in use` | Service déjà lancé | `lsof -i :8080 && kill -9 <PID>` |
| `Build failed` | Dépendance manquante | `mvn clean install -DskipTests` |
| `Service not found` | Eureka non démarré | Lancer discovery-service d'abord |
| `Invalid token` | Token expiré | Générer un nouveau token |
| `CORS error` | Origin non autorisé | Vérifier CORS_ALLOWED_ORIGINS dans .env |
| `No database connection` | BD non créée | `psql -U postgres -c "CREATE DATABASE xxx;"` |
| `OutOfMemory` | Heap trop petit | `export MAVEN_OPTS="-Xmx1024m"` |

### 📞 Déboguer Efficacement

#### Logs Structurés

```bash
# Combiner les logs de tous les services
tail -f */target/*.log 2>/dev/null | grep -E "ERROR|WARN|Exception"

# Voir les logs d'un service spécifique avec timestamps
tail -f auth-service/target/*.log | while IFS= read -r line; do
  echo "[$(date +'%H:%M:%S')] $line"
done
```

#### Requêtes HTTP de Debug

```bash
# Voir les en-têtes et le contenu complet
curl -v http://localhost:8080/api/users

# Avec authentification
curl -v -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/users

# POST avec données
curl -v -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"userId":1,"totalAmount":150}' 2>&1 | head -50
```

#### Health Check Complet

```bash
#!/bin/bash
# Script pour vérifier tous les services

echo "🔍 Vérification de la santé des services..."
echo ""

for port in 8761 8081 8082 8083 8080; do
  service_name="Service"
  case $port in
    8761) service_name="Eureka" ;;
    8081) service_name="Auth Service" ;;
    8082) service_name="User Service" ;;
    8083) service_name="Order Service" ;;
    8080) service_name="API Gateway" ;;
  esac
  
  if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
    echo "✅ $service_name (port $port) - OK"
  else
    echo "❌ $service_name (port $port) - DOWN"
  fi
done

echo ""
echo "🗄️  PostgreSQL:"
if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
  echo "✅ PostgreSQL - Connecté"
else
  echo "❌ PostgreSQL - Non accessible"
fi
```

---

## 🎯 Démarrage Rapide avec les Scripts

### Utiliser setup.sh (Installation)

```bash
# Rendre le script exécutable (première fois)
chmod +x setup.sh

# Lancer l'installation automatique
./setup.sh

# Le script va :
# ✅ Vérifier les prérequis (Java, Maven, PostgreSQL, Git)
# ✅ Configurer le fichier .env
# ✅ Initialiser PostgreSQL et créer les bases
# ✅ Builder le projet Maven
# ✅ Vérifier les artefacts générés
```

**Durée** : 5-10 minutes (vs. 15-20 minutes manuellement)

### Utiliser start-services.sh (Démarrage)

#### Mode 1 : Interactif (Par défaut - Recommandé pour le dev)

```bash
# Rendre le script exécutable (première fois)
chmod +x start-services.sh

# Lancer le script
./start-services.sh

# Résultat : 5 terminaux séparé s'ouvrent, un par service
# Parfait pour voir les logs de chaque service en direct
```

#### Mode 2 : Parallèle (Tous les services dans un terminal)

```bash
./start-services.sh --parallel

# Les logs s'affichent avec préfixes :
# [discovery-service] Tomcat started on port 8761
# [auth-service] Tomcat started on port 8081
# ...

# Arrêtez avec Ctrl+C
```

#### Mode 3 : Arrière-Plan (Pour CI/CD ou tests)

```bash
# Lancer en arrière-plan
./start-services.sh --background

# Les logs sont sauvegardés dans /tmp/microservices-logs/

# Voir les logs
./start-services.sh --logs

# Voir les logs d'un service spécifique
tail -f /tmp/microservices-logs/api-gateway.log

# Vérifier le statut
./start-services.sh --status

# Arrêter tous les services
./start-services.sh --stop
```

#### Autres Commandes

```bash
# Afficher l'aide
./start-services.sh --help

# Vérifier le statut des services
./start-services.sh --status

# Arrêter tous les services
./start-services.sh --stop

# Voir les logs en temps réel
./start-services.sh --logs
```

### Workflow Complet pour Nouveaux Développeurs

```bash
# 1. Cloner le projet
git clone https://github.com/votre-repo/microservices-ecommerce.git
cd microservices-ecommerce

# 2. Installation automatique (5-10 min)
chmod +x setup.sh
./setup.sh

# 3. Attendre la fin de l'installation
# Le script dira "Configuration complète!"

# 4. Démarrer les services (mode interactif)
chmod +x start-services.sh
./start-services.sh

# 5. 5 terminaux vont s'ouvrir avec les services

# 6. Vérifier dans un autre terminal
curl http://localhost:8761  # Eureka Dashboard

# 7. Tester l'API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"dev@example.com","password":"Dev123!","firstName":"Dev","lastName":"User"}'

# ✅ Vous êtes prêt à développer!
```

### Comparaison des Modes

| Mode | Utilité | Terminal | Logs | Recommandé pour |
|------|---------|----------|------|-----------------|
| **Interactif** | 5 terminaux séparés | 5 | Chaque service à part | Développement |
| **Parallèle** | 1 terminal, tous les logs | 1 | Entrelacés | Surveillance |
| **Arrière-plan** | Logs dans fichiers | 0 | Fichiers | CI/CD, Tests |

---

## 📚 Documentation Additionnelle

### Fichiers Importants

- 📖 [README.md](./README.md) - Documentation complète (vous êtes ici!)
- 🚀 [SETUP.md](./SETUP.md) - Guide rapide pour nouveaux développeurs (15 min)
- 📝 [.env.example](./.env.example) - Template de configuration
- 📋 [CAHIER-CHARGE.md](./CAHIER-CHARGE.md) - Spécifications fonctionnelles
- 🐳 [GUIDE-DEPLOYMENT.md](./GUIDE-DEPLOYMENT.md) - Déploiement en production

### Scripts Utiles

- ⚙️ `setup.sh` - Installation automatique du projet
- 🚀 `start-services.sh` - Démarrage des services (plusieurs modes)
- 📊 `GUIDE-SPRING-INITIALIZR.md` - Créer de nouveaux services

### Commandes Quotidiennes

```bash
# Installation (première fois)
./setup.sh

# Développement (tous les jours)
./start-services.sh                    # Mode interactif
curl http://localhost:8761              # Vérifier Eureka

# Tests
mvn clean test                          # Tests unitaires
./start-services.sh --background        # Services en background pour tests

# Monitoring
./start-services.sh --status            # Vérifier le statut
./start-services.sh --logs              # Voir les logs

# Arrêt
./start-services.sh --stop              # Arrêter les services
```

---
## 🌍 Configuration Production (.env.prod)

### 📋 Overview

Le fichier `.env.prod` contient la configuration optimisée et sécurisée pour un environnement de **production**. Il inclut des configurations pour :

- ✅ Haute disponibilité et load balancing
- ✅ Sécurité renforcée (HTTPS, WAF, DDoS)
- ✅ Monitoring et alertes
- ✅ Backup et disaster recovery
- ✅ Conformité (GDPR, CCPA, PCI-DSS)

### ⚠️ Sécurité - Points Critiques

#### 1. Ne JAMAIS Commiter `.env.prod`

```bash
# Vérifier que .env.prod est ignoré
cat .gitignore | grep ".env.prod"

# Doit afficher :
# .env.prod
# .env.*.prod
# !.env.prod.example (optionnel - template)
```

#### 2. Utiliser un Secret Manager (OBLIGATOIRE)

**Option A : AWS Secrets Manager** ⭐ Recommandé
```bash
# Créer le secret
aws secretsmanager create-secret \
  --name /prod/microservices/env \
  --secret-string file://.env.prod \
  --region eu-west-1

# Charger au démarrage
aws secretsmanager get-secret-value \
  --secret-id /prod/microservices/env \
  --region eu-west-1 \
  --query SecretString \
  --output text > .env.prod.tmp
```

**Option B : Azure Key Vault**
```bash
# Créer le vault
az keyvault create --name myVault --resource-group myGroup

# Ajouter les secrets
az keyvault secret set \
  --vault-name myVault \
  --name microservices-env \
  --file .env.prod

# Récupérer au démarrage
az keyvault secret show \
  --vault-name myVault \
  --name microservices-env \
  --query value -o tsv > .env.prod.tmp
```

**Option C : Kubernetes Secrets**
```bash
# Créer un Secret Kubernetes
kubectl create secret generic microservices-prod \
  --from-env-file=.env.prod \
  -n production

# Ou utiliser Sealed Secrets (plus sécurisé)
kubeseal -f .env.prod -w .env.prod.sealed.yaml

# Appliquer le secret
kubectl apply -f .env.prod.sealed.yaml
```

### 🔐 Exemple de Fichier `.env.prod`

```bash
# =====================================================
# PostgreSQL Production (RDS, Azure Database, etc)
# =====================================================
POSTGRES_HOST=prod-db.example.com
POSTGRES_PORT=5432
POSTGRES_USER=produser
POSTGRES_PASSWORD=<GENERATE_STRONG_PASSWORD>
POSTGRES_DB_AUTH=auth_prod_db
POSTGRES_DB_USER=user_prod_db
POSTGRES_DB_ORDER=order_prod_db

# =====================================================
# JWT Configuration (Sécurité)
# =====================================================
JWT_SECRET=<GENERATE_256_BIT_KEY_BASE64>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# =====================================================
# Services URLs (HTTPS Obligatoire)
# =====================================================
EUREKA_SERVER_URL=https://eureka.example.com
API_GATEWAY_URL=https://api.example.com
AUTH_SERVICE_URL=https://auth.example.com
USER_SERVICE_URL=https://user.example.com
ORDER_SERVICE_URL=https://order.example.com

# =====================================================
# SSL/TLS Configuration
# =====================================================
SERVER_SSL_ENABLED=true
SERVER_SSL_KEY_STORE=/etc/secrets/keystore.p12
SERVER_SSL_KEY_STORE_PASSWORD=<GENERATE_STRONG_PASSWORD>
SERVER_SSL_KEY_STORE_TYPE=PKCS12

# =====================================================
# CORS Configuration (Domaines strictes)
# =====================================================
CORS_ALLOWED_ORIGINS=https://app.example.com,https://www.example.com
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS,PATCH
CORS_ALLOWED_HEADERS=Content-Type,Authorization,Accept
CORS_ALLOW_CREDENTIALS=true

# =====================================================
# Logging & Monitoring
# =====================================================
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_MICROSERVICES=INFO
ELK_ENABLED=true
ELASTICSEARCH_HOST=elasticsearch.example.com
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=elastic
ELASTICSEARCH_PASSWORD=<GENERATE_PASSWORD>

# =====================================================
# Metrics & Observability
# =====================================================
METRICS_EXPORT_PROMETHEUS_ENABLED=true
PROMETHEUS_PUSHGATEWAY=http://pushgateway.example.com:9091
JAEGER_ENABLED=true
JAEGER_ENDPOINT=http://jaeger.example.com:14268/api/traces

# =====================================================
# Database Connection Pool
# =====================================================
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=30
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=10
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000
SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT=600000
SPRING_DATASOURCE_HIKARI_MAX_LIFETIME=1800000

# =====================================================
# Redis Cache (Optional but Recommended)
# =====================================================
REDIS_ENABLED=true
REDIS_HOST=redis.example.com
REDIS_PORT=6380
REDIS_PASSWORD=<GENERATE_PASSWORD>
REDIS_SSL=true
REDIS_DATABASE=0

# =====================================================
# Feign Client Configuration
# =====================================================
FEIGN_CONNECT_TIMEOUT=5000
FEIGN_READ_TIMEOUT=10000
FEIGN_WRITE_TIMEOUT=10000

# =====================================================
# Circuit Breaker Configuration
# =====================================================
RESILIENCE4J_ENABLED=true
RESILIENCE4J_FAILURE_RATE_THRESHOLD=50
RESILIENCE4J_SLOW_CALL_RATE_THRESHOLD=100
RESILIENCE4J_SLOW_CALL_DURATION_THRESHOLD=2000
RESILIENCE4J_MINIMUM_NUMBER_OF_CALLS=10
RESILIENCE4J_WAIT_DURATION_IN_OPEN_STATE=60000

# =====================================================
# Spring Profile
# =====================================================
SPRING_PROFILES_ACTIVE=prod

# =====================================================
# Eureka Configuration
# =====================================================
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://eureka.example.com/eureka/
EUREKA_INSTANCE_PREFER_IP_ADDRESS=false

# =====================================================
# Actuator Configuration
# =====================================================
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics,prometheus
MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/actuator
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=when-authorized

# =====================================================
# Rate Limiting
# =====================================================
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=1000
RATE_LIMIT_BURST_CAPACITY=100
```

### 📋 Checklist de Configuration Production

#### Avant le Déploiement

```bash
# 1. Vérifier que tous les secrets sont générés et sécurisés
grep "CHANGE_ME\|password123\|test\|localhost" .env.prod
# Doit être VIDE !

# 2. Valider que HTTPS est obligatoire
grep "SERVER_SSL_ENABLED=true" .env.prod
# Doit afficher la ligne

# 3. Vérifier les domaines (pas d'IP publiques)
grep "example.com" .env.prod | wc -l
# Doit avoir au moins 5 domaines configurés

# 4. Vérifier la sécurité des secrets
grep "JWT_SECRET\|POSTGRES_PASSWORD\|REDIS_PASSWORD" .env.prod | cut -d= -f2 | wc -c
# Chaque secret doit avoir au moins 32 caractères

# 5. Vérifier le Secret Manager est configuré
echo "Secrets configurés dans AWS/Azure/Kubernetes?"
# Doit être confirmé avant déploiement
```

#### Sécurité Essentielles

```bash
# ✅ HTTPS obligatoire
SERVER_SSL_ENABLED=true
SERVER_SSL_KEY_STORE_PASSWORD=<STRONG_PASSWORD>

# ✅ Pas d'exposition des erreurs
SERVER_ERROR_INCLUDE_MESSAGE=never
SERVER_ERROR_INCLUDE_STACKTRACE=never
SERVER_ERROR_INCLUDE_BINDING_ERRORS=never

# ✅ Logging sécurisé
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
# NE PAS afficher les mots de passe ou tokens dans les logs

# ✅ JWT sécurisé
JWT_SECRET=<256_BIT_KEY_BASE64_GÉNÉRÉ>
JWT_EXPIRATION=86400000  # 24h (max)

# ✅ CORS restrictif
CORS_ALLOWED_ORIGINS=https://app.example.com,https://www.example.com
# Pas de wildcard (*)

# ✅ Rate Limiting activé
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=1000
```

#### Haute Disponibilité

```bash
# ✅ Réplication base de données
POSTGRES_HOST=prod-db.example.com  # RDS/Managed instance
# Doit avoir replica automatique

# ✅ Auto-scaling (Kubernetes)
# replicas: 3 (min) à 10 (max)

# ✅ Load balancer
LOAD_BALANCER_TYPE=alb  # AWS ALB ou Azure LB
LOAD_BALANCER_URL=https://api.example.com

# ✅ Backup automatique
# PostgreSQL: enabled with WAL archiving
# Rétention: minimum 30 jours
```

#### Monitoring & Alertes

```bash
# ✅ Logs centralisés
ELK_ENABLED=true
ELASTICSEARCH_HOST=elasticsearch.example.com

# ✅ Métriques Prometheus
METRICS_EXPORT_PROMETHEUS_ENABLED=true

# ✅ Distributed Tracing
JAEGER_ENABLED=true
JAEGER_ENDPOINT=http://jaeger.example.com:14268/api/traces

# ✅ Alertes configurées
# PagerDuty, OpsGenie ou Slack webhooks doivent être ajoutés
```

### 🔑 Générer les Secrets Forts

```bash
# Générer JWT_SECRET (256 bits / 32 caractères base64)
openssl rand -base64 32
# Exemple: xC3mKp9jL2wQ8zAe5bR1vS4dF6gH7iU9jK0lM1nO2pP3qR4sT5u

# Générer POSTGRES_PASSWORD
openssl rand -base64 32

# Générer REDIS_PASSWORD
openssl rand -base64 32

# Générer SERVER_SSL_KEY_STORE_PASSWORD
openssl rand -base64 32

# Générer ELASTICSEARCH_PASSWORD
openssl rand -base64 32

# Script complet pour tous les secrets
cat > /tmp/generate-prod-secrets.sh << 'EOF'
#!/bin/bash
echo "=== Secrets de Production ===="
echo "JWT_SECRET=$(openssl rand -base64 32)"
echo "POSTGRES_PASSWORD=$(openssl rand -base64 32)"
echo "REDIS_PASSWORD=$(openssl rand -base64 32)"
echo "SERVER_SSL_KEY_STORE_PASSWORD=$(openssl rand -base64 32)"
echo "ELASTICSEARCH_PASSWORD=$(openssl rand -base64 32)"
echo "================================"
EOF

chmod +x /tmp/generate-prod-secrets.sh
/tmp/generate-prod-secrets.sh
```

### 🚀 Déploiement avec `.env.prod`

#### Docker Compose
```bash
# Déployer avec les secrets production
docker-compose -f docker-compose.prod.yml \
  --env-file .env.prod \
  up -d

# Vérifier que les services sont lancés
docker-compose ps
docker-compose logs -f
```

#### Kubernetes
```bash
# 1. Créer le secret depuis le Secret Manager
kubectl create secret generic microservices-prod \
  --from-env-file=.env.prod \
  -n production

# 2. Appliquer le deployment (référence le secret)
kubectl apply -f k8s/deployment-prod.yaml

# 3. Vérifier
kubectl get secrets -n production
kubectl get pods -n production
kubectl logs -f deployment/api-gateway -n production
```

#### AWS ECS
```bash
# 1. Pousser le secret dans Secrets Manager
aws secretsmanager create-secret \
  --name /prod/microservices/env \
  --secret-string file://.env.prod \
  --region eu-west-1

# 2. Créer la task definition avec le secret
# task.json référence l'ARN du secret

# 3. Créer le service
aws ecs create-service \
  --cluster prod \
  --task-definition microservices-prod \
  --service-name api-gateway \
  --desired-count 3
```

#### Bonnes Pratiques de Sécurité

- ✅ Utiliser un Secret Manager pour tous les secrets
- ✅ Activer HTTPS et TLS 1.3 pour toutes les communications
- ✅ Configurer des alertes pour les erreurs critiques
- ✅ Effectuer des tests de charge avant le déploiement
- ✅ Documenter toutes les configurations dans un fichier versionné

---

## 📞 Support & Ressources

### Documentation de Référence

- [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/)
- [Azure Key Vault](https://docs.microsoft.com/en-us/azure/key-vault/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- [Spring Security](https://spring.io/projects/spring-security)
- [OWASP Best Practices](https://owasp.org/www-project-web-security-testing-guide/)

### Contact

Pour toute question sur le projet, veuillez :

1. Vérifier la section [Troubleshooting](#-troubleshooting)
2. Consulter les issues GitHub
3. Créer une nouvelle issue avec les détails

---

## 📄 License

Ce projet est distribué sous la [Apache License 2.0](./LICENSE).

© 2026 Microservices Architecture Project. Tous droits réservés.