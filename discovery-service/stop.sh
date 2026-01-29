#!/bin/bash

###############################################################################
# Script d'arrêt du Discovery Service
# Usage: ./stop.sh
###############################################################################

# Couleurs
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

PID_FILE="discovery-service.pid"

print_info "Arrêt du Discovery Service..."

# Vérifier si le fichier PID existe
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    
    # Vérifier si le processus existe
    if ps -p $PID > /dev/null 2>&1; then
        print_info "Arrêt du processus avec PID: $PID"
        kill $PID
        
        # Attendre l'arrêt gracieux
        sleep 3
        
        # Vérifier si le processus est arrêté
        if ps -p $PID > /dev/null 2>&1; then
            print_info "Arrêt forcé du processus..."
            kill -9 $PID
        fi
        
        print_success "Service arrêté avec succès"
    else
        print_error "Aucun processus avec PID $PID n'est en cours d'exécution"
    fi
    
    # Supprimer le fichier PID
    rm "$PID_FILE"
else
    print_info "Aucun fichier PID trouvé, recherche du processus sur le port 8761..."
    
    # Essayer de trouver le processus par port
    if command -v lsof &> /dev/null; then
        PID=$(lsof -ti:8761)
        if [ ! -z "$PID" ]; then
            print_info "Processus trouvé avec PID: $PID"
            kill -9 $PID
            print_success "Service arrêté avec succès"
        else
            print_error "Aucun processus trouvé sur le port 8761"
        fi
    else
        print_error "Commande lsof non disponible, impossible de trouver le processus"
    fi
fi

print_info "Arrêt terminé"