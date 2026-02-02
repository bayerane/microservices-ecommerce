package com.microservices.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.auth.dto.AuthResponse;
import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.LoginResponse;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.service.AuthService;
import com.microservices.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour les endpoints d'authentification
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "API d'authentification et d'enregistrement")
public class AuthController {
    
    private final AuthService authService;

    // Endpoint de connexion
    @PostMapping("/login")
    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne un token JWT"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Connexion Réussie",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Identifiants invalides"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Requête invalide"
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requête de connexion reçue pour l'utilisateur: {}", request.getEmail());

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success(response, "Connexion réussie")
        );
    }

    // Endpoint d'enregistrement
    @PostMapping("/register")
    @Operation(
        summary = "Enregistrement utilisateur",
        description = "Crée un nouveau compte utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Enregistrement créé avec succès",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Données invalides ou email déja existant"
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Requête d'enregistrement reçue pour l'utilisateur: {}", request.getEmail());

        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(response, "Utilisateur enregistré avec succès.")
        );
    }

    // Endpoint de validation de token
    @GetMapping("/validate")
    @Operation(
        summary = "Validation de token",
        description = "Vérifie si le token JWT est valide"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Validation effectuée"
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        log.debug("Validation de token demandée");

        Boolean isValid = authService.validateToken(token);

        return ResponseEntity.ok(
            ApiResponse.success(isValid, isValid ? "Token valide" : "Token invalide")
        );
    }

    // Endpoint de santé
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Vérifie que le service d'authentification fonctionne"
    )
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
            ApiResponse.success("Auth Service is running", "Service opérationnel")
        );
    }
}
