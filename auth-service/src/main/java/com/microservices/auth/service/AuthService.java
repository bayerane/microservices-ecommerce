package com.microservices.auth.service;

import com.microservices.auth.dto.AuthResponse;
import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.LoginResponse;
import com.microservices.auth.dto.RegisterRequest;

/**
 * Interface du service d'authentification
 * 
 * @author Baye Rane
 * @version 1.0
 */
public interface AuthService {

    // Authentifie un utilisateur
    LoginResponse login(LoginRequest request);

    // Enregistre un nouvel utilisateur
    AuthResponse register(RegisterRequest request);

    // Valide un token JWT
    Boolean validateToken(String token);
}
