#!/bin/bash
RED='\033[0;31m'
NC='\033[0m'

if [ -f "user-service.pid" ]; then
    PID=$(cat user-service.pid)
    echo -e "${RED}[INFO] Arrêt du User Service (PID: $PID)...${NC}"
    kill $PID
    rm user-service.pid
    echo -e "${RED}[SUCCESS] Service arrêté.${NC}"
else
    echo -e "${RED}[ERROR] Aucun fichier PID trouvé.${NC}"
fi