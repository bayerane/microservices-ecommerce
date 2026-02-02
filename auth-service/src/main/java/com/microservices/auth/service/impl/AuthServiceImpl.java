package com.microservices.auth.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.auth.dto.AuthResponse;
import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.LoginResponse;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.entity.User;
import com.microservices.auth.repository.UserRepository;
import com.microservices.auth.security.JwtUtil;
import com.microservices.auth.service.AuthService;
import com.microservices.common.enums.ErrorCode;
import com.microservices.common.enums.Role;
import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.UnauthorizedException;
import com.microservices.common.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation du service d'authentification
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // Authentifie un utilisateur et génère un token JWT
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour l'utilisateur: {}", request.getEmail());

        try {
            // Authentification via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            log.debug("Authentification réussie pour: {}", request.getEmail());

            // Récupèrqtion de lùutilisateur
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> UnauthorizedException.invalidCredentials());

            // Vérification si l'utilisateur est activé
            if (!user.isEnabled()) {
                log.warn("Tentative de connexion d'un utilisateur désactivé: {}", request.getEmail());
                throw new UnauthorizedException(ErrorCode.USER_DISABLED, "Votre compte est désactivé");
            }
            
            // Générer un token JWT
            String token = jwtUtil.generateToken(user);

            log.info("Connexion réussie pour l'utilisateur: {} (ID: {})", user.getEmail(), user.getId());
            
            return LoginResponse.of(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtUtil.getExpiration()
            );
        } catch (BadCredentialsException e) {
            log.warn("Échec de connexion - Crédentials invalides pour: {}", request.getEmail());
            throw UnauthorizedException.invalidCredentials();
        } catch (DisabledException e) {
            log.warn("Échec de connexion - Compte desactivé pour: {}", request.getEmail());
            throw new UnauthorizedException(ErrorCode.USER_DISABLED, "Votre compte est désactivé");
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification: {}", e.getMessage(), e);
            throw new UnauthorizedException(ErrorCode.AUTHENTICATION_FAILED, "Erreur lors de l'authentification");
        }
    }

    // Enregistre un nouvel utilisateur
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'enregistrement pour l'email: {}", request.getEmail());

        // Validation de l'email
        String normalizedEmail = ValidationUtil.normalizeEmail(request.getEmail());
        if (!ValidationUtil.isValidEmail(normalizedEmail)) {
            throw BadRequestException.invalidEmail(normalizedEmail);
        }

        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Tentative d'enregistrement avec un email déjà existant: {}", normalizedEmail);
            throw BadRequestException.alreadyExists("Utilisateur", "email", normalizedEmail);
        }

        // Validation du mot de passe
        if (!ValidationUtil.isStrongPassword(request.getPassword())) {
            throw BadRequestException.weakPassword();
        }

        // Vérification de la correspondance des mots de passe
        if (!request.passwordsMatch()) {
            throw new BadRequestException(
                ErrorCode.PASSWORD_MISMATCH,
                "Les mots de passe ne correspondent pas"
            );
        }

        // Création de l'utilisateur
        User user = User.builder()
            .email(normalizedEmail)
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole() != null ? request.getRole() : Role.USER)
            .enabled(true)
            .build();

        User savedUser = userRepository.save(user);

        log.info("Utilisateur enregistré avec succès: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

        return AuthResponse.registered(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

    // Valide un token JWT
    @Override
    @Transactional(readOnly = true)
    public Boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage());
            return false;
        }
    }
}
