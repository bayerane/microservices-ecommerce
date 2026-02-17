#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info()    { echo -e "${BLUE}[INFO]${NC}    $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warn()    { echo -e "${YELLOW}[WARN]${NC}    $1"; }
print_error()   { echo -e "${RED}[ERROR]${NC}   $1"; }

# ─── Bannière ASCII ────────────────────────────────────────────────────────────
echo -e "${GREEN}"
cat << "EOF"
   ___          _             ____                  _
  / _ \ _ __ __| | ___ _ __  / ___|  ___ _ ____   _(_) ___ ___
 | | | | '__/ _` |/ _ \ '__| \___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |_| | | | (_| |  __/ |     ___) |  __/ |   \ V /| | (_|  __/
  \___/|_|  \__,_|\___|_|    |____/ \___|_|    \_/ |_|\___\___|
EOF
echo -e "              Gestion des Commandes — Port 8083"
echo -e "${NC}"

# ─── Variables ────────────────────────────────────────────────────────────────
PORT=8083
JAR_NAME="order-service-1.0.0.jar"
PID_FILE="order-service.pid"
LOG_FILE="logs/order-service.log"
MAX_ATTEMPTS=30

# ─── Compilation Maven ────────────────────────────────────────────────────────
print_info "Compilation Maven en cours..."
if mvn clean package -DskipTests > /dev/null 2>&1; then
    print_success "Compilation réussie."
else
    print_error "Échec de la compilation Maven."
    print_error "Exécutez 'mvn clean package' pour voir les erreurs."
    exit 1
fi

# ─── Vérification du JAR ──────────────────────────────────────────────────────
if [ ! -f "target/$JAR_NAME" ]; then
    print_error "JAR introuvable : target/$JAR_NAME"
    exit 1
fi

# ─── Libération du port 8083 ─────────────────────────────────────────────────
PID=$(lsof -ti:$PORT 2>/dev/null)
if [ -n "$PID" ]; then
    print_warn "Le port $PORT est occupé (PID: $PID). Libération en cours..."
    kill -9 "$PID"
    sleep 1
    print_success "Port $PORT libéré."
fi

# ─── Création du dossier logs ────────────────────────────────────────────────
mkdir -p logs

# ─── Lancement du JAR ────────────────────────────────────────────────────────
print_info "Lancement de $JAR_NAME (Port: $PORT)..."
java -jar "target/$JAR_NAME" \
     --spring.profiles.active=dev \
     > "$LOG_FILE" 2>&1 &

echo $! > "$PID_FILE"
print_info "Processus démarré (PID: $(cat $PID_FILE)) — logs : $LOG_FILE"

# ─── Attente du démarrage (Health Check) ─────────────────────────────────────
print_info "Attente du démarrage du service..."
COUNT=0

until curl --output /dev/null --silent --fail "http://localhost:$PORT/actuator/health"; do
    printf "${BLUE}.${NC}"
    sleep 2
    COUNT=$((COUNT + 1))
    if [ $COUNT -ge $MAX_ATTEMPTS ]; then
        echo ""
        print_error "Le service n'a pas démarré après $((MAX_ATTEMPTS * 2))s."
        print_error "Consultez les logs : $LOG_FILE"
        print_error "Dernières lignes :"
        tail -20 "$LOG_FILE"
        exit 1
    fi
done

# ─── Succès ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
print_success "Order Service opérationnel ! 🚀"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "  ${BLUE}Swagger UI :${NC}   http://localhost:$PORT/swagger-ui.html"
echo -e "  ${BLUE}Health     :${NC}   http://localhost:$PORT/actuator/health"
echo -e "  ${BLUE}PID        :${NC}   $(cat $PID_FILE)"
echo -e "  ${BLUE}Logs       :${NC}   $LOG_FILE"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "  Arrêt : ${YELLOW}./stop.sh${NC}"