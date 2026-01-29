# 📚 Guide d'Utilisation de la Common Library

## Installation

### 1. Build de la Common Library

```bash
cd common-lib
mvn clean install
```

Cela installera la bibliothèque dans votre repository Maven local.

### 2. Ajout de la dépendance dans les autres services

Dans le `pom.xml` de chaque service (auth-service, user-service, order-service) :

```xml
<dependency>
    <groupId>com.microservices</groupId>
    <artifactId>common-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Exemples d'Utilisation

### 🎯 Enums

#### Role
```java
import com.microservices.common.enums.Role;

// Utilisation
Role userRole = Role.USER;
Role adminRole = Role.ADMIN;

// Vérifications
if (userRole.isAdmin()) {
    // Logique admin
}

// Conversion depuis String
Role role = Role.fromCode("ADMIN");

// Récupération de la description
String description = role.getDescription(); // "Administrateur système"
```

#### OrderStatus
```java
import com.microservices.common.enums.OrderStatus;

// Utilisation
OrderStatus status = OrderStatus.PENDING;

// Vérifier si annulable
if (status.isCancellable()) {
    // Permettre l'annulation
}

// Vérifier si final
if (!status.isFinalStatus()) {
    // Permettre la modification
}

// Vérifier transition valide
if (currentStatus.canTransitionTo(OrderStatus.CONFIRMED)) {
    // Effectuer la transition
}
```

---

### 📦 DTOs

#### ApiResponse
```java
import com.microservices.common.dto.ApiResponse;

// Réponse de succès avec données
@GetMapping("/users/{id}")
public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String id) {
    UserDTO user = userService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(user, "Utilisateur trouvé"));
}

// Réponse de succès simple
@DeleteMapping("/users/{id}")
public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
    userService.delete(id);
    return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé"));
}

// Réponse d'erreur
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(ApiResponse.error(ex.getMessage()));
}
```

#### ErrorResponse
```java
import com.microservices.common.dto.ErrorResponse;
import com.microservices.common.enums.ErrorCode;

// Créer une ErrorResponse
ErrorResponse error = ErrorResponse.of(404, ErrorCode.USER_NOT_FOUND);
error.setPath(request.getRequestURI());

// Avec détails
ErrorResponse error = ErrorResponse.of(400, ErrorCode.VALIDATION_ERROR, "Email invalide");
```

#### PageResponse
```java
import com.microservices.common.dto.PageResponse;

// Créer une réponse paginée
@GetMapping("/users")
public ResponseEntity<PageResponse<UserDTO>> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    Page<User> userPage = userService.findAll(PageRequest.of(page, size));
    List<UserDTO> users = userMapper.toDTO(userPage.getContent());
    
    PageResponse<UserDTO> response = PageResponse.of(
        users,
        page,
        size,
        userPage.getTotalElements()
    );
    
    return ResponseEntity.ok(response);
}
```

---

### ⚠️ Exceptions

#### Utilisation dans les Services
```java
import com.microservices.common.exception.*;

public class UserServiceImpl implements UserService {
    
    @Override
    public UserDTO findById(String id) {
        return userRepository.findById(id)
            .map(userMapper::toDTO)
            .orElseThrow(() -> ResourceNotFoundException.user(id));
    }
    
    @Override
    public UserDTO create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BadRequestException.alreadyExists("Utilisateur", "email", request.getEmail());
        }
        
        if (!ValidationUtil.isStrongPassword(request.getPassword())) {
            throw BadRequestException.weakPassword();
        }
        
        // Logique de création...
    }
    
    @Override
    public void delete(String id, String currentUserId, Role currentUserRole) {
        if (!currentUserRole.isAdmin() && !id.equals(currentUserId)) {
            throw ForbiddenException.insufficientPermissions();
        }
        
        // Logique de suppression...
    }
}
```

#### GlobalExceptionHandler (exemple)
```java
import com.microservices.common.exception.*;
import com.microservices.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex,
        WebRequest request
    ) {
        ErrorResponse error = ErrorResponse.of(404, ex.getErrorCode(), ex.getDetails());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        return ResponseEntity.status(404).body(error);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException ex,
        WebRequest request
    ) {
        ErrorResponse error = ErrorResponse.of(401, ex.getErrorCode(), ex.getDetails());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        return ResponseEntity.status(401).body(error);
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
        ForbiddenException ex,
        WebRequest request
    ) {
        ErrorResponse error = ErrorResponse.of(403, ex.getErrorCode(), ex.getDetails());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        return ResponseEntity.status(403).body(error);
    }
}
```

---

### 🛠️ Utilitaires

#### ValidationUtil
```java
import com.microservices.common.util.ValidationUtil;

// Validation email
if (!ValidationUtil.isValidEmail(email)) {
    throw BadRequestException.invalidEmail(email);
}

// Validation mot de passe
if (!ValidationUtil.isStrongPassword(password)) {
    throw new BadRequestException(ValidationUtil.getPasswordRequirements());
}

// Validation téléphone
if (!ValidationUtil.isValidPhone(phone)) {
    throw new BadRequestException("Numéro de téléphone invalide");
}

// Normalisation email
String cleanEmail = ValidationUtil.normalizeEmail(email);
```

#### DateUtil
```java
import com.microservices.common.util.DateUtil;

// Date actuelle
LocalDateTime now = DateUtil.now();

// Formatage
String isoDate = DateUtil.formatIso(now);
String frenchDate = DateUtil.formatFrench(now);

// Parsing
LocalDate date = DateUtil.parseIsoDate("2024-01-23");

// Calculs
long days = DateUtil.daysBetween(startDate, endDate);
LocalDateTime tomorrow = DateUtil.addDays(now, 1);

// Vérifications
if (DateUtil.isPast(orderDate)) {
    // Date passée
}
```

#### StringUtil
```java
import com.microservices.common.util.StringUtil;

// Génération aléatoire
String randomCode = StringUtil.generateRandomAlphanumeric(10);
String orderNumber = "ORD-" + StringUtil.generateRandomNumeric(8);

// Capitalisation
String name = StringUtil.capitalizeWords("jean dupont"); // "Jean Dupont"

// Slug
String slug = StringUtil.toSlug("Mon Article Été 2024"); // "mon-article-ete-2024"

// Masquage
String maskedEmail = StringUtil.maskEmail("john.doe@example.com"); // "j***e@example.com"

// Troncature
String preview = StringUtil.truncate(longDescription, 100); // + "..."
```

---

### 🔐 Constantes

#### AppConstants
```java
import static com.microservices.common.constant.AppConstants.*;

// Pagination
Pageable pageable = PageRequest.of(
    DEFAULT_PAGE_NUMBER,
    DEFAULT_PAGE_SIZE,
    Sort.by(Sort.Direction.valueOf(DEFAULT_SORT_DIRECTION), DEFAULT_SORT_BY)
);

// Validation
if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
    throw new BadRequestException("Nom invalide");
}

// Messages
return ApiResponse.success(data, SUCCESS_MESSAGE);
```

#### SecurityConstants
```java
import static com.microservices.common.constant.SecurityConstants.*;

// Extraction du token
String authHeader = request.getHeader(AUTHORIZATION_HEADER);
if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
    String token = authHeader.substring(BEARER_PREFIX.length());
}

// Récupération headers utilisateur
String userId = request.getHeader(USER_ID_HEADER);
String userRole = request.getHeader(USER_ROLE_HEADER);

// Vérification rôle
if (!ROLE_ADMIN.equals(userRole)) {
    throw ForbiddenException.adminOnly();
}
```

---

## ✅ Bonnes Pratiques

### 1. Toujours utiliser les exceptions métier
```java
// ❌ Mauvais
throw new RuntimeException("Utilisateur non trouvé");

// ✅ Bon
throw ResourceNotFoundException.user(userId);
```

### 2. Utiliser ApiResponse pour la cohérence
```java
// ❌ Mauvais
return ResponseEntity.ok(userDTO);

// ✅ Bon
return ResponseEntity.ok(ApiResponse.success(userDTO));
```

### 3. Valider avec ValidationUtil
```java
// ✅ Validation centralisée
if (!ValidationUtil.isValidEmail(email)) {
    throw BadRequestException.invalidEmail(email);
}
```

### 4. Utiliser les constantes
```java
// ❌ Mauvais
if (role.equals("ADMIN")) { }

// ✅ Bon
if (SecurityConstants.ROLE_ADMIN.equals(role)) { }
```

### 5. Gérer les transitions de statut
```java
// ✅ Utiliser les méthodes de l'enum
if (!currentStatus.canTransitionTo(newStatus)) {
    throw new BadRequestException(
        ErrorCode.ORDER_STATUS_TRANSITION_INVALID,
        String.format("Impossible de passer de %s à %s", currentStatus, newStatus)
    );
}
```

---

## 🧪 Tests Unitaires (Exemples)

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    
    @Test
    void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
    }
    
    @Test
    void testStrongPassword() {
        assertTrue(ValidationUtil.isStrongPassword("Password123"));
        assertFalse(ValidationUtil.isStrongPassword("weak"));
    }
}
```

---

## 📌 Résumé

La Common Library fournit :
- ✅ **Enums standardisés** (Role, OrderStatus, ErrorCode)
- ✅ **DTOs réutilisables** (ApiResponse, ErrorResponse, PageResponse)
- ✅ **Exceptions métier** hiérarchisées
- ✅ **Utilitaires** de validation, date, string
- ✅ **Constantes** centralisées

**Avantage** : Code DRY (Don't Repeat Yourself), cohérence entre services, maintenabilité améliorée.