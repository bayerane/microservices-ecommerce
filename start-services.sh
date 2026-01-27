#!/bin/bash

################################################################################
# 🚀 Script de Démarrage des Services - Microservices E-Commerce
################################################################################
# Ce script lance automatiquement tous les services dans le bon ordre
# avec des options pour différents modes de lancement
#
# Usage:
#   chmod +x start-services.sh
#   ./start-services.sh              # Mode interactif par défaut
#   ./start-services.sh --parallel   # Lancer les services en parallèle
#   ./start-services.sh --background # Lancer en arrière-plan
#   ./start-services.sh --stop       # Arrêter tous les services
#   ./start-services.sh --help       # Afficher l'aide
################################################################################

set -e

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODE="${1:-interactive}"  # Mode par défaut : interactif
PIDS_FILE="/tmp/microservices-pids.txt"
LOG_DIR="/tmp/microservices-logs"
LAUNCH_DELAY=3  # Délai entre les lancements (en secondes)

# Services à lancer dans l'ordre
SERVICES=(
    "discovery-service:8761"
    "auth-service:8081"
    "user-service:8082"
    "order-service:8083"
    "api-gateway:8080"
)

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

log_service() {
    echo -e "${CYAN}🔧 $1${NC}"
}

separator() {
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
}

print_usage() {
    cat << 'EOF'
🚀 Script de Démarrage des Services - Microservices E-Commerce

USAGE:
    ./start-services.sh [OPTION]

OPTIONS:
    (aucune)        Mode interactif (par défaut)
                    Vous ouvre 5 terminaux, un par service
    
    --parallel      Lance tous les services en parallèle
                    Dans le même terminal avec des logs entrelacés
    
    --background    Lance tous les services en arrière-plan
                    Les logs sont sauvegardés dans /tmp/microservices-logs/
    
    --stop          Arrête tous les services en cours d'exécution
    
    --status        Affiche le statut de tous les services
    
    --logs          Affiche les logs en temps réel (mode background)
    
    --help          Affiche ce message d'aide

EXEMPLES:
    # Mode interactif (5 terminaux)
    ./start-services.sh
    
    # Mode arrière-plan avec logs
    ./start-services.sh --background
    tail -f /tmp/microservices-logs/api-gateway.log
    
    # Arrêter tous les services
    ./start-services.sh --stop

EOF
}

check_java() {
    if ! command -v java &> /dev/null; then
        log_error "Java n'est pas installé"
        exit 1
    fi
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        log_error "Maven n'est pas installé"
        exit 1
    fi
}

check_port() {
    local port=$1
    local service=$2
    
    if lsof -i :$port &> /dev/null; then
        log_warning "Le port $port est déjà utilisé (service: $service)"
        return 1
    fi
    return 0
}

check_prerequisites() {
    log_info "Vérification des prérequis..."
    separator
    
    check_java
    check_maven
    
    log_success "Java et Maven détectés"
    
    # Vérifier les ports
    log_info "Vérification des ports..."
    for service_info in "${SERVICES[@]}"; do
        service="${service_info%:*}"
        port="${service_info#*:}"
        
        if ! check_port "$port" "$service"; then
            read -p "Voulez-vous continuer quand même ? (y/n) " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                log_error "Annulation"
                exit 1
            fi
        fi
    done
    
    separator
}

################################################################################
# Mode Interactif
################################################################################

start_interactive() {
    log_info "Mode INTERACTIF - Ouverture de 5 terminaux"
    separator
    
    check_prerequisites
    
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  5 terminaux vont s'ouvrir - Un service par terminal${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo ""
    
    local terminal_cmd=""
    
    # Détecter l'émulateur de terminal disponible
    if command -v gnome-terminal &> /dev/null; then
        terminal_cmd="gnome-terminal"
    elif command -v xterm &> /dev/null; then
        terminal_cmd="xterm"
    elif command -v konsole &> /dev/null; then
        terminal_cmd="konsole"
    elif command -v xfce4-terminal &> /dev/null; then
        terminal_cmd="xfce4-terminal"
    else
        log_error "Aucun émulateur de terminal trouvé"
        log_info "Veuillez lancer manuellement dans des terminaux séparés :"
        for service_info in "${SERVICES[@]}"; do
            service="${service_info%:*}"
            echo "cd $PROJECT_DIR/$service && mvn spring-boot:run"
        done
        exit 1
    fi
    
    # Lancer chaque service dans un nouveau terminal
    for service_info in "${SERVICES[@]}"; do
        service="${service_info%:*}"
        port="${service_info#*:}"
        
        case "$terminal_cmd" in
            gnome-terminal)
                gnome-terminal -- bash -c "cd $PROJECT_DIR/$service && echo '🚀 Démarrage de $service (port $port)' && mvn spring-boot:run; bash"
                ;;
            konsole)
                konsole -e bash -c "cd $PROJECT_DIR/$service && echo '🚀 Démarrage de $service (port $port)' && mvn spring-boot:run; bash"
                ;;
            xfce4-terminal)
                xfce4-terminal -e "bash -c 'cd $PROJECT_DIR/$service && echo \"🚀 Démarrage de $service (port $port)\" && mvn spring-boot:run; bash'"
                ;;
            xterm)
                xterm -e "cd $PROJECT_DIR/$service && echo '🚀 Démarrage de $service (port $port)' && mvn spring-boot:run; bash" &
                ;;
        esac
        
        sleep 2
        echo "✅ Terminal ouvert pour $service (port $port)"
    done
    
    separator
    log_success "Tous les services sont en cours de démarrage!"
    print_final_instructions
}

################################################################################
# Mode Parallèle
################################################################################

start_parallel() {
    log_info "Mode PARALLÈLE - Tous les services dans le même terminal"
    separator
    
    check_prerequisites
    
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  Lancement de tous les services en parallèle${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo ""
    
    PIDS=()
    
    for service_info in "${SERVICES[@]}"; do
        service="${service_info%:*}"
        port="${service_info#*:}"
        
        log_service "Démarrage de $service (port $port)..."
        
        (
            cd "$PROJECT_DIR/$service"
            mvn spring-boot:run 2>&1 | sed "s/^/[$service] /"
        ) &
        
        PIDS+=($!)
        sleep "$LAUNCH_DELAY"
    done
    
    # Sauvegarder les PIDs
    echo "${PIDS[@]}" > "$PIDS_FILE"
    
    separator
    log_success "Tous les services sont en cours de démarrage!"
    log_info "PIDs sauvegardés dans $PIDS_FILE"
    
    # Attendre que tous les processus se terminent
    for pid in "${PIDS[@]}"; do
        wait "$pid" || true
    done
}

################################################################################
# Mode Arrière-Plan
################################################################################

start_background() {
    log_info "Mode ARRIÈRE-PLAN - Logs sauvegardés"
    separator
    
    check_prerequisites
    
    # Créer le répertoire des logs
    mkdir -p "$LOG_DIR"
    
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  Lancement de tous les services en arrière-plan${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════════════════════${NC}"
    echo ""
    
    PIDS=()
    
    for service_info in "${SERVICES[@]}"; do
        service="${service_info%:*}"
        port="${service_info#*:}"
        log_file="$LOG_DIR/$service.log"
        
        log_service "Démarrage de $service (port $port)..."
        log_info "Logs sauvegardés dans $log_file"
        
        (
            cd "$PROJECT_DIR/$service"
            mvn spring-boot:run > "$log_file" 2>&1
        ) &
        
        PID=$!
        PIDS+=($PID)
        echo "$service:$PID" >> "$PIDS_FILE"
        
        sleep "$LAUNCH_DELAY"
    done
    
    separator
    log_success "Tous les services sont lancés en arrière-plan!"
    log_info "PIDs sauvegardés dans $PIDS_FILE"
    
    print_background_instructions
}

################################################################################
# Arrêter les Services
################################################################################

stop_services() {
    log_info "Arrêt de tous les services..."
    separator
    
    if [ ! -f "$PIDS_FILE" ]; then
        log_warning "Aucun fichier PIDs trouvé"
        log_info "Vous pouvez arrêter manuellement avec : pkill -f 'spring-boot:run'"
        return
    fi
    
    while IFS= read -r line; do
        if [[ "$line" == *":"* ]]; then
            service="${line%:*}"
            pid="${line#*:}"
        else
            pid="$line"
            service="?"
        fi
        
        if kill -0 "$pid" 2>/dev/null; then
            kill "$pid" 2>/dev/null || true
            log_success "Arrêt de $service (PID: $pid)"
        fi
    done < "$PIDS_FILE"
    
    rm -f "$PIDS_FILE"
    log_success "Tous les services sont arrêtés"
    separator
}

################################################################################
# Statut des Services
################################################################################

check_services_status() {
    log_info "Vérification du statut des services..."
    separator
    
    for service_info in "${SERVICES[@]}"; do
        service="${service_info%:*}"
        port="${service_info#*:}"
        
        if curl -s http://localhost:$port/actuator/health &> /dev/null; then
            log_success "$service ($port) - ACTIF"
        else
            log_warning "$service ($port) - INACTIF"
        fi
    done
    
    separator
}

################################################################################
# Afficher les Logs
################################################################################

show_logs() {
    if [ ! -d "$LOG_DIR" ]; then
        log_error "Aucun dossier de logs trouvé"
        exit 1
    fi
    
    log_info "Affichage des logs en temps réel..."
    echo "Appuyez sur Ctrl+C pour quitter"
    separator
    
    tail -f "$LOG_DIR"/*.log 2>/dev/null
}

################################################################################
# Instructions Finales
################################################################################

print_final_instructions() {
    cat << 'EOF'

📌 INSTRUCTIONS DE VÉRIFICATION :

1️⃣  Vérifier le Dashboard Eureka :
    http://localhost:8761
    ✅ Vous devez voir 5 services enregistrés

2️⃣  Vérifier les Health Checks :
    curl http://localhost:8081/actuator/health  (Auth Service)
    curl http://localhost:8082/actuator/health  (User Service)
    curl http://localhost:8083/actuator/health  (Order Service)
    curl http://localhost:8080/actuator/health  (API Gateway)

3️⃣  Accéder à Swagger UI :
    http://localhost:8080/swagger-ui.html

4️⃣  Tester l'API :
    curl -X POST http://localhost:8080/api/auth/register \
      -H "Content-Type: application/json" \
      -d '{"email":"test@example.com","password":"Test123!","firstName":"Test","lastName":"User"}'

📚 Documentation :
   • Guide rapide      : SETUP.md
   • Aide complète     : README.md
   • API Documentation : http://localhost:8080/swagger-ui.html

❌ Pour arrêter tous les services :
   Appuyez sur Ctrl+C dans chaque terminal

EOF
    separator
}

print_background_instructions() {
    cat << EOF
📌 LOGS ET GESTION :

📁 Dossier des logs : $LOG_DIR/

Voir les logs en temps réel :
  ./start-services.sh --logs
  
Voir les logs d'un service spécifique :
  tail -f $LOG_DIR/api-gateway.log
  tail -f $LOG_DIR/auth-service.log
  tail -f $LOG_DIR/discovery-service.log
  
Arrêter tous les services :
  ./start-services.sh --stop

Vérifier le statut :
  ./start-services.sh --status

EOF
    separator
    print_final_instructions
}

################################################################################
# Main
################################################################################

main() {
    case "$MODE" in
        --help)
            print_usage
            ;;
        --stop)
            stop_services
            ;;
        --status)
            check_services_status
            ;;
        --logs)
            show_logs
            ;;
        --parallel)
            start_parallel
            ;;
        --background)
            start_background
            ;;
        interactive|"")
            start_interactive
            ;;
        *)
            log_error "Option inconnue: $MODE"
            echo ""
            print_usage
            exit 1
            ;;
    esac
}

# Vérifier que le script est lancé depuis le bon répertoire
if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
    log_error "Le script doit être lancé depuis le répertoire racine du projet"
    exit 1
fi

# Afficher l'en-tête
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                                                                            ║${NC}"
echo -e "${BLUE}║  🚀 Démarrage des Services - Microservices E-Commerce                     ║${NC}"
echo -e "${BLUE}║                                                                            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Lancer le programme
main