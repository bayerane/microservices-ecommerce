#!/bin/bash

################################################################################
# 🚀 Script d'Installation Automatique - Microservices E-Commerce
################################################################################
# Ce script automatise l'installation complète du projet pour les développeurs
# Durée estimée : 5-10 minutes (vs. 15-20 minutes manuellement)
#
# Usage:
#   chmod +x setup.sh
#   ./setup.sh
################################################################################

set -e  # Arrêter le script si une erreur se produit

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$PROJECT_DIR/.env"
ENV_EXAMPLE="$PROJECT_DIR/.env.example"
POSTGRES_USER="postgres"
POSTGRES_HOST="localhost"
POSTGRES_PORT="5432"

################################################################################
# Fonctions Utilitaires
################################################################################

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

separator() {
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
}

################################################################################
# Étape 1 : Vérifier les Prérequis
################################################################################

check_prerequisites() {
    log_info "Vérification des prérequis..."
    separator
    
    # Vérifier Java
    if ! command -v java &> /dev/null; then
        log_error "Java n'est pas installé"
        echo "Installez Java 17+ : https://adoptopenjdk.net/"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]*' | cut -d. -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java 17+ est requis (vous avez Java $JAVA_VERSION)"
        exit 1
    fi
    log_success "Java $JAVA_VERSION détecté"
    
    # Vérifier Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven n'est pas installé"
        echo "Installez Maven 3.8+ : https://maven.apache.org/download.cgi"
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | awk '{print $3}')
    log_success "Maven $MVN_VERSION détecté"
    
    # Vérifier PostgreSQL
    if ! command -v psql &> /dev/null; then
        log_error "PostgreSQL n'est pas installé"
        echo "Installez PostgreSQL 13+ : https://www.postgresql.org/download/"
        exit 1
    fi
    
    PSQL_VERSION=$(psql --version | awk '{print $NF}')
    log_success "PostgreSQL $PSQL_VERSION détecté"
    
    # Vérifier Git
    if ! command -v git &> /dev/null; then
        log_error "Git n'est pas installé"
        exit 1
    fi
    
    GIT_VERSION=$(git --version | awk '{print $NF}')
    log_success "Git $GIT_VERSION détecté"
    
    separator
}

################################################################################
# Étape 2 : Configurer les Variables d'Environnement
################################################################################

setup_env() {
    log_info "Configuration du fichier .env..."
    separator
    
    if [ -f "$ENV_FILE" ]; then
        log_warning "Le fichier .env existe déjà"
        read -p "Voulez-vous le réutiliser ? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            log_success "Utilisation du fichier .env existant"
            separator
            return
        fi
    fi
    
    # Copier le fichier example
    if [ ! -f "$ENV_EXAMPLE" ]; then
        log_error "Le fichier .env.example n'existe pas"
        exit 1
    fi
    
    cp "$ENV_EXAMPLE" "$ENV_FILE"
    log_success "Fichier .env créé à partir de .env.example"
    
    # Demander les paramètres PostgreSQL
    read -p "Host PostgreSQL (défaut: localhost) : " input_host
    POSTGRES_HOST=${input_host:-localhost}
    
    read -p "Port PostgreSQL (défaut: 5432) : " input_port
    POSTGRES_PORT=${input_port:-5432}
    
    read -p "Utilisateur PostgreSQL (défaut: postgres) : " input_user
    POSTGRES_USER=${input_user:-postgres}
    
    read -sp "Mot de passe PostgreSQL (défaut: postgres) : " input_password
    POSTGRES_PASSWORD=${input_password:-postgres}
    echo
    
    # Mettre à jour le fichier .env
    sed -i "s/POSTGRES_HOST=.*/POSTGRES_HOST=$POSTGRES_HOST/" "$ENV_FILE"
    sed -i "s/POSTGRES_PORT=.*/POSTGRES_PORT=$POSTGRES_PORT/" "$ENV_FILE"
    sed -i "s/POSTGRES_USER=.*/POSTGRES_USER=$POSTGRES_USER/" "$ENV_FILE"
    sed -i "s/POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=$POSTGRES_PASSWORD/" "$ENV_FILE"
    
    # Générer une clé JWT sécurisée si openssl est disponible
    if command -v openssl &> /dev/null; then
        JWT_SECRET=$(openssl rand -base64 32)
        sed -i "s/JWT_SECRET=.*/JWT_SECRET=$JWT_SECRET/" "$ENV_FILE"
        log_success "Clé JWT générée automatiquement"
    fi
    
    log_success "Fichier .env configuré"
    separator
}

################################################################################
# Étape 3 : Configurer PostgreSQL
################################################################################

setup_postgres() {
    log_info "Configuration de PostgreSQL..."
    separator
    
    # Vérifier si PostgreSQL est en cours d'exécution
    if ! pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" &> /dev/null; then
        log_warning "PostgreSQL n'est pas en cours d'exécution"
        read -p "Voulez-vous démarrer PostgreSQL ? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            if command -v systemctl &> /dev/null; then
                sudo systemctl start postgresql
                log_success "PostgreSQL démarré"
                sleep 2
            else
                log_error "Impossible de démarrer PostgreSQL automatiquement"
                exit 1
            fi
        else
            log_error "PostgreSQL doit être en cours d'exécution"
            exit 1
        fi
    else
        log_success "PostgreSQL est actif"
    fi
    
    # Créer les bases de données
    log_info "Création des bases de données..."
    
    # Utiliser PGPASSWORD pour éviter de demander le mot de passe
    export PGPASSWORD="$POSTGRES_PASSWORD"
    
    for db in auth_db user_db order_db; do
        if psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -lqt | cut -d \| -f 1 | grep -qw "$db"; then
            log_warning "Base de données '$db' existe déjà"
        else
            psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -c "CREATE DATABASE $db;" 2>/dev/null || true
            log_success "Base de données '$db' créée"
        fi
    done
    
    unset PGPASSWORD
    
    log_success "PostgreSQL configuré avec 3 bases de données"
    separator
}

################################################################################
# Étape 4 : Build du Projet
################################################################################

build_project() {
    log_info "Build du projet Maven..."
    separator
    
    cd "$PROJECT_DIR"
    
    # Afficher la progression
    log_info "Cela peut prendre 2-3 minutes..."
    
    if mvn clean install -DskipTests -q; then
        log_success "Build Maven réussi"
    else
        log_error "Le build Maven a échoué"
        read -p "Voulez-vous voir les logs détaillés ? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            mvn clean install -DskipTests
        fi
        exit 1
    fi
    
    separator
}

################################################################################
# Étape 5 : Vérification du Build
################################################################################

verify_build() {
    log_info "Vérification des artefacts générés..."
    separator
    
    JARS=(
        "discovery-service/target/discovery-service-1.0.0.jar"
        "api-gateway/target/api-gateway-1.0.0.jar"
        "auth-service/target/auth-service-1.0.0.jar"
        "user-service/target/user-service-1.0.0.jar"
        "order-service/target/order-service-1.0.0.jar"
    )
    
    all_found=true
    for jar in "${JARS[@]}"; do
        if [ -f "$PROJECT_DIR/$jar" ]; then
            log_success "$(basename $jar) généré"
        else
            log_error "$(basename $jar) non trouvé"
            all_found=false
        fi
    done
    
    if [ "$all_found" = false ]; then
        log_error "Certains artefacts n'ont pas été générés"
        exit 1
    fi
    
    separator
}

################################################################################
# Étape 6 : Afficher les Instructions Finales
################################################################################

print_final_instructions() {
    log_success "Installation réussie ! 🎉"
    separator
    
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  Prochaines étapes pour démarrer les services${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    
    cat << 'EOF'

📌 ÉTAPE 1 : Ouvrir 5 terminaux différents

Terminal 1 - Discovery Service (Eureka) :
$ cd discovery-service && mvn spring-boot:run

Terminal 2 - Auth Service :
$ cd auth-service && mvn spring-boot:run

Terminal 3 - User Service :
$ cd user-service && mvn spring-boot:run

Terminal 4 - Order Service :
$ cd order-service && mvn spring-boot:run

Terminal 5 - API Gateway :
$ cd api-gateway && mvn spring-boot:run

─────────────────────────────────────────────────────────────────────────────

📌 ÉTAPE 2 : Vérifier que les services sont démarrés

✅ Dashboard Eureka : http://localhost:8761
✅ API Gateway : http://localhost:8080
✅ Swagger/OpenAPI : http://localhost:8080/swagger-ui.html

─────────────────────────────────────────────────────────────────────────────

📌 ÉTAPE 3 : Tester les APIs

S'inscrire :
$ curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"dev@example.com","password":"DevPassword123!","firstName":"Dev","lastName":"User"}'

Se connecter :
$ curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"dev@example.com","password":"DevPassword123!"}'

─────────────────────────────────────────────────────────────────────────────

📚 DOCUMENTATION :
• Guide rapide         : SETUP.md
• Documentation complète : README.md
• Déploiement          : GUIDE-DEPLOYMENT.md

❓ SUPPORT :
• Troubleshooting : SETUP.md#troubleshooting
• Contact         : contact@microservices.com

EOF
    
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    separator
}

################################################################################
# Fonction Main
################################################################################

main() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║                                                                            ║${NC}"
    echo -e "${BLUE}║  🚀 Installation Automatique - Microservices E-Commerce                   ║${NC}"
    echo -e "${BLUE}║                                                                            ║${NC}"
    echo -e "${BLUE}║  Durée estimée : 5-10 minutes                                             ║${NC}"
    echo -e "${BLUE}║  Date : $(date +'%d/%m/%Y')                                                      ║${NC}"
    echo -e "${BLUE}║                                                                            ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    # Exécuter chaque étape
    check_prerequisites
    setup_env
    setup_postgres
    build_project
    verify_build
    print_final_instructions
    
    log_success "Configuration complète !"
    log_info "Vous pouvez maintenant démarrer les services en suivant les instructions ci-dessus"
}

################################################################################
# Exécution
################################################################################

# Vérifier si le script est exécuté depuis le répertoire correct
if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
    log_error "Le script doit être exécuté depuis le répertoire racine du projet"
    exit 1
fi

# Lancer l'installation
main