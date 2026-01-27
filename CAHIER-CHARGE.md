# 📘 CAHIER DES CHARGES

## Projet : Architecture Micro-services Backend avec Spring Boot

## 1️⃣ Contexte et justification

Dans un contexte de développement d'applications modernes, robustes et
sécurisées, ce projet vise à mettre en place une **architecture
micro-services backend sécurisée** basée sur **Spring Boot et Spring
Cloud**.\
Il servira de **référence technique et pédagogique** pour la mise en
œuvre de micro-services sécurisés, évolutifs et maintenables.

## 2️⃣ Objectifs du projet

### Objectif général

Concevoir et implémenter une **architecture micro-services backend
sécurisée**, répondant aux standards actuels de développement.

### Objectifs spécifiques

-   Implémenter une sécurité avancée avec authentification et
    > autorisation

-   Gérer les rôles utilisateurs (**ADMIN**, **USER**)

-   Séparer clairement les responsabilités des services

-   Permettre la communication fiable entre micro-services

-   Centraliser l'accès aux services

-   Assurer la persistance des données

## 3️⃣ Périmètre du projet

Le projet couvre :

-   Le **backend**

-   Les **API REST**

-   La **sécurité**

-   La **gestion des utilisateurs et des commandes**

-   La **documentation automatique des API**

Ne sont pas inclus :

-   Interface frontend

-   Applications mobiles

## 4️⃣ Architecture du système

L'architecture du projet repose sur les composants suivants :

### 🔹 Discovery Service

-   Découverte dynamique des micro-services

-   Enregistrement automatique des services

### 🔹 API Gateway

-   Point d'entrée unique de l'application

-   Routage des requêtes

-   Application des règles de sécurité globales

### 🔹 Auth Service (sécurité)

-   Authentification des utilisateurs

-   Gestion des rôles (ADMIN, USER)

-   Génération et validation des tokens (JWT)

### 🔹 User Service

-   Gestion complète des utilisateurs

-   Communication avec Auth Service

### 🔹 Order Service

-   Gestion complète des commandes

-   Communication avec User Service

## 5️⃣ Fonctionnalités attendues

### 🔐 Sécurité et gestion des rôles

-   Authentification par login/mot de passe

-   Autorisation basée sur les rôles (ADMIN, USER)

-   Protection des endpoints sensibles

-   Gestion sécurisée des mots de passe (hashage)

### 👤 User Service

-   Créer un utilisateur

-   Modifier un utilisateur

-   Supprimer un utilisateur

-   Modifier le mot de passe

-   Récupérer le profil d'un utilisateur

-   Lister les utilisateurs (ADMIN uniquement)

### 📦 Order Service

-   Créer une commande

-   Modifier une commande

-   Supprimer une commande

-   Annuler une commande

-   Récupérer les détails d'une commande

-   Lister les commandes d'un utilisateur

### 🌐 API Gateway

-   Routage des requêtes vers les micro-services

-   Centralisation des règles de sécurité

-   Gestion des erreurs globales

## 6️⃣ Contraintes techniques

-   Langage : **Java**

-   Framework : **Spring Boot**

-   Architecture : **Micro-services**

-   Sécurité : **Spring Security + JWT**

-   Communication : **REST (JSON)**

-   Découverte de services : **Spring Cloud Eureka**

-   Base de données : **PostgreSQL**

-   Outil de build : **Maven**

-   Documentation API : **Swagger / OpenAPI**

## 7️⃣ Exigences non fonctionnelles

-   Sécurité conforme aux bonnes pratiques

-   Données persistantes et cohérentes

-   Services indépendants et déployables séparément

-   Temps de réponse inférieur à 2 secondes

-   Architecture évolutive et maintenable

-   APIs documentées et accessibles via Swagger

## 8️⃣ Livrables attendus

-   Code source complet des micro-services

-   Fichiers de configuration (application.yml)

-   Scripts de création de base de données PostgreSQL

-   Documentation Swagger accessible via navigateur

-   Documentation technique du projet

-   Diagramme d'architecture

## 9️⃣ Critères de validation

-   Authentification fonctionnelle avec gestion des rôles

-   Accès contrôlé selon les profils ADMIN / USER

-   Fonctionnalités CRUD utilisateurs et commandes opérationnelles

-   Données correctement persistées dans PostgreSQL

-   APIs accessibles et documentées via Swagger

-   Services enregistrés dans le Discovery Service

## 🔟 Perspectives d'évolution

-   Ajout d'un Config Server

-   Déploiement avec Docker et Docker Compose

-   Monitoring (Prometheus, Grafana)

-   Logs centralisés (ELK)

-   Communication asynchrone (Kafka / RabbitMQ)