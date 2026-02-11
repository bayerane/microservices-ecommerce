package com.microservices.user.service;

import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.ForbiddenException;
import com.microservices.common.exception.ResourceNotFoundException;
import com.microservices.user.dto.UserCreateRequest;
import com.microservices.user.dto.UserDTO;
import com.microservices.user.dto.UserUpdateRequest;
import com.microservices.user.entity.User;
import com.microservices.user.mapper.UserMapper;
import com.microservices.user.repository.UserRepository;
import com.microservices.user.security.SecurityContextUtil;
import com.microservices.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService
 * 
 * @author Baye Rane
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContextUtil securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testUser = User.builder()
            .id(testUserId)
            .email("test@test.com")
            .firstName("Test")
            .lastName("User")
            .city("Paris")
            .build();

        testUserDTO = UserDTO.builder()
            .id(testUserId)
            .email("test@test.com")
            .firstName("Test")
            .lastName("User")
            .fullName("Test User")
            .city("Paris")
            .build();
    }

    @Test
    void testGetUserByIdAsOwner() {
        // Arrange
        when(securityContext.canAccessResource(testUserId)).thenReturn(true);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        // Act
        UserDTO result = userService.getUserById(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        verify(securityContext).canAccessResource(testUserId);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void testGetUserByIdUnauthorized() {
        // Arrange
        when(securityContext.canAccessResource(testUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> userService.getUserById(testUserId));
        verify(securityContext).canAccessResource(testUserId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(securityContext.canAccessResource(testUserId)).thenReturn(true);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(testUserId));
    }

    @Test
    void testCreateUserSuccess() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
            .email("newuser@test.com")
            .password("Password123")
            .firstName("New")
            .lastName("User")
            .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        // Act
        UserDTO result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicateEmail() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
            .email("existing@test.com")
            .password("Password123")
            .firstName("Test")
            .lastName("User")
            .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.createUser(request));
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserSuccess() {
        // Arrange
        UserUpdateRequest request = UserUpdateRequest.builder()
            .firstName("Updated")
            .city("Lyon")
            .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
        doNothing().when(securityContext).requireAccessToResource(testUserId);

        // Act
        UserDTO result = userService.updateUser(testUserId, request);

        // Assert
        assertNotNull(result);
        verify(securityContext).requireAccessToResource(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUserAsAdmin() {
        // Arrange
        UUID userToDelete = UUID.randomUUID();
        when(securityContext.isCurrentUserAdmin()).thenReturn(true);
        when(securityContext.isCurrentUser(userToDelete)).thenReturn(false);
        when(userRepository.existsById(userToDelete)).thenReturn(true);

        // Act
        userService.deleteUser(userToDelete);

        // Assert
        verify(securityContext).isCurrentUserAdmin();
        verify(userRepository).deleteById(userToDelete);
    }

    @Test
    void testDeleteUserAsNonAdmin() {
        // Arrange
        when(securityContext.isCurrentUserAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> userService.deleteUser(testUserId));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteOwnAccountShouldFail() {
        // Arrange
        when(securityContext.isCurrentUserAdmin()).thenReturn(true);
        when(securityContext.isCurrentUser(testUserId)).thenReturn(true);
        when(userRepository.existsById(testUserId)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.deleteUser(testUserId));
        verify(userRepository, never()).deleteById(any());
    }
}