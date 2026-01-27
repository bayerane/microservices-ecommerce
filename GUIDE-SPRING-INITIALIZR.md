# 🚀 Guide Spring Initializr - Création Complète du Projet

## 📋 Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, VS Code)
- PostgreSQL 13+ installé
- Navigateur web pour Spring Initializr

---

## 🌐 Accès à Spring Initializr

**URL** : https://start.spring.io/

---

# 1️⃣ DISCOVERY SERVICE (Eureka Server)

## Configuration Spring Initializr

| Paramètre | Valeur |
|-----------|--------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.1 (ou dernière stable 3.x) |
| **Project Metadata** | |
| - Group | `com.microservices` |
| - Artifact | `discovery-service` |
| - Name | `discovery-service` |
| - Description | `Eureka Server for service discovery` |
| - Package name | `com.microservices.discovery` |
| - Packaging | `Jar` |
| - Java | `17` |

## Dependencies à ajouter

1. **Eureka Server** 
   - Catégorie: Spring Cloud Discovery
   - ID: `spring-cloud-starter-netflix-eureka-server`

2. **Spring Boot Actuator**
   - Catégorie: Ops
   - ID: `spring-boot-starter-actuator`

3. **Lombok** (optionnel mais recommandé)
   - Catégorie: Developer Tools
   - ID: `lombok`

## Étapes de génération

1. Cliquez sur **"ADD DEPENDENCIES"**
2. Cherchez et ajoutez : `Eureka Server`
3. Cherchez et ajoutez : `Spring Boot Actuator`
4. Cherchez et ajoutez : `Lombok`
5. Cliquez sur **"GENERATE"** (Ctrl+Enter)
6. Téléchargez le fichier ZIP
7. Décompressez dans votre workspace

---

# 2️⃣ API GATEWAY

## Configuration Spring Initializr

| Paramètre | Valeur |
|-----------|--------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.1 |
| **Project Metadata** | |
| - Group | `com.microservices` |
| - Artifact | `api-gateway` |
| - Name | `api-gateway` |
| - Description | `API Gateway with JWT security` |
| - Package name | `com.microservices.gateway` |
| - Packaging | `Jar` |
| - Java | `17` |

## Dependencies à ajouter

1. **Gateway**
   - Catégorie: Spring Cloud Routing
   - ID: `spring-cloud-starter-gateway`

2. **Eureka Discovery Client**
   - Catégorie: Spring Cloud Discovery
   - ID: `spring-cloud-starter-netflix-eureka-client`

3. **Spring Boot Actuator**
   - Catégorie: Ops
   - ID: `spring-boot-starter-actuator`

4. **Lombok**
   - Catégorie: Developer Tools
   - ID: `lombok`

⚠️ **IMPORTANT** : Les dépendances JWT et Security Reactive devront être ajoutées **manuellement** dans le `pom.xml` après génération.

## Étapes de génération

1. Ajoutez les dépendances listées ci-dessus
2. Cliquez sur **"GENERATE"**
3. Téléchargez et décompressez

---

# 3️⃣ AUTH SERVICE

## Configuration Spring Initializr

| Paramètre | Valeur |
|-----------|--------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.1 |
| **Project Metadata** | |
| - Group | `com.microservices` |
| - Artifact | `auth-service` |
| - Name | `auth-service` |
| - Description | `Authentication and authorization service` |
| - Package name | `com.microservices.auth` |
| - Packaging | `Jar` |
| - Java | `17` |

## Dependencies à ajouter

1. **Spring Web**
   - Catégorie: Web
   - ID: `spring-boot-starter-web`

2. **Spring Security**
   - Catégorie: Security
   - ID: `spring-boot-starter-security`

3. **Spring Data JPA**
   - Catégorie: SQL
   - ID: `spring-boot-starter-data-jpa`

4. **PostgreSQL Driver**
   - Catégorie: SQL
   - ID: `postgresql`

5. **Eureka Discovery Client**
   - Catégorie: Spring Cloud Discovery
   - ID: `spring-cloud-starter-netflix-eureka-client`

6. **Validation**
   - Catégorie: I/O
   - ID: `spring-boot-starter-validation`

7. **Spring Boot Actuator**
   - Catégorie: Ops
   - ID: `spring-boot-starter-actuator`

8. **Lombok**
   - Catégorie: Developer Tools
   - ID: `lombok`

⚠️ **IMPORTANT** : Les dépendances JWT et SpringDoc devront être ajoutées **manuellement** dans le `pom.xml`.

## Étapes de génération

1. Ajoutez toutes les dépendances listées
2. Cliquez sur **"GENERATE"**
3. Téléchargez et décompressez

---

# 4️⃣ USER SERVICE

## Configuration Spring Initializr

| Paramètre | Valeur |
|-----------|--------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.1 |
| **Project Metadata** | |
| - Group | `com.microservices` |
| - Artifact | `user-service` |
| - Name | `user-service` |
| - Description | `User management service` |
| - Package name | `com.microservices.user` |
| - Packaging | `Jar` |
| - Java | `17` |

## Dependencies à ajouter

1. **Spring Web**
   - ID: `spring-boot-starter-web`

2. **Spring Security**
   - ID: `spring-boot-starter-security`

3. **Spring Data JPA**
   - ID: `spring-boot-starter-data-jpa`

4. **PostgreSQL Driver**
   - ID: `postgresql`

5. **Eureka Discovery Client**
   - ID: `spring-cloud-starter-netflix-eureka-client`

6. **OpenFeign**
   - Catégorie: Spring Cloud Routing
   - ID: `spring-cloud-starter-openfeign`

7. **Validation**
   - ID: `spring-boot-starter-validation`

8. **Spring Boot Actuator**
   - ID: `spring-boot-starter-actuator`

9. **Lombok**
   - ID: `lombok`

⚠️ **IMPORTANT** : Resilience4j et SpringDoc devront être ajoutés **manuellement**.

## Étapes de génération

1. Ajoutez toutes les dépendances
2. Cliquez sur **"GENERATE"**
3. Téléchargez et décompressez

---

# 5️⃣ ORDER SERVICE

## Configuration Spring Initializr

| Paramètre | Valeur |
|-----------|--------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.1 |
| **Project Metadata** | |
| - Group | `com.microservices` |
| - Artifact | `order-service` |
| - Name | `order-service` |
| - Description | `Order management service` |
| - Package name | `com.microservices.order` |
| - Packaging | `Jar` |
| - Java | `17` |

## Dependencies à ajouter

**Exactement les mêmes que User Service** :

1. Spring Web
2. Spring Security
3. Spring Data JPA
4. PostgreSQL Driver
5. Eureka Discovery Client
6. OpenFeign
7. Validation
8. Spring Boot Actuator
9. Lombok

## Étapes de génération

1. Ajoutez toutes les dépendances
2. Cliquez sur **"GENERATE"**
3. Téléchargez et décompressez

---

# 6️⃣ COMMON LIBRARY

⚠️ **ATTENTION** : La Common Library **ne se génère PAS avec Spring Initializr**.

## Création manuelle

### Méthode 1 : Via IDE (IntelliJ IDEA)

1. Clic droit sur le projet parent → New → Module
2. Choisir "Maven"
3. Nom : `common-lib`
4. Group : `com.microservices`
5. Créer la structure de packages manuellement

### Méthode 2 : Manuelle

1. Créer le dossier `common-lib/`
2. Créer `pom.xml` avec le contenu fourni précédemment
3. Créer `src/main/java/com/microservices/common/`
4. Créer les sous-packages : dto, exception, enums, util

---

# 📁 ORGANISATION DU PROJET

## Étape finale : Création du projet multi-module

### Option A : Création manuelle

1. Créer un dossier racine : `microservices-backend/`
2. Déplacer tous les projets générés dans ce dossier
3. Créer le `pom.xml` parent (fourni précédemment)
4. Modifier chaque `pom.xml` de service pour ajouter la section `<parent>`

### Option B : Via IDE (Recommandé)

#### IntelliJ IDEA

1. **File → New → Project**
2. Choisir "Maven"
3. Nom : `microservices-backend`
4. Cocher "Create as module parent"
5. Créer le projet
6. Ajouter chaque service comme module :
   - Clic droit sur projet → New → Module from Existing Sources
   - Sélectionner chaque dossier de service décompressé
7. Modifier le `pom.xml` parent pour lister tous les modules

#### Eclipse

1. **File → New → Maven Project**
2. Cocher "Create a simple project"
3. Group ID : `com.microservices`
4. Artifact ID : `backend-parent`
5. Packaging : `pom`
6. Cliquer droit sur le projet → Import → Existing Maven Projects
7. Sélectionner chaque service

---

# ✅ CHECKLIST DE VÉRIFICATION

Après génération, vérifiez que chaque service contient :

## Discovery Service
- [ ] `DiscoveryServiceApplication.java`
- [ ] `application.yml`
- [ ] Dépendance `eureka-server`

## API Gateway
- [ ] `GatewayApplication.java`
- [ ] `application.yml`
- [ ] Dépendance `spring-cloud-gateway`
- [ ] Dépendance `eureka-client`
- [ ] Ajouter manuellement : JWT, Security Reactive

## Auth Service
- [ ] `AuthServiceApplication.java`
- [ ] `application.yml`
- [ ] Dépendances : Web, Security, JPA, PostgreSQL, Eureka
- [ ] Ajouter manuellement : JWT, SpringDoc

## User Service
- [ ] `UserServiceApplication.java`
- [ ] `application.yml`
- [ ] Dépendances : Web, Security, JPA, PostgreSQL, Eureka, Feign
- [ ] Ajouter manuellement : Resilience4j, SpringDoc

## Order Service
- [ ] `OrderServiceApplication.java`
- [ ] `application.yml`
- [ ] Dépendances identiques à User Service

## Common Library
- [ ] `pom.xml` configuré
- [ ] Structure de packages créée
- [ ] Pas d'annotation Spring Boot (c'est une librairie)

---

# 🔧 MODIFICATIONS POST-GÉNÉRATION

## Ajouter les dépendances manquantes

### Dans API Gateway
```xml
<!-- Ajouter au pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### Dans Auth, User et Order Services
```xml
<!-- JWT (pour Auth uniquement) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- SpringDoc OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Resilience4j (User et Order uniquement) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

---

# 🎯 ORDRE DE CRÉATION RECOMMANDÉ

1. **Discovery Service** (Premier à démarrer)
2. **Common Library** (Créer manuellement)
3. **Auth Service**
4. **API Gateway**
5. **User Service**
6. **Order Service**

---

# 🚀 COMMANDES DE BUILD

```bash
# Build de tous les modules
cd microservices-backend/
mvn clean install

# Build d'un service spécifique
cd discovery-service/
mvn clean package

# Run un service
mvn spring-boot:run
```

---

# 📌 NOTES IMPORTANTES

- Spring Initializr génère la structure de base uniquement
- Les classes métier (Controller, Service, Repository) doivent être créées manuellement
- Les fichiers de configuration `application.yml` doivent être complétés avec les configurations fournies
- La version de Spring Cloud doit être compatible avec Spring Boot 3.2.x
- Pour Spring Boot 3.2.1, utilisez Spring Cloud 2023.0.0

---

# 🔗 RESSOURCES UTILES

- **Spring Initializr** : https://start.spring.io/
- **Spring Cloud Version Matrix** : https://spring.io/projects/spring-cloud
- **Documentation Eureka** : https://cloud.spring.io/spring-cloud-netflix/
- **Documentation Gateway** : https://spring.io/projects/spring-cloud-gateway
- **JJWT Library** : https://github.com/jwtk/jjwt