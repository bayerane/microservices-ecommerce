#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }

# Bannière ASCII
echo -e "${GREEN}"
cat << "EOF"
  _   _                       ____                      _ce 
 | | | |___  ___ _ __        / ___|  ___ _ ____   _(_) ___ ___ 
 | | | / __|/ _ \ '__| ____  \___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |_| \__ \  __/ |   |____|  ___) |  __/ |   \ V /| | (_|  __/
  \___/|___/\___|_|          |____/ \___|_|    \_/ |_|\___\___|
EOF
echo -e "             Gestion des Profils Utilisateurs"
echo -e "${NC}"

# Compilation
print_info "Compilation Maven en cours..."
if mvn clean package -DskipTests > /dev/null 2>&1; then
    print_success "Compilation réussie."
else
    echo -e "${RED}[ERROR] Échec de la compilation Maven.${NC}"
    exit 1
fi

# Nettoyage port 8082
PID=$(lsof -ti:8082)
if [ ! -z "$PID" ]; then
    print_info "Le port 8082 est déjà utilisé. Libération du processus $PID..."
    kill -9 $PID
    sleep 1
fi

# Lancement
print_info "Lancement du JAR (Port: 8082)..."
mkdir -p logs
java -jar target/user-service-1.0.0.jar --spring.profiles.active=dev > logs/user-service.log 2>&1 &
echo $! > user-service.pid

# Vérification Health
print_info "Attente du démarrage du service..."
MAX_ATTEMPTS=30
COUNT=0

until $(curl --output /dev/null --silent --head --fail http://localhost:8082/actuator/health); do
    printf "${BLUE}.${NC}"
    sleep 2
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_ATTEMPTS ]; then
        echo -e "\n${RED}[ERROR] Le service met trop de temps à démarrer. Vérifiez logs/user-service.log${NC}"
        exit 1
    fi
done

echo -e "\n${GREEN}[SUCCESS] User Service opérationnel !${NC}"
echo -e "${BLUE}URL :${NC} http://localhost:8082/swagger-ui.html"