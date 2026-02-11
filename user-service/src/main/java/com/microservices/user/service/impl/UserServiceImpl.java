package com.microservices.user.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.common.dto.PageResponse;
import com.microservices.common.enums.ErrorCode;
import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.ForbiddenException;
import com.microservices.common.exception.ResourceNotFoundException;
import com.microservices.common.util.ValidationUtil;
import com.microservices.user.client.AuthServiceClient;
import com.microservices.user.dto.PasswordUpdateRequest;
import com.microservices.user.dto.UserCreateRequest;
import com.microservices.user.dto.UserDTO;
import com.microservices.user.dto.UserUpdateRequest;
import com.microservices.user.entity.User;
import com.microservices.user.mapper.UserMapper;
import com.microservices.user.repository.UserRepository;
import com.microservices.user.security.SecurityContextUtil;
import com.microservices.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation du service de gestion des utilisateurs
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityContextUtil securityContext;
    private final AuthServiceClient authServiceClient;

    // Crée un nouvel utilisateur
    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        log.info("Création d'un nouvel utilisateur: {}", request.getEmail());

        // Validation de l'email
        String normalizedEmail = ValidationUtil.normalizeEmail(request.getEmail());
        if (!ValidationUtil.isValidEmail(normalizedEmail)) {
            throw BadRequestException.invalidEmail(normalizedEmail);
        }

        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Tentative de création avec un email déjà existant: {}", normalizedEmail);
            throw BadRequestException.alreadyExists("Utilisateur", "email", normalizedEmail);
        }

        // Validation du mot de passe
        if (!ValidationUtil.isStrongPassword(request.getPassword())) {
            throw BadRequestException.weakPassword();
        }

        // Créer l'utilisateur en base
        User user = userMapper.toEntity(request);
        user.setId(UUID.randomUUID());
        user.setEmail(normalizedEmail);

        User savedUser = userRepository.save(user);

        log.info("Utilisateur créé avec succès: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    // Récupère un utilisateur par son ID
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        log.debug("Récupèration de l'utilisateur avec ID: {}", id);

        // Vérifier les permissions
        if (!securityContext.canAccessResource(id)) {
            log.warn("Accès refusé: utilisateur {} tente d'accéder au profil {}", securityContext.getCurrentUserId(), id);
            throw ForbiddenException.resourceAccess("profil utilisateur");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));
        
        return userMapper.toDTO(user);
    }

    // Récupère le profil de l'utilisateur courant
    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUserProfile() {
        UUID currentUserId = securityContext.getCurrentUserId();
        log.debug("Récupèration du profil pour l'utilisateur courant: {}", currentUserId);

        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> ResourceNotFoundException.user(currentUserId.toString()));
            
        return userMapper.toDTO(user);
    }

    // Récupère tous les utilisateurs (réservé aux admins) 
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Récupèration de tous les utilisateurs (page: {}, size: {})", pageable.getPageNumber(), pageable.getPageSize());

        // Vérifier que l'utilisateur est admin
        if (!securityContext.isCurrentUserAdmin()) {
            log.warn("Tentative d'accès à la liste des utilisateurs par un non-admin: {}", securityContext.getCurrentUserId());
            throw ForbiddenException.adminOnly();
        }

        Page<User> userPage = userRepository.findAll(pageable);

        return PageResponse.of(
            userMapper.toDTOList(userPage.getContent()),
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements()
        );
    }

    // Recherche des utilisateurs (réservé aux admins)
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTO> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Recherche d'utilisateur avec le terme: {}", searchTerm);

        // Vérifier que l'utilisateur est admin
        if (!securityContext.isCurrentUserAdmin()) {
            throw ForbiddenException.adminOnly();
        }

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers(pageable);
        }

        Page<User> userPage =  userRepository.searchUsers(searchTerm.trim(), pageable);

        return PageResponse.of(
            userMapper.toDTOList(userPage.getContent()),
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements()
        );
    }

    // Met à jour un utilisateur
    @Override
    @Transactional
    public UserDTO updateUser(UUID id, UserUpdateRequest request) {
        log.info("Mise à jour de l'utilisateur: {}", id);

        // Vérifier les permissions
        securityContext.requireAccessToResource(id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));

        // Mettre à jour les champs
        userMapper.updateEntityFromRequest(user, request);

        User updateUser = userRepository.save(user);

        log.info("Utilisateur mis à jour avec succès: {}", id);

        return userMapper.toDTO(updateUser);
    }

    // Met à jour le mot de passe
    @Override
    @Transactional
    public void updatePassword(UUID id, PasswordUpdateRequest request) {
        log.info("Mise à jour du mot de passe pour l'utilisateur: {}", id);

        // Vérifier les permissions (seulement son propre mot de passe)
        if (!securityContext.isCurrentUser(id)) {
            log.warn("Tentative de modification du mot de passe d'un autre utilisateur");
            throw ForbiddenException.operationNotAllowed("modification du mot de passe d'un autre utilisateur");
        }

        // Vérifier que les nouveaux l'utilisateur existe
        User user = userRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.user(id.toString()));

        // Vérifier que les nouveaux mots de passe correspondent
        if (!request.passwordsMatch()) {
            throw new BadRequestException(
                ErrorCode.PASSWORD_MISMATCH,
                "Les mots de passe ne correspondent pas"
            );
        }

        // Valider le nouveau mot de passe
        if (!ValidationUtil.isStrongPassword(request.getNewPassword())) {
            throw BadRequestException.weakPassword();
        }

        // Déléguer la mise à jour au Auth Service
        try {
            authServiceClient.updatePassword(id.toString(), request);
            log.info("Mot de passe mis à jour avec succès pour l'utilisateur: {}", id);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du mot de passe: {}", e.getMessage());
            throw new BadRequestException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Erreur lors de la mise à jour du mot de passe"
            );
        }
    }

    // Supprime un utilisateur
    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("Suppression de l'utilisateur: {}", id);

        // Seuls les admins peuvent supprimer des utilisateurs
        if (!securityContext.isCurrentUserAdmin()) {
            log.warn("Tentative de suppression d'utilisateur par un non-admin");
            throw ForbiddenException.adminOnly();
        }

        // Vérifier que l'utilisateur existe
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.user(id.toString());
        }

        // Empêcher la suppression de son propre compte
        if (securityContext.isCurrentUser(id)) {
            log.warn("Tentative de suppression de son propre compte");
            throw new BadRequestException(
                ErrorCode.FORBIDDEN_OPERATION,
                "Vous ne pouvez pas supprimer votre propre compte"
            );
        }

        userRepository.deleteById(id);

        log.info("Utilisateur supprimé avec succès: {}", id);
    }
}
