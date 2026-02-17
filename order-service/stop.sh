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

# ─── Bannière ASCII ───────────────────────────────────────────────────────────
echo -e "${RED}"
cat << "EOF"
   ___          _             ____                  _
  / _ \ _ __ __| | ___ _ __  / ___|  ___ _ ____   _(_) ___ ___
 | | | | '__/ _` |/ _ \ '__| \___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |_| | | | (_| |  __/ |     ___) |  __/ |   \ V /| | (_|  __/
  \___/|_|  \__,_|\___|_|    |____/ \___|_|    \_/ |_|\___\___|
EOF
echo -e "                      Arrêt du service"
echo -e "${NC}"

# ─── Variables ────────────────────────────────────────────────────────────────
PORT=8083
PID_FILE="order-service.pid"

# ─── Arrêt via fichier PID ────────────────────────────────────────────────────
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")

    if kill -0 "$PID" 2>/dev/null; then
        print_info "Arrêt du processus PID $PID (SIGTERM)..."
        kill -15 "$PID"

        # Attente de l'arrêt gracieux (max 15s)
        COUNT=0
        while kill -0 "$PID" 2>/dev/null; do
            printf "${YELLOW}.${NC}"
            sleep 1
            COUNT=$((COUNT + 1))
            if [ $COUNT -ge 15 ]; then
                echo ""
                print_warn "Arrêt forcé (SIGKILL)..."
                kill -9 "$PID" 2>/dev/null
                break
            fi
        done

        echo ""
        print_success "Processus PID $PID arrêté."
    else
        print_warn "Le processus PID $PID n'est plus actif."
    fi

    rm -f "$PID_FILE"

# ─── Fallback : arrêt par port ───────────────────────────────────────────────
else
    print_warn "Fichier $PID_FILE introuvable. Recherche par port $PORT..."
    PID=$(lsof -ti:$PORT 2>/dev/null)

    if [ -n "$PID" ]; then
        print_info "Processus trouvé sur le port $PORT (PID: $PID). Arrêt..."
        kill -15 "$PID"
        sleep 3
        if kill -0 "$PID" 2>/dev/null; then
            kill -9 "$PID" 2>/dev/null
        fi
        print_success "Processus PID $PID arrêté."
    else
        print_info "Aucun processus actif sur le port $PORT."
    fi
fi

# ─── Vérification finale du port ─────────────────────────────────────────────
sleep 1
if lsof -ti:$PORT &>/dev/null 2>&1; then
    print_warn "Le port $PORT semble encore occupé."
else
    print_success "Port $PORT libéré."
fi

# ─── Résumé ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
print_success "Order Service arrêté avec succès. 🛑"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "  Relancer : ${YELLOW}./start.sh${NC}"