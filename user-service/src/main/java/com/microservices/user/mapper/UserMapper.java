package com.microservices.user.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.microservices.user.dto.UserCreateRequest;
import com.microservices.user.dto.UserDTO;
import com.microservices.user.dto.UserUpdateRequest;
import com.microservices.user.entity.User;

/**
 * Mapper pour convertir entre Entity et DTO
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
public class UserMapper {
    
    // Convertit une entité User en UserDTO
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .postalCode(user.getPostalCode())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Convertit  une liste d'entités en liste de DTOs
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // Convertit un UserCreateRequest en entité User
    public User toEntity(UserCreateRequest request) {
        
        if (request == null) {
            return null;
        }

        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();
    }

    // Met à jour une entité User avec les données d'un UserUpdateRequest
    public void updateEntityFromRequest(User user, UserUpdateRequest request) {

        if (user == null || request == null) {
            return;
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }
    }
}
