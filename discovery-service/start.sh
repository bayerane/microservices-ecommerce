#!/bin/bash

###############################################################################
# Script de démarrage du Discovery Service
# Usage: ./start.sh [dev|prod|test]
###############################################################################

# Couleurs pour l'affichage
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction d'affichage
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Banner
echo -e "${GREEN}"
cat << "EOF"
  ____  _                                    
 |  _ \(_)___  ___ _____   _____ _ __ _   _ 
 | | | | / __|/ __/ _ \ \ / / _ \ '__| | | |
 | |_| | \__ \ (_| (_) \ V /  __/ |  | |_| |
 |____/|_|___/\___\___/ \_/ \___|_|   \__, |
                                       |___/ 
  ____                  _          
 / ___|  ___ _ ____   _(_) ___ ___ 
 \___ \ / _ \ '__\ \ / / |/ __/ _ \
  ___) |  __/ |   \ V /| | (_|  __/
 |____/ \___|_|    \_/ |_|\___\___|
                                    
EOF
echo -e "${NC}"

# Récupération du profil (par défaut: dev)
PROFILE=${1:-dev}

print_info "Démarrage du Discovery Service avec le profil: $PROFILE"

# Vérification Java
if ! command -v java &> /dev/null; then
    print_error "Java n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 ou supérieur est requis. Version actuelle: $JAVA_VERSION"
    exit 1
fi

print_success "Java version: $(java -version 2>&1 | head -n 1)"

# Vérification Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

print_success "Maven version: $(mvn -version | head -n 1)"

# Vérification du port 8761
if lsof -Pi :8761 -sTCP:LISTEN -t >/dev/null 2>&1; then
    print_warning "Le port 8761 est déjà utilisé"
    print_info "Processus utilisant le port:"
    lsof -i :8761
    read -p "Voulez-vous tuer ce processus et continuer? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        PID=$(lsof -ti:8761)
        kill -9 $PID
        print_success "Processus $PID terminé"
    else
        print_error "Arrêt du script"
        exit 1
    fi
fi

# Clean et build
print_info "Nettoyage et compilation du projet..."
if mvn clean package -DskipTests; then
    print_success "Compilation réussie"
else
    print_error "Erreur lors de la compilation"
    exit 1
fi

# Démarrage
print_info "Démarrage du service sur le port 8761..."
JAR_FILE="target/discovery-service-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    print_error "Le fichier JAR n'existe pas: $JAR_FILE"
    exit 1
fi

# Créer le dossier logs s'il n'existe pas
mkdir -p logs

# Lancement avec le profil spécifié
print_info "Lancement avec Spring profile: $PROFILE"
java -jar "$JAR_FILE" --spring.profiles.active="$PROFILE" &

# Récupération du PID
SERVICE_PID=$!
echo $SERVICE_PID > discovery-service.pid

print_success "Service démarré avec PID: $SERVICE_PID"
print_info "Logs disponibles dans: logs/discovery-service.log"
print_info "PID sauvegardé dans: discovery-service.pid"

# Attente du démarrage
print_info "Attente du démarrage du service..."
sleep 10

# Vérification du démarrage
if curl -s http://localhost:8761/actuator/health > /dev/null 2>&1; then
    print_success "Service démarré avec succès!"
    echo ""
    print_info "Dashboard Eureka: ${GREEN}http://localhost:8761${NC}"
    print_info "Health check: ${GREEN}http://localhost:8761/actuator/health${NC}"
    print_info "Info: ${GREEN}http://localhost:8761/actuator/info${NC}"
    echo ""
    print_info "Pour arrêter le service: ./stop.sh"
    print_info "Pour voir les logs: tail -f logs/discovery-service.log"
else
    print_error "Le service n'a pas pu démarrer correctement"
    print_info "Vérifiez les logs: tail -f logs/discovery-service.log"
    exit 1
fi