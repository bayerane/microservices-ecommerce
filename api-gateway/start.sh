#!/bin/bash

###############################################################################
# Script de démarrage de l'API Gateway
# Usage: ./start.sh [dev|prod|test]
###############################################################################

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

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
    _    ____ ___    ____       _                           
   / \  |  _ \_ _|  / ___| __ _| |_ _____      ____ _ _   _ 
  / _ \ | |_) | |  | |  _ / _` | __/ _ \ \ /\ / / _` | | | |
 / ___ \|  __/| |  | |_| | (_| | ||  __/\ V  V / (_| | |_| |
/_/   \_\_|  |___|  \____|\__,_|\__\___| \_/\_/ \__,_|\__, |
                                                       |___/ 
EOF
echo -e "${NC}"

# Récupération du profil
PROFILE=${1:-dev}

print_info "Démarrage de l'API Gateway avec le profil: $PROFILE"

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
    print_error "Maven n'est pas installé"
    exit 1
fi

print_success "Maven version: $(mvn -version | head -n 1)"

# Vérification Eureka
print_info "Vérification du Discovery Service..."
if curl -s http://localhost:8761/actuator/health > /dev/null 2>&1; then
    print_success "Discovery Service accessible"
else
    print_warning "Discovery Service non accessible sur http://localhost:8761"
    print_warning "Le Gateway démarrera mais ne pourra pas router les requêtes"
    read -p "Continuer quand même? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Vérification du port 8080
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    print_warning "Le port 8080 est déjà utilisé"
    print_info "Processus utilisant le port:"
    lsof -i :8080
    read -p "Voulez-vous tuer ce processus? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        PID=$(lsof -ti:8080)
        kill -9 $PID
        print_success "Processus $PID terminé"
        sleep 2
    else
        print_error "Arrêt du script"
        exit 1
    fi
fi

# Build
print_info "Nettoyage et compilation..."
if mvn clean package -DskipTests; then
    print_success "Compilation réussie"
else
    print_error "Erreur lors de la compilation"
    exit 1
fi

# Démarrage
print_info "Démarrage du Gateway..."
JAR_FILE="target/api-gateway-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    print_error "Le fichier JAR n'existe pas: $JAR_FILE"
    exit 1
fi

# Créer dossier logs
mkdir -p logs

# Lancement
print_info "Lancement avec profil: $PROFILE"
java -jar "$JAR_FILE" --spring.profiles.active="$PROFILE" &

SERVICE_PID=$!
echo $SERVICE_PID > api-gateway.pid

print_success "Service démarré avec PID: $SERVICE_PID"
print_info "Logs: logs/api-gateway.log"
print_info "PID sauvegardé dans: api-gateway.pid"

# Attente du démarrage
print_info "Attente du démarrage (peut prendre 30-60s)..."
sleep 15

# Vérification
MAX_ATTEMPTS=20
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "Gateway démarré avec succès!"
        echo ""
        print_info "URLs importantes:"
        echo -e "${GREEN}http://localhost:8080${NC} - Gateway"
        echo -e "${GREEN}http://localhost:8080/actuator/health${NC} - Health check"
        echo -e "${GREEN}http://localhost:8080/actuator/gateway/routes${NC} - Routes"
        echo ""
        print_info "Pour tester:"
        echo "curl http://localhost:8080/api/auth/health"
        echo ""
        print_info "Pour arrêter: ./stop.sh"
        exit 0
    fi
    ATTEMPT=$((ATTEMPT+1))
    echo -n "."
    sleep 3
done

print_error "Le service n'a pas démarré dans le délai imparti"
print_info "Vérifier les logs: tail -f logs/api-gateway.log"
exit 1