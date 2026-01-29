# 🔍 Discovery Service (Eureka Server)

## 📋 Description

Le Discovery Service est un serveur Eureka qui permet la découverte automatique de tous les micro-services de l'architecture. Il agit comme un registre central où tous les services s'enregistrent au démarrage.

## 🎯 Fonctionnalités

- ✅ Enregistrement automatique des micro-services
- ✅ Découverte dynamique des instances
- ✅ Health checks automatiques
- ✅ Dashboard web pour visualisation
- ✅ Auto-nettoyage des instances mortes
- ✅ Load balancing côté client

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.8+

### Lancement

```bash
# Via Maven
mvn spring-boot:run

# Via JAR
mvn clean package
java -jar target/discovery-service-1.0.0.jar

# Avec profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Vérification

Le service démarre sur le port **8761**.

**Dashboard Eureka** : http://localhost:8761

Vous devriez voir l'interface web d'Eureka avec la liste des services enregistrés.

## 📊 Endpoints

| Endpoint | Description |
|----------|-------------|
| `http://localhost:8761` | Dashboard Eureka |
| `http://localhost:8761/eureka/apps` | Liste des applications enregistrées (XML) |
| `http://localhost:8761/eureka/apps/{appName}` | Détails d'une application |
| `http://localhost:8761/actuator/health` | Health check |
| `http://localhost:8761/actuator/info` | Informations du service |
| `http://localhost:8761/actuator/metrics` | Métriques |

## 🔧 Configuration

### Ports

- **Développement** : 8761
- **Production** : Configurable via `application-prod.yml`

### Profils Spring

- `dev` : Développement (auto-preservation désactivée)
- `prod` : Production (auto-preservation activée)
- `test` : Tests (configuration minimale)

### Variables d'Environnement

```bash
# Port du serveur
SERVER_PORT=8761

# Profil actif
SPRING_PROFILES_ACTIVE=dev

# Hostname
EUREKA_INSTANCE_HOSTNAME=localhost
```

## 📝 Enregistrement d'un Service Client

Pour qu'un micro-service s'enregistre dans Eureka, ajouter dans son `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Et dans son `application.yml` :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

## 🧪 Tests

```bash
# Lancer les tests
mvn test

# Lancer les tests avec rapport de couverture
mvn clean test jacoco:report
```

## 📈 Monitoring

### Health Check

```bash
curl http://localhost:8761/actuator/health
```

Réponse :
```json
{
  "status": "UP"
}
```

### Informations

```bash
curl http://localhost:8761/actuator/info
```

Réponse :
```json
{
  "app": {
    "name": "Discovery Service",
    "description": "Eureka Server for microservices discovery",
    "version": "1.0.0"
  }
}
```

## 🔒 Sécurité

### Mode Développement

En développement, le dashboard est accessible sans authentification.

### Mode Production

Pour sécuriser Eureka en production, décommenter la configuration dans `SecurityConfig.java` et configurer les credentials.

## 🐛 Résolution de Problèmes

### Le dashboard ne s'affiche pas

Vérifier que le port 8761 n'est pas déjà utilisé :

```bash
# Linux/macOS
lsof -i :8761

# Windows
netstat -ano | findstr :8761
```

### Les services ne s'enregistrent pas

1. Vérifier que le Discovery Service est bien démarré
2. Vérifier l'URL dans `defaultZone` des clients
3. Vérifier les logs pour des erreurs de connexion
4. Attendre 30-60 secondes (délai de registration)

### Auto-préservation activée en dev

Message : "EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP..."

Solution : Vérifier que `enable-self-preservation: false` dans le profil dev.

## 📊 Métriques et Logs

### Logs

Les logs sont écrits dans :
- Console : Format coloré avec horodatage
- Fichier : `logs/discovery-service.log`

### Niveaux de log

- **Dev** : DEBUG pour application, INFO pour Eureka
- **Prod** : INFO pour application, WARN pour Eureka

## 🔄 Mise à Jour

Pour mettre à jour le Discovery Service :

```bash
# Arrêter le service
# Ctrl+C ou kill <PID>

# Récupérer les dernières modifications
git pull

# Rebuild
mvn clean package

# Redémarrer
mvn spring-boot:run
```

## 📚 Ressources

- [Spring Cloud Netflix Documentation](https://spring.io/projects/spring-cloud-netflix)
- [Eureka Wiki](https://github.com/Netflix/eureka/wiki)
- [Architecture Microservices](https://microservices.io/)

## 👥 Auteurs

Baye Rane

## 📄 Licence

Copyright © 2026 - Tous droits réservés