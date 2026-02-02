package com.microservices.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microservices.auth.config.TestSecurityConfig;
import com.microservices.auth.dto.AuthResponse;
import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.LoginResponse;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.entity.User;
import com.microservices.auth.repository.UserRepository;
import com.microservices.auth.service.AuthService;
import com.microservices.common.enums.ErrorCode;
import com.microservices.common.enums.Role;
import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.UnauthorizedException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour AuthController
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
//@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthService authService;

    void createAdminUser() {
        if (!userRepository.existsByEmail("admin@microservices.com")) {
            userRepository.save(
                User.builder()
                    .email("admin@microservices.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build()
            );
        }
    }

    @Test
    void testLoginSuccess() throws Exception {
        createAdminUser();
        // Arrange
        LoginResponse response = LoginResponse.builder()
        .email("admin@microservices.com")
        .role(Role.ADMIN)
        .token("fake-jwt-token")
        .build();

        Mockito.when(authService.login(Mockito.any()))
            .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new LoginRequest("admin@microservices.com", "admin123")
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
            .email("admin@microservices.com")
            .password("wrongpassword")
            .build();

        Mockito.when(authService.login(Mockito.any()))
            .thenThrow(new UnauthorizedException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginValidationError() throws Exception {
        // Arrange - email invalide
        LoginRequest request = LoginRequest.builder()
            .email("invalid-email")
            .password("password123")
            .build();

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
            .email("newuser@test.com")
            .password("Password123")
            .confirmPassword("Password123")
            .role(Role.USER)
            .build();

        AuthResponse mockResponse = AuthResponse.builder()
        .email("newuser@test.com")
        .role(Role.USER)
        .build();

        Mockito.when(authService.register(Mockito.any()))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("newuser@test.com"))
            .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        // Arrange - email déjà existant
        RegisterRequest request = RegisterRequest.builder()
            .email("admin@microservices.com")
            .password("Password123")
            .confirmPassword("Password123")
            .build();

        Mockito.when(authService.register(Mockito.any()))
            .thenThrow(BadRequestException.alreadyExists("Utilisateur", "email", "admin@microservices.com"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWeakPassword() throws Exception {
        // Arrange - mot de passe faible
        RegisterRequest request = RegisterRequest.builder()
            .email("test@test.com")
            .password("weak")
            .confirmPassword("weak")
            .build();

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterPasswordMismatch() throws Exception {
        // Arrange - mots de passe différents
        RegisterRequest request = RegisterRequest.builder()
            .email("test@test.com")
            .password("Password123")
            .confirmPassword("DifferentPassword123")
            .build();

            Mockito.when(authService.register(Mockito.any()))
            .thenThrow(new BadRequestException(ErrorCode.PASSWORD_MISMATCH, "Les mots de passe ne correspondent pas"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testHealthEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/auth/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("Auth Service is running"));
    }
}