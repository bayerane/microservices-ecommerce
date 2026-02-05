#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() { echo -e "${YELLOW}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

PID_FILE="auth-service.pid"

print_info "Arrêt du Auth Service..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        sleep 3
        ps -p $PID > /dev/null 2>&1 && kill -9 $PID
        print_success "Service (PID $PID) arrêté"
    fi
    rm "$PID_FILE"
else
    # Fallback si le fichier PID est manquant
    PID=$(lsof -ti:8081)
    if [ ! -z "$PID" ]; then
        kill -9 $PID
        print_success "Service sur port 8081 arrêté"
    else
        print_error "Aucun processus trouvé sur le port 8081"
    fi
fi