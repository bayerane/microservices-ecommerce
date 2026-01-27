# 🚀 Guide de Démarrage Rapide pour Nouveaux Développeurs

> **Durée estimée** : 15-20 minutes pour une mise en place complète  
> **Dernière mise à jour** : 26 janvier 2026

---

## 📋 Table des Matières

1. [Prérequis](#-prérequis)
2. [Installation Rapide](#-installation-rapide)
3. [Démarrage des Services](#-démarrage-des-services)
4. [Vérification](#-vérification)
5. [Premiers Pas](#-premiers-pas)
6. [Troubleshooting](#-troubleshooting)
7. [Ressources Supplémentaires](#-ressources-supplémentaires)

---

## ✅ Prérequis

### Vérifier les outils installés

```bash
# Java 17+
java -version

# Maven 3.8+
mvn -version

# PostgreSQL 13+
psql --version

# Git 2.30+
git --version
```

**Si un outil est manquant**, consultez le [README.md](./README.md#-prérequis) pour les instructions d'installation.

---

## ⚡ Installation Rapide

### 1️⃣ Cloner le Projet

```bash
git clone https://github.com/votre-username/microservices-ecommerce.git
cd microservices-ecommerce
```

### 2️⃣ Créer la Configuration Locale

```bash
# Copier le fichier d'exemple
cp .env.example .env

# Éditer la configuration (optionnel, les valeurs par défaut conviennent pour le dev)
nano .env
```

**⚠️ Important** : Ne modifiez que si PostgreSQL n'est pas sur `localhost:5432`

### 3️⃣ Initialiser PostgreSQL

```bash
# Démarrer PostgreSQL
sudo systemctl start postgresql

# Créer les 3 bases de données
psql -U postgres -c "CREATE DATABASE auth_db;"
psql -U postgres -c "CREATE DATABASE user_db;"
psql -U postgres -c "CREATE DATABASE order_db;"

# Vérifier
psql -U postgres -l | grep "_db"
```

✅ Vous devez voir 3 bases : `auth_db`, `user_db`, `order_db`

### 4️⃣ Build du Projet

```bash
# Build complet (prend 2-3 minutes)
mvn clean install -DskipTests

# Ou avec les tests
mvn clean install
```

✅ Attendez le message : `BUILD SUCCESS`

---

## 🚀 Démarrage des Services

⚠️ **Ordre important** : Démarrer les services dans cet ordre

### Option 1 : Lancer dans des Terminaux Séparés (Recommandé)

Ouvrez **5 terminaux** différents :

**Terminal 1 : Eureka Discovery Service**
```bash
cd discovery-service
mvn spring-boot:run
```
✅ Attendez : `Tomcat started on port(s): 8761`

**Terminal 2 : Auth Service**
```bash
cd auth-service
mvn spring-boot:run
```
✅ Attendez : `Tomcat started on port(s): 8081`

**Terminal 3 : User Service**
```bash
cd user-service
mvn spring-boot:run
```
✅ Attendez : `Tomcat started on port(s): 8082`

**Terminal 4 : Order Service**
```bash
cd order-service
mvn spring-boot:run
```
✅ Attendez : `Tomcat started on port(s): 8083`

**Terminal 5 : API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```
✅ Attendez : `Tomcat started on port(s): 8080`

### Option 2 : Lancer en Arrière-Plan (Avancé)

```bash
# Lancer tous les services en background
cd discovery-service && mvn spring-boot:run > /tmp/discovery.log 2>&1 &
sleep 5
cd ../auth-service && mvn spring-boot:run > /tmp/auth.log 2>&1 &
sleep 3
cd ../user-service && mvn spring-boot:run > /tmp/user.log 2>&1 &
sleep 3
cd ../order-service && mvn spring-boot:run > /tmp/order.log 2>&1 &
sleep 3
cd ../api-gateway && mvn spring-boot:run > /tmp/gateway.log 2>&1 &

# Vérifier les logs
tail -f /tmp/discovery.log &
tail -f /tmp/api-gateway.log &
```

---

## ✔️ Vérification

### 1. Dashboard Eureka

Ouvrez votre navigateur :

```
http://localhost:8761
```

✅ Vous devez voir **5 services** enregistrés :
- DISCOVERY-SERVICE
- AUTH-SERVICE
- USER-SERVICE
- ORDER-SERVICE
- API-GATEWAY

### 2. Health Check des Services

```bash
# Auth Service
curl http://localhost:8081/actuator/health

# User Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8083/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

Attendu : `{"status":"UP"}`

### 3. Documentation API (Swagger)

Ouvrez votre navigateur :

- **API Gateway** : http://localhost:8080/swagger-ui.html
- **Auth Service** : http://localhost:8081/swagger-ui.html
- **User Service** : http://localhost:8082/swagger-ui.html
- **Order Service** : http://localhost:8083/swagger-ui.html

---

## 🎯 Premiers Pas

### Test Simple : S'inscrire et se Connecter

```bash
# 1. S'INSCRIRE
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dev@example.com",
    "password": "DevPassword123!",
    "firstName": "Dev",
    "lastName": "User"
  }'

# Résultat attendu :
# {
#   "token": "eyJhbGciOiJIUzI1NiIs...",
#   "expiresIn": 86400000,
#   "userId": 1
# }
```

Copiez le **token** 👆

```bash
# 2. SE CONNECTER (avec le même email/password)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dev@example.com",
    "password": "DevPassword123!"
  }'
```

```bash
# 3. OBTENIR SON PROFIL
TOKEN="votre_token_ici"

curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

### Créer une Commande

```bash
# Avoir le token (voir ci-dessus)
TOKEN="votre_token_ici"

# Créer une commande
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "totalAmount": 150.00,
    "items": [
      {
        "productId": 101,
        "quantity": 1,
        "unitPrice": 150.00
      }
    ]
  }'
```

---

## 🔧 Troubleshooting

### ❌ Erreur : "Connection refused" sur PostgreSQL

```bash
# Vérifier que PostgreSQL est démarré
sudo systemctl status postgresql

# Démarrer PostgreSQL
sudo systemctl start postgresql

# Vérifier les bases
psql -U postgres -l | grep "_db"
```

### ❌ Erreur : "Port already in use"

```bash
# Trouver le processus qui utilise le port (ex: 8080)
lsof -i :8080

# Tuer le processus
kill -9 <PID>

# Ou attendre que Spring relâche le port (5 sec)
sleep 5
```

### ❌ Erreur : "Failed to bind to port"

Vérifier les ports dans `.env` :

```bash
grep "PORT" .env
```

Changer les ports si nécessaire :

```bash
nano .env
# Modifier les ports pour éviter les conflits
```

### ❌ Erreur : "Database does not exist"

```bash
# Créer les bases manquantes
psql -U postgres << EOF
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;
EOF
```

### ❌ Erreur : "Maven build fails"

```bash
# Nettoyer et reconstruire
mvn clean
mvn install -DskipTests

# Ou forcer la réindexation
rm -rf ~/.m2/repository
mvn clean install
```

### ❌ Erreur : "Cannot find Java 17"

```bash
# Vérifier la version de Java
java -version

# Définir la version JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
java -version

# Vérifier que Maven utilise Java 17
mvn -version
```

---

## 📚 Ressources Supplémentaires

### Documentation Complète
- 📖 [README.md](./README.md) - Documentation complète du projet
- 🏗️ [Architecture Details](./README.md#-architecture)
- 📚 [API Documentation](./README.md#-api-documentation)
- 🧪 [Tests Guide](./README.md#-tests)

### Guides Spécifiques
- 🐳 [Guide de Déploiement](./GUIDE-DEPLOYMENT.md)
- 📋 [Cahier des Charges](./CAHIER-CHARGE.md)
- 🌐 [Spring Initializr](./GUIDE-SPRING-INITIALIZR.md)

### Commandes Utiles

```bash
# Voir les logs en temps réel
tail -f api-gateway/target/*.log

# Arrêter tous les services Maven
pkill -f "spring-boot:run"

# Nettoyer les builds
mvn clean -rf

# Exécuter les tests
mvn test

# Générer un rapport de couverture
mvn clean test jacoco:report
open api-gateway/target/site/jacoco/index.html
```

### Outils Recommandés

| Outil | Utilité | Installation |
|-------|---------|--------------|
| **Postman** | Tester les APIs | https://postman.com/downloads |
| **IntelliJ IDEA** | IDE Java | https://jetbrains.com/idea |
| **VS Code** | Éditeur léger | https://code.visualstudio.com |
| **DBeaver** | Gérer PostgreSQL | https://dbeaver.io |
| **jq** | Parser JSON CLI | `sudo apt install jq` |

---

## 👥 Support et Questions

### Avant de poser une question

1. ✅ Consulter le [Troubleshooting](#-troubleshooting)
2. ✅ Vérifier le [README.md](./README.md)
3. ✅ Regarder les logs : `/tmp/*.log`
4. ✅ Vérifier les services : `curl http://localhost:8761`

### Contacter l'équipe

- 📧 **Email** : contact@microservices.com
- 💬 **Slack** : #microservices-dev
- 🐛 **Issues** : https://github.com/votre-repo/issues

---

## ✨ Prochaines Étapes

Maintenant que vous avez les services en cours d'exécution :

1. ✅ Explorez les endpoints Swagger (http://localhost:8080/swagger-ui.html)
2. ✅ Lisez le code source des services
3. ✅ Lancez les tests unitaires (`mvn test`)
4. ✅ Modifiez un endpoint pour comprendre le flux
5. ✅ Consultez le [README.md](./README.md) pour plus de détails

---

## 📋 Checklist de Démarrage

- [ ] Java 17+ installé
- [ ] Maven 3.8+ installé
- [ ] PostgreSQL 13+ installé et démarré
- [ ] Projet cloné
- [ ] `.env` configuré
- [ ] 3 bases de données créées
- [ ] `mvn clean install` réussi
- [ ] Discovery Service lancé (port 8761)
- [ ] Auth Service lancé (port 8081)
- [ ] User Service lancé (port 8082)
- [ ] Order Service lancé (port 8083)
- [ ] API Gateway lancé (port 8080)
- [ ] Eureka dashboard visible (http://localhost:8761)
- [ ] 5 services visibles dans Eureka
- [ ] Test d'inscription/connexion réussi

✅ **Si tous les points sont cochés, vous êtes prêt(e) à développer !**

---

**Bienvenue dans l'équipe ! 🎉**