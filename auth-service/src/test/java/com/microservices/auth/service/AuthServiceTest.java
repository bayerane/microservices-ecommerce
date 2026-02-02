package com.microservices.auth.service;

import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.LoginResponse;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.dto.AuthResponse;
import com.microservices.auth.entity.User;
import com.microservices.auth.repository.UserRepository;
import com.microservices.auth.security.JwtUtil;
import com.microservices.auth.service.impl.AuthServiceImpl;
import com.microservices.common.enums.Role;
import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AuthService
 * 
 * @author Baye Rane
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("test@test.com")
            .password("encodedPassword")
            .role(Role.USER)
            .enabled(true)
            .build();

        loginRequest = LoginRequest.builder()
            .email("test@test.com")
            .password("password123")
            .build();

        registerRequest = RegisterRequest.builder()
            .email("newuser@test.com")
            .password("Password123")
            .confirmPassword("Password123")
            .role(Role.USER)
            .build();
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail()))
            .thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole(), response.getRole());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void testLoginInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void testLoginDisabledUser() {
        // Arrange
        testUser.setEnabled(false);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail()))
            .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole(), response.getRole());
        verify(userRepository).existsByEmail(anyString());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterPasswordMismatch() {
        // Arrange
        registerRequest.setConfirmPassword("DifferentPassword");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testValidateTokenValid() {
        // Arrange
        String token = "valid-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // Act
        Boolean result = authService.validateToken(token);

        // Assert
        assertTrue(result);
        verify(jwtUtil).validateToken(token);
    }

    @Test
    void testValidateTokenInvalid() {
        // Arrange
        String token = "invalid-token";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        Boolean result = authService.validateToken(token);

        // Assert
        assertFalse(result);
        verify(jwtUtil).validateToken(token);
    }
}