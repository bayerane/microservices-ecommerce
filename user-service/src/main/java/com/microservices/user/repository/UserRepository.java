package com.microservices.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservices.user.entity.User;

/**
 * Repository pour la gestion des utilisateurs
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Recherche un utilisateur par email
    Optional<User> findByEmail(String email);

    // Vérifie si un email existe déjà
    boolean existsByEmail(String email);

    // Recherche les utilisateurs par nom de famille
    Page<User> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // Recherche les utilisateurs par prénom
    Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // Recherche les utilisateurs par ville
    List<User> findByCity(String city);

    // Recherche les utilisateurs par pays
    List<User> findByCountry(String country);

    // Recherche globale par nom, prénom ou email
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
    )
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Recherche les utilisateurs créés après une certaine date
    List<User> findByCreatedAtAfter(LocalDateTime createdAt);

    // Compte le nombre d'utilisateurs par ville
    long countByCity(String city);

    // Compte le nombre d'utilisateurs par pays
    long countByCountry(String country);

    // Recherche un utilisateur par email (case insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);
}
