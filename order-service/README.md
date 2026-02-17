# 📦 Order Service - Service de Gestion des Commandes

## 📋 Description

Le **Order Service** est un microservice de gestion des commandes dans une architecture microservices Spring Boot. Il gère le cycle de vie complet des commandes avec gestion des statuts, permissions basées sur les rôles, et communication avec le User Service.

---

## 🎯 Fonctionnalités Principales

### ✅ Gestion des Commandes
- **CRUD complet** des commandes
- **Génération automatique** de numéros de commande uniques (`ORD-YYYYMMDD-XXXXXX`)
- **Validation des données** avec Bean Validation
- **Recherches avancées** (par utilisateur, statut, dates)
- **Statistiques** (comptage, montant total)

### 🔄 Gestion des Statuts
- **5 statuts** : `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`
- **Transitions validées** : empêche les changements de statut invalides
- **États finaux** : `DELIVERED` et `CANCELLED` (non modifiables)
- **Vérification d'annulation** : seules certaines commandes sont annulables

### 🔐 Sécurité & Permissions
- **Authentification JWT** via Auth Service
- **2 rôles** : `USER` et `ADMIN`
- **Permissions granulaires** :
  - `USER` : peut créer et gérer **ses propres commandes uniquement**
  - `ADMIN` : **accès complet** à toutes les commandes

### 🌐 Communication Inter-Services
- **Feign Client** pour communiquer avec User Service
- **Circuit Breaker** (Resilience4j) avec fallback
- **Propagation JWT** automatique dans les appels Feign
- **Enrichissement** des commandes avec infos utilisateur

---

## 🏗️ Architecture

### Technologies Utilisées
- **Spring Boot 3.2.1**
- **Spring Data JPA** (H2 Database)
- **Spring Security** + OAuth2 Resource Server (JWT)
- **Spring Cloud OpenFeign** (communication inter-services)
- **Resilience4j** (Circuit Breaker)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok** (réduction du code boilerplate)

### Structure du Projet
```
order-service/
├── controller/          # Endpoints REST
├── service/            # Logique métier
├── repository/         # Accès données (JPA)
├── entity/             # Entités JPA
├── dto/                # Data Transfer Objects
├── mapper/             # Conversions Entity ↔ DTO
├── client/             # Feign Clients
├── security/           # Configuration Spring Security
├── config/             # Configurations (OpenAPI, Feign)
└── exception/          # Gestion globale des erreurs
```

---

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.8+
- Auth Service en cours d'exécution (port 8081)
- User Service en cours d'exécution (port 8082)

### 1. Cloner et Construire
```bash
cd order-service
mvn clean install
```

### 2. Lancer le Service
```bash
mvn spring-boot:run
```

Le service démarre sur **http://localhost:8083**

### 3. Accéder à la Documentation
- **Swagger UI** : http://localhost:8083/swagger-ui.html
- **H2 Console** : http://localhost:8083/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: *(vide)*

### Avec script
```bash
# Rendre les scripts exécutables
chmod +x start.sh stop.sh

# Premier démarrage (build + run)
./start.sh --build

# Démarrages suivants
./start.sh

# Arrêt propre
./stop.sh

# Lancer les tests
cd order-service
mvn test
```

---

## 📡 Endpoints Principaux

### 🔓 Publics
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/orders/health` | Health check |

### 🔒 Authentifiés (USER/ADMIN)

#### Commandes Utilisateur
| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| POST | `/orders` | USER, ADMIN | Créer une commande |
| GET | `/orders/{id}` | USER, ADMIN | Récupérer une commande |
| GET | `/orders/number/{orderNumber}` | USER, ADMIN | Récupérer par numéro |
| GET | `/orders/my-orders` | USER, ADMIN | Mes commandes |
| PUT | `/orders/{id}` | USER, ADMIN | Mettre à jour |
| PATCH | `/orders/{id}/cancel` | USER, ADMIN | Annuler une commande |

#### Administration (ADMIN uniquement)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/orders` | Toutes les commandes (paginé) |
| GET | `/orders/status/{status}` | Commandes par statut |
| PATCH | `/orders/{id}/status` | Changer le statut |
| DELETE | `/orders/{id}` | Supprimer (commandes annulées) |

#### Statistiques
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/orders/user/{userId}/count` | Nombre de commandes |
| GET | `/orders/user/{userId}/count/status/{status}` | Par statut |
| GET | `/orders/user/{userId}/total-amount` | Montant total |
| GET | `/orders/user/{userId}/latest` | Dernières commandes |

#### Recherche (ADMIN)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/orders/search/after?date={date}` | Après une date |
| GET | `/orders/search/between?startDate={start}&endDate={end}` | Entre deux dates |

---

## 🔄 Gestion des Statuts

### Cycle de Vie d'une Commande
```
PENDING → CONFIRMED → SHIPPED → DELIVERED (final)
   ↓
CANCELLED (final)
```

### Transitions Autorisées
| Depuis | Vers | Autorisé |
|--------|------|----------|
| PENDING | CONFIRMED | ✅ |
| PENDING | CANCELLED | ✅ |
| CONFIRMED | SHIPPED | ✅ |
| CONFIRMED | CANCELLED | ✅ |
| SHIPPED | DELIVERED | ✅ |
| SHIPPED | CANCELLED | ❌ |
| DELIVERED | * | ❌ (final) |
| CANCELLED | * | ❌ (final) |

### Commandes Annulables
- ✅ `PENDING` : Oui
- ✅ `CONFIRMED` : Oui
- ❌ `SHIPPED` : Non (déjà expédiée)
- ❌ `DELIVERED` : Non (déjà livrée)
- ❌ `CANCELLED` : Non (déjà annulée)

---

## 🧪 Tests avec Swagger

### 1. Obtenir un Token JWT
Appelez le Auth Service pour vous connecter :
```bash
POST http://localhost:8081/auth/login
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

### 2. Configurer Swagger
1. Accédez à http://localhost:8083/swagger-ui.html
2. Cliquez sur **"Authorize"** (cadenas en haut à droite)
3. Entrez : `Bearer <votre_token>`
4. Cliquez sur **"Authorize"** puis **"Close"**

### 3. Tester les Endpoints
Tous les endpoints sont maintenant accessibles !

---

## 📊 Données de Test

Le service est pré-chargé avec 8 commandes de test :

### John Doe (4 commandes)
- `ORD-20250120-100001` : PENDING - Ordinateur portable (299.99€)
- `ORD-20250118-100002` : CONFIRMED - Souris (89.99€)
- `ORD-20250115-100003` : SHIPPED - iPhone (1499.99€)
- `ORD-20250110-100004` : DELIVERED - Câble USB-C (49.99€)

### Jane Smith (3 commandes)
- `ORD-20250121-100005` : PENDING - MacBook Air (799.99€)
- `ORD-20250119-100006` : CONFIRMED - AirPods (199.99€)
- `ORD-20250117-100007` : CANCELLED - Chargeur (59.99€)

### Admin (1 commande)
- `ORD-20250112-100008` : DELIVERED - MacBook Pro (2499.99€)

**Total : 8 commandes | Montant total : 5,489.91€**

---

## 🔌 Intégration avec User Service

### Communication via Feign
Le Order Service communique avec le User Service pour enrichir les commandes avec les informations utilisateur :

```java
@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {
    @GetMapping("/{userId}")
    ApiResponse<UserDTO> getUserById(@PathVariable String userId);
}
```

### Circuit Breaker
Si le User Service est indisponible :
- ✅ **Fallback automatique** : retourne un utilisateur par défaut
- ✅ **Pas d'erreur** : les commandes restent accessibles
- ⚠️ **Données limitées** : email et nom = "Utilisateur inconnu"

---

## 🔒 Sécurité

### Authentification JWT
- **Issuer** : `http://localhost:8081` (Auth Service)
- **Validation** : Signature + expiration
- **Claims requis** : `userId`, `sub` (email), `roles`

### Permissions par Endpoint
```java
@PreAuthorize("hasRole('USER')")     // USER ou ADMIN
@PreAuthorize("hasRole('ADMIN')")    // ADMIN uniquement
```

### Validation des Accès
Le service vérifie automatiquement :
- ✅ Si l'utilisateur accède à **ses propres commandes**
- ✅ Si l'utilisateur est **ADMIN** (accès complet)
- ❌ Sinon : **403 Forbidden**

---

## 📝 Exemples de Requêtes

### Créer une Commande (USER)
```bash
POST http://localhost:8083/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "totalAmount": 1299.99,
  "description": "MacBook Pro 14\"",
  "shippingAddress": "123 Rue Example",
  "shippingCity": "Paris",
  "shippingCountry": "France",
  "shippingPostalCode": "75001",
  "notes": "Livraison express"
}
```

### Mes Commandes (USER)
```bash
GET http://localhost:8083/orders/my-orders?page=0&size=10
Authorization: Bearer <token>
```

### Annuler une Commande (USER)
```bash
PATCH http://localhost:8083/orders/{orderId}/cancel
Authorization: Bearer <token>
```

### Changer le Statut (ADMIN)
```bash
PATCH http://localhost:8083/orders/{orderId}/status?status=SHIPPED
Authorization: Bearer <admin_token>
```

### Statistiques Utilisateur
```bash
GET http://localhost:8083/orders/user/{userId}/total-amount
Authorization: Bearer <token>
```

---

## 🐛 Gestion des Erreurs

Le service retourne des réponses structurées :

### Succès (200 OK)
```json
{
  "success": true,
  "message": "Commande créée avec succès",
  "data": { ... }
}
```

### Erreur (400 Bad Request)
```json
{
  "success": false,
  "message": "Transition de statut invalide: SHIPPED -> CANCELLED"
}
```

### Erreur Validation (400)
```json
{
  "success": false,
  "message": "Erreur de validation",
  "data": {
    "totalAmount": "Le montant doit être supérieur à 0",
    "shippingAddress": "L'adresse ne peut pas dépasser 500 caractères"
  }
}
```

### Non Autorisé (403 Forbidden)
```json
{
  "success": false,
  "message": "Vous n'êtes pas autorisé à accéder à cette commande"
}
```

---

## 📈 Monitoring

### Actuator Endpoints
- **Health** : http://localhost:8083/actuator/health
- **Info** : http://localhost:8083/actuator/info
- **Metrics** : http://localhost:8083/actuator/metrics
- **Circuit Breakers** : http://localhost:8083/actuator/health/circuitBreakers

### Circuit Breaker Status
```bash
GET http://localhost:8083/actuator/health
```

Response :
```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP",
      "details": {
        "user-service": {
          "status": "CLOSED",
          "failureRate": "0.0%"
        }
      }
    }
  }
}
```

---

## 🔧 Configuration

### Ports par Défaut
- Order Service : **8083**
- Auth Service : **8081**
- User Service : **8082**

### Modifier les Ports
Éditez `application.yml` :
```yaml
server:
  port: 8083  # Changez ici
```

### Configuration JWT
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081
          jwk-set-uri: http://localhost:8081/.well-known/jwks.json
```

### Configuration Feign
```yaml
feign:
  client:
    config:
      user-service:
        url: http://localhost:8082  # URL du User Service
```

---

## 🎓 Points Clés à Retenir

### ✅ Ce que ce service fait bien
1. **Sécurité robuste** : JWT + permissions granulaires
2. **Gestion de statuts** : Transitions validées
3. **Résilience** : Circuit Breaker avec fallback
4. **Communication** : Feign Client avec propagation JWT
5. **Documentation** : Swagger UI complet
6. **Validation** : Bean Validation sur tous les DTOs

### 💡 Bonnes Pratiques Implémentées
- ✅ **Séparation des responsabilités** (Controller/Service/Repository)
- ✅ **DTOs** pour éviter d'exposer les entités
- ✅ **Mapper** pour conversions Entity ↔ DTO
- ✅ **Gestion centralisée des exceptions**
- ✅ **Logs structurés** (SLF4J + Lombok)
- ✅ **Transactions** (@Transactional)
- ✅ **Auditing JPA** (createdAt, updatedAt)

---

## 🎉 Conclusion

Le **Order Service** est maintenant **COMPLET** ! 🚀

Il offre :
- ✅ Gestion complète des commandes
- ✅ Sécurité JWT robuste
- ✅ Communication inter-services
- ✅ Gestion avancée des statuts
- ✅ Permissions granulaires
- ✅ Documentation Swagger
- ✅ Résilience avec Circuit Breaker

---

## 🔗 Liens Utiles

- **Swagger UI** : http://localhost:8083/swagger-ui.html
- **H2 Console** : http://localhost:8083/h2-console
- **Health Check** : http://localhost:8083/orders/health
- **Actuator** : http://localhost:8083/actuator

---

**Baye Rane** | Version 1.0.0 | Janvier 2025