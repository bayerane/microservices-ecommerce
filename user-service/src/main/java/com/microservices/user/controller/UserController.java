package com.microservices.user.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.common.dto.ApiResponse;
import com.microservices.common.dto.PageResponse;
import com.microservices.user.dto.PasswordUpdateRequest;
import com.microservices.user.dto.UserCreateRequest;
import com.microservices.user.dto.UserDTO;
import com.microservices.user.dto.UserUpdateRequest;
import com.microservices.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;


/**
 * Contrôleur pour la gestion des utilisateurs
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "API de gestion des utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;

    // Crée un nouvel utilisateur
    @PostMapping
    @Operation(
        summary = "Créer un utilisateur",
        description = "Crée un nouvel utilisateur dans le système"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Utilisateur créé avec succès"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Données invalides"
        )
    })
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
        @Valid @RequestBody UserCreateRequest request
    ) {
        log.info("Requête de création d'utilisateur reçue: {}", request.getEmail());

        UserDTO user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(user, "Utilisateur créé avec succès")
        );
    }

    // Récupère le profil de l'utilisateur courant
    @GetMapping("/profile")
    @Operation(
        summary = "Récupérer son profil",
        description = "Récupère le profil de l'utilisateur connecté"
    )
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUserProfile() {
        log.info("Requête de récupèration du profil utilisateur");

        UserDTO user = userService.getCurrentUserProfile();

        return ResponseEntity.ok(
            ApiResponse.success(user, "Profil récupéré avec succès")
        );
    }

    // Récupère un utilisateur par son ID
    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un utilisateur",
        description = "Récupère un utilisateur par son ID (accès restreint)"
    )
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID id
    ) {
        log.debug("Requête de récupèration d'utilisateur: {}", id);

        UserDTO user = userService.getUserById(id);

        return ResponseEntity.ok(
            ApiResponse.success(user, "Utilisateur récupéré avec succès")
        );
    }

    // Récupère tous les utilisateurs (réservé aux admins)
    @GetMapping
    @Operation(
        summary = "Lister les utilisateurs",
        description = "Récupère tous les utilisateurs avec pagination (ADMIN uniquement)"
    )
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getAllUsers(
        @Parameter(description = "Numéro de page (0-indexed)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de la page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri (ASC/DESC)")
        @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("Requête de récupèration de tous les utilisateurs");

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<UserDTO> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(
            ApiResponse.success(users, "Utilisateurs récupérés avec succès")
        );
    }

    // Recherche des utilisateurs
    @GetMapping("/search")
    @Operation(
        summary = "Rechercher des utilisateurs",
        description = "Recherche des utilisateurs par nom, prénom ou email (ADMIN uniquement)"
    )
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> searchUsers(
        @Parameter(description = "Terme de recherche")
        @RequestParam String query,

        @RequestParam(defaultValue = "0") int page,

        @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Recherche d'utilisateurs avec le terme: {}", query);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "lastName"));

        PageResponse<UserDTO> users = userService.searchUsers(query, pageable);

        return ResponseEntity.ok(
            ApiResponse.success(users, "Recherche effectuée avec succès")
        );
    }

    // Met à jour un utilisateur
    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un utilisateur",
        description = "Met à jour les informations d'un utilisateur"
    )
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID id,

        @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("Requête de mise à jour de l'utilisateur: {}", id);

        UserDTO user = userService.updateUser(id, request);

        return ResponseEntity.ok(
            ApiResponse.success(user, "Utilisateur mis à jour avec succès")
        );
    }

    // Met à jour le mot de passe
    @PutMapping("/{id}/password")
    @Operation(
        summary = "Changer le mot de passe",
        description = "Change le mot de passe de l'utilisateur"
    )
    public ResponseEntity<ApiResponse<Void>> updatePassword(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID id,

        @Valid @RequestBody PasswordUpdateRequest request
    ) {
        log.info("Requête de changement de mot de passe pour l'utilisateur: {}", id);

        userService.updatePassword(id, request);

        return ResponseEntity.ok(
            ApiResponse.success("Mot de passe mis à jour avec succès")
        );
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un utilisateur",
        description = "Supprime un utilisateur (ADMIN uniquement)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID id
    ) {
        log.info("Requête de suppression de l'utilisateur: {}", id);    

        userService.deleteUser(id);

        return ResponseEntity.ok(
            ApiResponse.success("Utilisateur supprimé avec succès")
        );
    }
}
