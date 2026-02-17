#!/bin/bash
# ==============================================================================
# start.sh - Script de démarrage de l'architecture microservices
#
# Ordre de démarrage :
#   1. Vérifications préliminaires (Java, Maven, PostgreSQL)
#   2. Discovery Service  (port 8761)
#   3. Auth Service       (port 8081)
#   4. API Gateway        (port 8080)
#   5. User Service       (port 8082)
#   6. Order Service      (port 8083)
#
# Usage :
#   chmod +x start.sh
#   ./start.sh           → démarrage standard (Maven)
#   ./start.sh --jar     → démarrage via JARs précompilés
#   ./start.sh --build   → build + démarrage
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

# Ports
PORT_DISCOVERY=8761
PORT_AUTH=8081
PORT_GATEWAY=8080
PORT_USER=8082
PORT_ORDER=8083

# Délais d'attente (secondes)
WAIT_DISCOVERY=40
WAIT_AUTH=25
WAIT_GATEWAY=20
WAIT_USER=20
WAIT_ORDER=15

# Timeout de démarrage d'un service (secondes)
STARTUP_TIMEOUT=120

# Mode de démarrage (maven par défaut)
MODE="maven"
BUILD=false

# ==============================================================================
# COULEURS
# ==============================================================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# ==============================================================================
# FONCTIONS UTILITAIRES
# ==============================================================================

log_info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
log_success() { echo -e "${GREEN}[OK]${NC}    $*"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }
log_step()    { echo -e "\n${BOLD}${CYAN}━━━ $* ━━━${NC}"; }

# Affiche le titre du script
print_banner() {
    echo -e "${BOLD}${CYAN}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║       🚀  Microservices - Démarrage             ║"
    echo "║       Auth • User • Order • Gateway • Eureka    ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Crée les répertoires nécessaires
init_dirs() {
    mkdir -p "$LOGS_DIR" "$PIDS_DIR"
}

# Vérifie si un port est libre
is_port_free() {
    local port=$1
    ! lsof -i :"$port" &>/dev/null 2>&1
}

# Attend que le port soit actif (health check HTTP)
wait_for_port() {
    local service=$1
    local port=$2
    local timeout=$3
    local elapsed=0

    log_info "Attente du démarrage de $service sur le port $port..."
    while ! curl -sf "http://localhost:$port/actuator/health" &>/dev/null; do
        if [ "$elapsed" -ge "$timeout" ]; then
            log_error "$service n'a pas démarré dans le délai imparti ($timeout s)"
            return 1
        fi
        printf "."
        sleep 2
        elapsed=$((elapsed + 2))
    done
    echo ""
    log_success "$service est opérationnel sur le port $port"
}

# Enregistre le PID d'un service
save_pid() {
    local service=$1
    local pid=$2
    echo "$pid" > "$PIDS_DIR/$service.pid"
    log_info "PID $pid enregistré pour $service"
}

# Vérifie si un service est déjà en cours d'exécution
is_running() {
    local service=$1
    local pid_file="$PIDS_DIR/$service.pid"
    if [ -f "$pid_file" ]; then
        local pid
        pid=$(cat "$pid_file")
        kill -0 "$pid" 2>/dev/null && return 0
    fi
    return 1
}

# ==============================================================================
# VÉRIFICATIONS PRÉLIMINAIRES
# ==============================================================================

check_prerequisites() {
    log_step "Vérifications préliminaires"
    local errors=0

    # Java
    if command -v java &>/dev/null; then
        local java_version
        java_version=$(java -version 2>&1 | head -n1 | awk -F '"' '{print $2}')
        log_success "Java $java_version détecté"
    else
        log_error "Java n'est pas installé. Installez Java 17+"
        errors=$((errors + 1))
    fi

    # Maven
    if command -v mvn &>/dev/null; then
        local mvn_version
        mvn_version=$(mvn -version 2>&1 | head -n1 | awk '{print $3}')
        log_success "Maven $mvn_version détecté"
    else
        log_error "Maven n'est pas installé. Installez Maven 3.8+"
        errors=$((errors + 1))
    fi

    # PostgreSQL
    if command -v pg_isready &>/dev/null; then
        if pg_isready -h localhost -p 5432 -q; then
            log_success "PostgreSQL est actif sur le port 5432"
        else
            log_warn "PostgreSQL ne répond pas sur le port 5432"
            log_warn "Les services utilisant H2 démarreront quand même"
        fi
    else
        log_warn "pg_isready non trouvé — vérification PostgreSQL ignorée"
    fi

    # Vérification des ports
    for port in $PORT_DISCOVERY $PORT_AUTH $PORT_GATEWAY $PORT_USER $PORT_ORDER; do
        if ! is_port_free "$port"; then
            log_error "Le port $port est déjà utilisé"
            log_error "Exécutez : lsof -i :$port"
            errors=$((errors + 1))
        fi
    done

    if [ "$errors" -gt 0 ]; then
        log_error "$errors erreur(s) détectée(s). Corrigez-les avant de continuer."
        exit 1
    fi

    log_success "Toutes les vérifications sont passées"
}

# ==============================================================================
# BUILD
# ==============================================================================

build_project() {
    log_step "Build du projet (mvn clean install)"
    log_info "Cette opération peut prendre quelques minutes..."

    cd "$BASE_DIR"
    if mvn clean install -DskipTests -q; then
        log_success "Build réussi"
    else
        log_error "Échec du build Maven"
        exit 1
    fi
}

# ==============================================================================
# DÉMARRAGE D'UN SERVICE
# ==============================================================================

start_service() {
    local service_name=$1     # Ex: discovery-service
    local display_name=$2     # Ex: Discovery Service
    local port=$3
    local wait_time=$4

    log_step "Démarrage de $display_name (port $port)"

    if is_running "$service_name"; then
        log_warn "$display_name semble déjà en cours d'exécution. Ignoré."
        return 0
    fi

    local log_file="$LOGS_DIR/${service_name}.log"
    cd "$BASE_DIR/$service_name"

    if [ "$MODE" = "jar" ]; then
        local jar_file
        jar_file=$(ls target/*.jar 2>/dev/null | head -n1)
        if [ -z "$jar_file" ]; then
            log_error "Aucun JAR trouvé dans $service_name/target/. Lancez avec --build"
            exit 1
        fi
        log_info "Démarrage via JAR : $jar_file"
        java -jar "$jar_file" > "$log_file" 2>&1 &
    else
        log_info "Démarrage via Maven Spring Boot"
        mvn spring-boot:run > "$log_file" 2>&1 &
    fi

    local pid=$!
    save_pid "$service_name" "$pid"
    log_info "Processus démarré (PID: $pid) — logs : $log_file"

    # Attendre que le service soit prêt
    if wait_for_port "$display_name" "$port" "$STARTUP_TIMEOUT"; then
        log_success "$display_name est prêt ✓"
    else
        log_error "$display_name n'a pas démarré. Consultez : $log_file"
        exit 1
    fi

    log_info "Pause de ${wait_time}s avant le service suivant..."
    sleep "$wait_time"
}

# ==============================================================================
# DÉMARRAGE DANS L'ORDRE
# ==============================================================================

start_all_services() {
    log_step "Démarrage de tous les services"

    start_service \
        "discovery-service" \
        "Discovery Service (Eureka)" \
        "$PORT_DISCOVERY" \
        "$WAIT_DISCOVERY"

    start_service \
        "auth-service" \
        "Auth Service" \
        "$PORT_AUTH" \
        "$WAIT_AUTH"

    start_service \
        "api-gateway" \
        "API Gateway" \
        "$PORT_GATEWAY" \
        "$WAIT_GATEWAY"

    start_service \
        "user-service" \
        "User Service" \
        "$PORT_USER" \
        "$WAIT_USER"

    start_service \
        "order-service" \
        "Order Service" \
        "$PORT_ORDER" \
        "$WAIT_ORDER"
}

# ==============================================================================
# RÉSUMÉ FINAL
# ==============================================================================

print_summary() {
    echo ""
    echo -e "${BOLD}${GREEN}"
    echo "╔══════════════════════════════════════════════════════════╗"
    echo "║           ✅  Tous les services sont démarrés !         ║"
    echo "╠══════════════════════════════════════════════════════════╣"
    echo "║  Service           │ Port  │ URL                        ║"
    echo "╠══════════════════════════════════════════════════════════╣"
    printf "║  %-18s│ %-5s │ %-26s  ║\n" \
        "Eureka Dashboard"  "$PORT_DISCOVERY" "http://localhost:$PORT_DISCOVERY"
    printf "║  %-18s│ %-5s │ %-26s  ║\n" \
        "API Gateway"       "$PORT_GATEWAY"   "http://localhost:$PORT_GATEWAY"
    printf "║  %-18s│ %-5s │ %-26s  ║\n" \
        "Auth Service"      "$PORT_AUTH"      "http://localhost:$PORT_AUTH"
    printf "║  %-18s│ %-5s │ %-26s  ║\n" \
        "User Service"      "$PORT_USER"      "http://localhost:$PORT_USER"
    printf "║  %-18s│ %-5s │ %-26s  ║\n" \
        "Order Service"     "$PORT_ORDER"     "http://localhost:$PORT_ORDER"
    echo "╠══════════════════════════════════════════════════════════╣"
    echo "║  📚  Swagger UI                                          ║"
    printf "║    Auth  → http://localhost:%-5s/swagger-ui.html      ║\n" "$PORT_AUTH"
    printf "║    User  → http://localhost:%-5s/swagger-ui.html      ║\n" "$PORT_USER"
    printf "║    Order → http://localhost:%-5s/swagger-ui.html      ║\n" "$PORT_ORDER"
    echo "╠══════════════════════════════════════════════════════════╣"
    echo "║  📋  Logs : ./logs/<service>.log                         ║"
    echo "║  🛑  Arrêt : ./stop.sh                                   ║"
    echo "╚══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# ==============================================================================
# PARSING DES ARGUMENTS
# ==============================================================================

parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --jar)
                MODE="jar"
                log_info "Mode : démarrage via JARs précompilés"
                shift
                ;;
            --build)
                BUILD=true
                log_info "Mode : build + démarrage"
                shift
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  (aucune)   Démarrage standard via Maven spring-boot:run"
                echo "  --jar      Démarrage via les JARs dans target/"
                echo "  --build    Build Maven puis démarrage"
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
    init_dirs
    check_prerequisites

    if [ "$BUILD" = true ]; then
        build_project
    fi

    start_all_services
    print_summary
}

main "$@"