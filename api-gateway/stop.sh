#!/bin/bash

###############################################################################
# Script d'arrêt de l'API Gateway
# Usage: ./stop.sh
###############################################################################

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

PID_FILE="api-gateway.pid"

print_info "Arrêt de l'API Gateway..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    
    if ps -p $PID > /dev/null 2>&1; then
        print_info "Arrêt du processus avec PID: $PID"
        kill $PID
        
        sleep 3
        
        if ps -p $PID > /dev/null 2>&1; then
            print_info "Arrêt forcé..."
            kill -9 $PID
        fi
        
        print_success "Gateway arrêté"
    else
        print_error "Aucun processus avec PID $PID"
    fi
    
    rm "$PID_FILE"
else
    print_info "Recherche du processus sur le port 8080..."
    
    if command -v lsof &> /dev/null; then
        PID=$(lsof -ti:8080)
        if [ ! -z "$PID" ]; then
            print_info "Processus trouvé: $PID"
            kill -9 $PID
            print_success "Gateway arrêté"
        else
            print_error "Aucun processus sur le port 8080"
        fi
    fi
fi

print_info "Arrêt terminé"