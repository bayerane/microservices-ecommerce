#!/bin/bash
# ==============================================================================
# stop.sh - Script d'arrêt de l'architecture microservices
#
# Ordre d'arrêt (inverse du démarrage) :
#   1. Order Service
#   2. User Service
#   3. API Gateway
#   4. Auth Service
#   5. Discovery Service
#
# Usage :
#   chmod +x stop.sh
#   ./stop.sh            → arrêt propre (SIGTERM + attente)
#   ./stop.sh --force    → arrêt forcé (SIGKILL immédiat)
#   ./stop.sh --clean    → arrêt + suppression des logs et PIDs
#
# @author Baye Rane
# @version 1.0
# ==============================================================================

set -euo pipefail

# ==============================================================================
# CONFIGURATION
# ==============================================================================

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOGS_DIR="$BASE_DIR/logs"
PIDS_DIR="$BASE_DIR/.pids"

# Timeout d'arrêt gracieux par service (secondes)
STOP_TIMEOUT=30

# Mode d'arrêt
FORCE=false
CLEAN=false

# Services dans l'ordre d'arrêt
SERVICES=(
    "order-service:Order Service:8083"
    "user-service:User Service:8082"
    "api-gateway:API Gateway:8080"
    "auth-service:Auth Service:8081"
    "discovery-service:Discovery Service:8761"
)

# ==============================================================================
# COULEURS
# ==============================================================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# ==============================================================================
# FONCTIONS UTILITAIRES
# ==============================================================================

log_info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
log_success() { echo -e "${GREEN}[OK]${NC}    $*"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }
log_step()    { echo -e "\n${BOLD}${CYAN}━━━ $* ━━━${NC}"; }

print_banner() {
    echo -e "${BOLD}${RED}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║       🛑  Microservices - Arrêt                 ║"
    echo "║       Auth • User • Order • Gateway • Eureka    ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Vérifie si un PID est actif
is_pid_alive() {
    local pid=$1
    kill -0 "$pid" 2>/dev/null
}

# Attend que le port soit libéré
wait_for_port_free() {
    local service=$1
    local port=$2
    local elapsed=0

    while lsof -i :"$port" &>/dev/null 2>&1; do
        if [ "$elapsed" -ge "$STOP_TIMEOUT" ]; then
            log_warn "$service : timeout atteint. Le port $port n'est pas libéré."
            return 1
        fi
        printf "."
        sleep 1
        elapsed=$((elapsed + 1))
    done
    echo ""
    return 0
}

# ==============================================================================
# ARRÊT PAR PID FILE
# ==============================================================================

stop_by_pid_file() {
    local service_name=$1
    local display_name=$2
    local port=$3
    local pid_file="$PIDS_DIR/$service_name.pid"

    if [ ! -f "$pid_file" ]; then
        log_warn "$display_name : pas de fichier PID trouvé ($pid_file)"
        # Tentative par port en fallback
        stop_by_port "$display_name" "$port"
        return
    fi

    local pid
    pid=$(cat "$pid_file")

    if ! is_pid_alive "$pid"; then
        log_warn "$display_name : le processus PID $pid n'existe plus"
        rm -f "$pid_file"
        return
    fi

    log_info "$display_name : arrêt du processus PID $pid..."

    if [ "$FORCE" = true ]; then
        # Arrêt immédiat (SIGKILL)
        kill -9 "$pid" 2>/dev/null || true
        log_success "$display_name : arrêt forcé (SIGKILL)"
    else
        # Arrêt gracieux (SIGTERM)
        kill -15 "$pid" 2>/dev/null || true
        log_info "$display_name : signal SIGTERM envoyé. Attente de l'arrêt..."

        local elapsed=0
        while is_pid_alive "$pid"; do
            if [ "$elapsed" -ge "$STOP_TIMEOUT" ]; then
                log_warn "$display_name : timeout ($STOP_TIMEOUT s). Envoi de SIGKILL..."
                kill -9 "$pid" 2>/dev/null || true
                break
            fi
            printf "."
            sleep 1
            elapsed=$((elapsed + 1))
        done
        echo ""
    fi

    # Vérification finale
    if ! is_pid_alive "$pid"; then
        log_success "$display_name : arrêté avec succès (PID $pid)"
        rm -f "$pid_file"
    else
        log_error "$display_name : impossible d'arrêter le processus PID $pid"
    fi

    # Attente que le port soit libéré
    log_info "$display_name : libération du port $port..."
    wait_for_port_free "$display_name" "$port" && \
        log_success "$display_name : port $port libéré" || \
        log_warn "$display_name : le port $port semble encore occupé"
}

# ==============================================================================
# ARRÊT PAR PORT (fallback)
# ==============================================================================

stop_by_port() {
    local display_name=$1
    local port=$2

    if ! lsof -i :"$port" &>/dev/null 2>&1; then
        log_info "$display_name : rien ne tourne sur le port $port"
        return
    fi

    log_info "$display_name : recherche du processus sur le port $port..."

    local pids
    pids=$(lsof -t -i :"$port" 2>/dev/null || true)

    if [ -z "$pids" ]; then
        log_warn "$display_name : aucun processus trouvé sur le port $port"
        return
    fi

    for pid in $pids; do
        log_info "$display_name : arrêt du PID $pid (port $port)"
        if [ "$FORCE" = true ]; then
            kill -9 "$pid" 2>/dev/null || true
        else
            kill -15 "$pid" 2>/dev/null || true
            sleep 3
            if is_pid_alive "$pid"; then
                kill -9 "$pid" 2>/dev/null || true
            fi
        fi
    done

    sleep 2
    if ! lsof -i :"$port" &>/dev/null 2>&1; then
        log_success "$display_name : port $port libéré"
    else
        log_warn "$display_name : port $port encore occupé"
    fi
}

# ==============================================================================
# ARRÊT DE TOUS LES PROCESSUS JAVA (emergency stop)
# ==============================================================================

stop_all_java() {
    log_warn "Arrêt d'urgence : tous les processus Java microservices seront tués"
    log_warn "Cela peut affecter d'autres applications Java en cours d'exécution !"

    local java_pids
    java_pids=$(pgrep -f "spring-boot|microservices" 2>/dev/null || true)

    if [ -z "$java_pids" ]; then
        log_info "Aucun processus Java microservices détecté"
        return
    fi

    for pid in $java_pids; do
        log_info "Arrêt du processus Java PID $pid"
        kill -15 "$pid" 2>/dev/null || true
    done

    sleep 5

    for pid in $java_pids; do
        if is_pid_alive "$pid"; then
            log_warn "Force kill PID $pid"
            kill -9 "$pid" 2>/dev/null || true
        fi
    done

    log_success "Tous les processus Java microservices arrêtés"
}

# ==============================================================================
# ARRÊT DANS L'ORDRE INVERSE
# ==============================================================================

stop_all_services() {
    log_step "Arrêt de tous les services"

    for entry in "${SERVICES[@]}"; do
        IFS=':' read -r service_name display_name port <<< "$entry"
        stop_by_pid_file "$service_name" "$display_name" "$port"
        sleep 2
    done
}

# ==============================================================================
# NETTOYAGE
# ==============================================================================

clean_artifacts() {
    log_step "Nettoyage"

    # Suppression des fichiers PID résiduels
    if [ -d "$PIDS_DIR" ]; then
        log_info "Suppression des fichiers PID..."
        rm -rf "$PIDS_DIR"
        log_success "Fichiers PID supprimés"
    fi

    # Suppression des logs
    if [ -d "$LOGS_DIR" ]; then
        read -r -p "$(echo -e "${YELLOW}Supprimer aussi les logs ? [y/N]${NC} ")" confirm
        if [[ "$confirm" =~ ^[Yy]$ ]]; then
            rm -rf "$LOGS_DIR"
            log_success "Logs supprimés"
        else
            log_info "Logs conservés dans $LOGS_DIR"
        fi
    fi

    log_success "Nettoyage terminé"
}

# ==============================================================================
# STATUT FINAL
# ==============================================================================

print_status() {
    echo ""
    log_step "Statut des ports après arrêt"

    for entry in "${SERVICES[@]}"; do
        IFS=':' read -r service_name display_name port <<< "$entry"
        if lsof -i :"$port" &>/dev/null 2>&1; then
            echo -e "  ${RED}✗${NC} Port $port ($display_name) : encore occupé"
        else
            echo -e "  ${GREEN}✓${NC} Port $port ($display_name) : libéré"
        fi
    done

    echo ""
    echo -e "${BOLD}${GREEN}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║       ✅  Arrêt des microservices terminé !     ║"
    echo "║       Relancez avec : ./start.sh                 ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# ==============================================================================
# PARSING DES ARGUMENTS
# ==============================================================================

parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --force|-f)
                FORCE=true
                log_warn "Mode FORCE activé : arrêt immédiat par SIGKILL"
                shift
                ;;
            --clean|-c)
                CLEAN=true
                log_info "Mode CLEAN activé : nettoyage après arrêt"
                shift
                ;;
            --all)
                log_warn "Option --all : arrêt de TOUS les processus Java microservices"
                stop_all_java
                exit 0
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  (aucune)   Arrêt gracieux via fichiers PID (SIGTERM)"
                echo "  --force    Arrêt immédiat (SIGKILL)"
                echo "  --clean    Arrêt + suppression logs et PIDs"
                echo "  --all      Arrêt de tous les processus Java microservices"
                echo "  --help     Affiche cette aide"
                exit 0
                ;;
            *)
                log_error "Option inconnue: $1"
                echo "Utilisez --help pour voir les options disponibles"
                exit 1
                ;;
        esac
    done
}

# ==============================================================================
# MAIN
# ==============================================================================

main() {
    parse_args "$@"
    print_banner
    stop_all_services

    if [ "$CLEAN" = true ]; then
        clean_artifacts
    fi

    print_status
}

main "$@"