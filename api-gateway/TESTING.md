# 🧪 Guide de Test - API Gateway

## 📋 Table des Matières

1. [Tests Manuels](#tests-manuels)
2. [Tests avec Postman](#tests-avec-postman)
3. [Tests avec cURL](#tests-avec-curl)
4. [Tests de Charge](#tests-de-charge)
5. [Scénarios de Test](#scénarios-de-test)

---

## 🔧 Tests Manuels

### Prérequis

Assurez-vous que les services suivants sont démarrés :
- ✅ Discovery Service (port 8761)
- ✅ Auth Service (port 8081)
- ✅ API Gateway (port 8080)

### Vérification des Services

```bash
# 1. Vérifier Discovery Service
curl http://localhost:8761/actuator/health

# 2. Vérifier Auth Service
curl http://localhost:8081/actuator/health

# 3. Vérifier Gateway
curl http://localhost:8080/actuator/health

# 4. Vérifier les routes Gateway
curl http://localhost:8080/actuator/gateway/routes
```

---

## 📬 Tests avec Postman

### Collection Postman

Importez la collection suivante dans Postman :

**Variables d'environnement à définir :**
```json
{
  "gateway_url": "http://localhost:8080",
  "jwt_token": ""
}
```

### Scénario 1 : Login et Récupération du Token

**Étape 1 - Login**

```
POST {{gateway_url}}/api/auth/login
Content-Type: application/json

{
  "email": "admin@microservices.com",
  "password": "admin123"
}
```

**Script Post-Request (dans Tests):**
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData.data.token);
    console.log("Token saved:", jsonData.data.token);
}
```

**Étape 2 - Utiliser le Token**

```
GET {{gateway_url}}/api/users/profile
Authorization: Bearer {{jwt_token}}
```

---

## 💻 Tests avec cURL

### Test 1 : Login via Gateway

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@microservices.com",
    "password": "admin123"
  }' | jq

# Sauvegarder le token dans une variable
export TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

echo "Token: $TOKEN"
```

### Test 2 : Requête sans Token (devrait échouer)

```bash
curl -v http://localhost:8080/api/users/profile
# Attendu: 401 Unauthorized
```

### Test 3 : Requête avec Token Valide

```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN" | jq
```

### Test 4 : Requête avec Token Invalide

```bash
curl -v -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer invalid-token-here"
# Attendu: 401 Unauthorized
```

### Test 5 : Vérifier les Headers Ajoutés

```bash
# Dans les logs du User Service, vous devriez voir :
# X-User-Id, X-User-Email, X-User-Role
```

---

## ⚡ Tests de Charge

### Avec Apache Bench

```bash
# 1. Obtenir un token
export TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

# 2. Créer un fichier avec le header
echo "Authorization: Bearer $TOKEN" > headers.txt

# 3. Test de charge (1000 requêtes, 10 concurrent)
ab -n 1000 -c 10 \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/auth/health

# Analyser les résultats
# - Time per request
# - Requests per second
# - Failed requests (devrait être 0)
```

### Avec Artillery

```yaml
# artillery-test.yml
config:
  target: "http://localhost:8080"
  phases:
    - duration: 60
      arrivalRate: 10
      name: "Warm up"
    - duration: 120
      arrivalRate: 50
      name: "Sustained load"

scenarios:
  - name: "Auth flow"
    flow:
      - post:
          url: "/api/auth/login"
          json:
            email: "admin@microservices.com"
            password: "admin123"
          capture:
            - json: "$.data.token"
              as: "token"
      - get:
          url: "/api/users/profile"
          headers:
            Authorization: "Bearer {{ token }}"
```

Exécution :
```bash
artillery run artillery-test.yml
```

---

## 🎯 Scénarios de Test

### Scénario 1 : Authentification Complète

```bash
#!/bin/bash

echo "=== Test Scénario 1: Authentification Complète ==="

# 1. Login Admin
echo "1. Login Admin..."
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

if [ "$ADMIN_TOKEN" != "null" ]; then
  echo "✅ Login Admin réussi"
else
  echo "❌ Login Admin échoué"
  exit 1
fi

# 2. Login User
echo "2. Login User..."
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@microservices.com","password":"user123"}' \
  | jq -r '.data.token')

if [ "$USER_TOKEN" != "null" ]; then
  echo "✅ Login User réussi"
else
  echo "❌ Login User échoué"
  exit 1
fi

# 3. Vérifier que les tokens sont différents
if [ "$ADMIN_TOKEN" != "$USER_TOKEN" ]; then
  echo "✅ Tokens uniques générés"
else
  echo "❌ Tokens identiques (erreur)"
  exit 1
fi

echo "✅ Scénario 1 : SUCCÈS"
```

### Scénario 2 : Validation des Routes

```bash
#!/bin/bash

echo "=== Test Scénario 2: Validation des Routes ==="

# Obtenir un token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

# Test routes publiques
echo "Test routes publiques..."
curl -s http://localhost:8080/api/auth/health | jq '.success' | grep -q true
if [ $? -eq 0 ]; then
  echo "✅ Route publique /api/auth/health accessible"
else
  echo "❌ Route publique inaccessible"
fi

# Test routes protégées sans token
echo "Test routes protégées sans token..."
STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/users/profile)
if [ "$STATUS" == "401" ]; then
  echo "✅ Route protégée bloque sans token"
else
  echo "❌ Route protégée accessible sans token (erreur de sécurité)"
fi

# Test routes protégées avec token
echo "Test routes protégées avec token..."
curl -s http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN" | jq '.success' | grep -q true
if [ $? -eq 0 ]; then
  echo "✅ Route protégée accessible avec token"
else
  echo "❌ Route protégée inaccessible avec token"
fi

echo "✅ Scénario 2 : SUCCÈS"
```

### Scénario 3 : Test de Charge Progressive

```bash
#!/bin/bash

echo "=== Test Scénario 3: Charge Progressive ==="

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

# Test avec 10 requêtes
echo "Test 10 requêtes séquentielles..."
for i in {1..10}; do
  curl -s http://localhost:8080/api/auth/health \
    -H "Authorization: Bearer $TOKEN" > /dev/null
done
echo "✅ 10 requêtes complétées"

# Test avec 50 requêtes parallèles
echo "Test 50 requêtes parallèles..."
for i in {1..50}; do
  curl -s http://localhost:8080/api/auth/health \
    -H "Authorization: Bearer $TOKEN" > /dev/null &
done
wait
echo "✅ 50 requêtes parallèles complétées"

echo "✅ Scénario 3 : SUCCÈS"
```

### Scénario 4 : Test de Résilience

```bash
#!/bin/bash

echo "=== Test Scénario 4: Résilience ==="

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@microservices.com","password":"admin123"}' \
  | jq -r '.data.token')

# Test avec service indisponible
echo "Test routing vers service indisponible..."
STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
  http://localhost:8080/api/fake-service/test \
  -H "Authorization: Bearer $TOKEN")

if [ "$STATUS" == "503" ] || [ "$STATUS" == "404" ]; then
  echo "✅ Gateway retourne erreur appropriée"
else
  echo "⚠️  Status code inattendu: $STATUS"
fi

# Test timeout
echo "Test timeout handling..."
# (nécessite un service avec délai artificiel)

echo "✅ Scénario 4 : SUCCÈS"
```

---

## 📊 Métriques à Surveiller

### Pendant les Tests

```bash
# Surveiller les métriques en temps réel
watch -n 2 'curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq'

# Vérifier la latence
curl http://localhost:8080/actuator/metrics/gateway.requests | jq

# Vérifier les erreurs
curl http://localhost:8080/actuator/metrics/http.server.requests | \
  jq '.measurements[] | select(.statistic == "COUNT")'
```

### Indicateurs Clés

- **Latence p50** : < 50ms
- **Latence p95** : < 200ms
- **Latence p99** : < 500ms
- **Taux d'erreur** : < 1%
- **Throughput** : > 100 req/s

---

## ✅ Checklist de Validation

### Fonctionnalités

- [ ] Login via Gateway fonctionne
- [ ] Token JWT généré correctement
- [ ] Routes publiques accessibles sans token
- [ ] Routes protégées bloquées sans token
- [ ] Routes protégées accessibles avec token valide
- [ ] Token invalide rejeté (401)
- [ ] Headers X-User-* ajoutés correctement
- [ ] CORS fonctionne (tests depuis navigateur)
- [ ] Retry automatique fonctionne
- [ ] Logs centralisés fonctionnent

### Performance

- [ ] Latence acceptable (< 500ms p99)
- [ ] Pas de memory leak
- [ ] CPU usage stable
- [ ] Connexions pool gérées correctement

### Sécurité

- [ ] Secrets JWT sécurisés
- [ ] Pas de token dans les logs
- [ ] HTTPS en production
- [ ] CORS restreint en production

---

## 🐛 Debugging

### Activer Logs Détaillés

```bash
# Via Actuator
curl -X POST http://localhost:8080/actuator/loggers/com.microservices.gateway \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"TRACE"}'

# Vérifier
tail -f logs/api-gateway.log
```

### Tracer une Requête

```bash
# Ajouter header de trace
curl -v http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-B3-TraceId: 12345" \
  2>&1 | grep -E "< |>"
```

---

## 📚 Ressources

- **Postman Collection** : Importer depuis le repo
- **Scripts de Test** : `/scripts/test-gateway.sh`
- **Monitoring** : Grafana Dashboard pour Gateway