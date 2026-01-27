# 🚀 Guide de Déploiement et Démarrage

## 📋 Prérequis Système

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

---

## 🗄️ ÉTAPE 1 : Configuration PostgreSQL

### 1.1 Installation PostgreSQL (si non installé)

**Windows**
```bash
# Télécharger depuis https://www.postgresql.org/download/windows/
# Ou via Chocolatey
choco install postgresql
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

### 1.2 Création des Bases de Données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Ou si authentification requise
psql -U postgres -W
```

Exécuter le script SQL fourni précédemment, ou créer manuellement :

```sql
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;
```

### 1.3 Vérification

```sql
\l  -- Lister toutes les bases de données
\q  -- Quitter psql
```

### 1.4 Configuration du Mot de Passe (si nécessaire)

Si vous utilisez un mot de passe différent de "postgres", modifiez les fichiers `application.yml` de chaque service :

```yaml
spring:
  datasource:
    username: votre_utilisateur
    password: votre_mot_de_passe
```

---

## 📦 ÉTAPE 2 : Clonage et Build du Projet

### 2.1 Clonage (si projet sur Git)

```bash
git clone <url-du-repo>
cd microservices-backend
```

### 2.2 Build Complet

```bash
# Build de tous les modules
mvn clean install

# Si erreur sur common-lib, build dans l'ordre
cd common-lib
mvn clean install

cd ../discovery-service
mvn clean install

cd ../api-gateway
mvn clean install

cd ../auth-service
mvn clean install

cd ../user-service
mvn clean install

cd ../order-service
mvn clean install
```

### 2.3 Vérification du Build

Chaque service doit avoir généré un fichier JAR dans son dossier `target/` :

```
discovery-service/target/discovery-service-1.0.0.jar
api-gateway/target/api-gateway-1.0.0.jar
auth-service/target/auth-service-1.0.0.jar
user-service/target/user-service-1.0.0.jar
order-service/target/order-service-1.0.0.jar
```

---

## 🎯 ÉTAPE 3 : Ordre de Démarrage des Services

⚠️ **IMPORTANT** : L'ordre de démarrage est CRUCIAL !

### Ordre Obligatoire

```
1. PostgreSQL (doit être démarré en premier)
2. Discovery Service (Eureka)
3. Auth Service
4. API Gateway
5. User Service
6. Order Service
```

### 3.1 Démarrer Discovery Service

**Terminal 1**
```bash
cd discovery-service
mvn spring-boot:run

# OU via JAR
java -jar target/discovery-service-1.0.0.jar
```

**Vérification** : Accéder à http://localhost:8761
- Vous devez voir le Dashboard Eureka
- Aucun service n'est encore enregistré

⏱️ **Attendre 30 secondes** avant de continuer

### 3.2 Démarrer Auth Service

**Terminal 2**
```bash
cd auth-service
mvn spring-boot:run

# OU
java -jar target/auth-service-1.0.0.jar
```

**Vérification** :
- Console : `Registered with Eureka`
- http://localhost:8761 : `AUTH-SERVICE` apparaît
- Swagger : http://localhost:8081/swagger-ui.html

⏱️ **Attendre 15 secondes**

### 3.3 Démarrer API Gateway

**Terminal 3**
```bash
cd api-gateway
mvn spring-boot:run

# OU
java -jar target/api-gateway-1.0.0.jar
```

**Vérification** :
- Console : `Registered with Eureka`
- http://localhost:8761 : `API-GATEWAY` apparaît
- Routes configurées visibles dans les logs

⏱️ **Attendre 15 secondes**

### 3.4 Démarrer User Service

**Terminal 4**
```bash
cd user-service
mvn spring-boot:run

# OU
java -jar target/user-service-1.0.0.jar
```

**Vérification** :
- http://localhost:8761 : `USER-SERVICE` apparaît
- Swagger : http://localhost:8082/swagger-ui.html

### 3.5 Démarrer Order Service

**Terminal 5**
```bash
cd order-service
mvn spring-boot:run

# OU
java -jar target/order-service-1.0.0.jar
```

**Vérification** :
- http://localhost:8761 : `ORDER-SERVICE` apparaît
- Swagger : http://localhost:8083/swagger-ui.html

---

## ✅ ÉTAPE 4 : Vérification Complète

### 4.1 Dashboard Eureka

Accéder à http://localhost:8761

Vous devez voir 4 services enregistrés :
- `API-GATEWAY` (1 instance)
- `AUTH-SERVICE` (1 instance)
- `USER-SERVICE` (1 instance)
- `ORDER-SERVICE` (1 instance)

### 4.2 Endpoints Actuator

```bash
# Discovery Service
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8081/actuator/health

# User Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8083/actuator/health
```

Tous doivent retourner `{"status":"UP"}`

### 4.3 Test d'Authentification

```bash
# Via API Gateway
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@microservices.com",
    "password": "admin123"
  }'
```

Réponse attendue :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "email": "admin@microservices.com",
  "role": "ADMIN"
}
```

---

## 🐛 ÉTAPE 5 : Résolution des Problèmes

### Problème 1 : Services ne s'enregistrent pas dans Eureka

**Symptôme** : Service démarre mais n'apparaît pas sur http://localhost:8761

**Solutions** :
1. Vérifier que Eureka est bien démarré
2. Vérifier `eureka.client.service-url.defaultZone` dans application.yml
3. Vérifier les logs pour des erreurs de connexion
4. Attendre 30-60 secondes (délai de registration)

```bash
# Forcer le refresh
curl -X POST http://localhost:8761/eureka/apps/AUTH-SERVICE
```

### Problème 2 : Erreur de connexion PostgreSQL

**Symptôme** : `org.postgresql.util.PSQLException: Connection refused`

**Solutions** :
1. Vérifier que PostgreSQL est démarré
```bash
# Linux/macOS
sudo systemctl status postgresql

# Windows (Services)
services.msc → Rechercher PostgreSQL
```

2. Vérifier le port (5432 par défaut)
```bash
netstat -an | grep 5432
```

3. Vérifier les credentials dans application.yml

4. Créer les bases de données si manquantes

### Problème 3 : Port déjà utilisé

**Symptôme** : `Port 8080 is already in use`

**Solutions** :
1. Trouver le processus utilisant le port
```bash
# Linux/macOS
lsof -i :8080

# Windows
netstat -ano | findstr :8080
```

2. Tuer le processus ou changer le port dans application.yml

### Problème 4 : JWT Token invalide

**Symptôme** : `401 Unauthorized` sur les requêtes protégées

**Solutions** :
1. Vérifier que le secret JWT est identique dans Gateway et Auth Service
2. Vérifier l'expiration du token (24h par défaut)
3. Générer un nouveau token via `/api/auth/login`

### Problème 5 : Mémoire insuffisante

**Symptôme** : Services s'arrêtent ou redémarrent

**Solutions** :
1. Augmenter la mémoire JVM
```bash
java -Xmx512m -jar service.jar
```

2. Démarrer moins de services en parallèle
3. Utiliser des profils Spring pour environnement de dev

---

## 📊 ÉTAPE 6 : Monitoring et Logs

### Consulter les Logs

```bash
# Logs en temps réel
tail -f discovery-service/logs/application.log

# Ou dans la console si démarré avec mvn spring-boot:run
```

### Endpoints de Monitoring

```bash
# Métriques
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info

# Circuitbreakers (User/Order Service)
curl http://localhost:8082/actuator/circuitbreakers
```

---

## 🔄 ÉTAPE 7 : Redémarrage

### Arrêt Propre

1. Arrêter dans l'ordre inverse :
   - Order Service (Ctrl+C)
   - User Service
   - API Gateway
   - Auth Service
   - Discovery Service

2. Vérifier qu'aucun processus ne reste actif
```bash
jps  # Liste les processus Java
```

### Redémarrage

Suivre l'ordre de l'ÉTAPE 3.

---

## 🐳 ÉTAPE 8 : Alternative Docker (Optionnel)

Si vous préférez utiliser Docker :

```bash
# Build des images
docker-compose build

# Démarrage
docker-compose up -d

# Logs
docker-compose logs -f

# Arrêt
docker-compose down
```

---

## 📝 Checklist de Démarrage

- [ ] PostgreSQL démarré et bases créées
- [ ] Discovery Service démarré (port 8761)
- [ ] Dashboard Eureka accessible
- [ ] Auth Service enregistré dans Eureka
- [ ] API Gateway enregistré dans Eureka
- [ ] User Service enregistré dans Eureka
- [ ] Order Service enregistré dans Eureka
- [ ] Test d'authentification réussi
- [ ] Accès aux Swagger UI de chaque service

---

## 🎯 Résumé des URLs

| Service | Port | URL | Swagger |
|---------|------|-----|---------|
| Eureka Dashboard | 8761 | http://localhost:8761 | - |
| API Gateway | 8080 | http://localhost:8080 | - |
| Auth Service | 8081 | http://localhost:8081 | http://localhost:8081/swagger-ui.html |
| User Service | 8082 | http://localhost:8082 | http://localhost:8082/swagger-ui.html |
| Order Service | 8083 | http://localhost:8083 | http://localhost:8083/swagger-ui.html |

**Tous les endpoints doivent être accessibles via la Gateway** : http://localhost:8080/api/...