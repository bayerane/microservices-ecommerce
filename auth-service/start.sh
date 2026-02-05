#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }

# Banner
echo -e "${GREEN}"
cat << "EOF"
    _         _   _     ____                      
   / \  _   _| |_| |__ / ___|  ___ _ ____   _(_) ___ ___ 
  / _ \| | | | __| '_ \\___ \ / _ \ '__\ \ / / |/ __/ _ \
 / ___ \ |_| | |_| | | |___) |  __/ |   \ V /| | (_|  __/
/_/   \_\__,_|\__|_| |_|____/ \___|_|    \_/ |_|\___\___|
EOF
echo -e "${NC}"

PROFILE=${1:-dev}
print_info "Démarrage du Auth Service avec le profil: $PROFILE"

# Vérification Java 17+
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17+ requis. Actuel: $JAVA_VERSION"; exit 1
fi

# Vérification du port 8081
if lsof -Pi :8081 -sTCP:LISTEN -t >/dev/null 2>&1; then
    print_warning "Port 8081 déjà utilisé. Voulez-vous tuer le processus? (y/n)"
    read -n 1 -r; echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        kill -9 $(lsof -ti:8081); sleep 2
    else
        exit 1
    fi
fi

# Build
print_info "Compilation en cours..."
if mvn clean package -DskipTests; then
    print_success "Compilation réussie"
else
    print_error "Échec de la compilation"; exit 1
fi

# Démarrage
mkdir -p logs
JAR_FILE="target/auth-service-1.0.0.jar"
java -jar "$JAR_FILE" --spring.profiles.active="$PROFILE" &
echo $! > auth-service.pid

print_success "Service lancé avec PID: $(cat auth-service.pid)"

# Attente et vérification
print_info "Vérification du démarrage..."
MAX_ATTEMPTS=20
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
        print_success "Auth Service est opérationnel!"
        echo -e "URL: ${GREEN}http://localhost:8081/swagger-ui.html${NC}"
        exit 0
    fi
    echo -n "."
    sleep 3
    ATTEMPT=$((ATTEMPT+1))
done

print_error "Le service n'a pas démarré. Vérifiez logs/auth-service.log"; exit 1