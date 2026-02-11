package com.microservices.user.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.microservices.common.dto.PageResponse;
import com.microservices.user.dto.PasswordUpdateRequest;
import com.microservices.user.dto.UserCreateRequest;
import com.microservices.user.dto.UserDTO;
import com.microservices.user.dto.UserUpdateRequest;

/**
 * Interface du service de gestion des utilisateurs
 * 
 * @author Baye Rane
 * @version 1.0
 */
public interface UserService {
    
    // Crée un nouvel utilisateur
    UserDTO createUser(UserCreateRequest request);

    // Récupère un utilisateur par son ID
    UserDTO getUserById(UUID id);

    // Récupère le profil de l'utilisateur courant
    UserDTO getCurrentUserProfile();

    // Récupère tous les utilisateurs (paginé)
    PageResponse<UserDTO> getAllUsers(Pageable pageable);

    // Recherche des utilisateurs
    PageResponse<UserDTO> searchUsers(String searchTerm, Pageable pageable);

    // Met à jour un utilisateur
    UserDTO updateUser(UUID id, UserUpdateRequest request);

    // Met à jour le mot de passe
    void updatePassword(UUID id, PasswordUpdateRequest request);

    // Supprimer un utilisateur
    void deleteUser(UUID id);
}
