package com.microservices.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservices.auth.entity.User;
import com.microservices.common.enums.Role;

/**
 * Repository pour la gestion des utilisateurs
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Recherche un utilisateur par son email
    Optional<User> findByEmail(String email);

    // Vérifie si un email existe déjà
    boolean existsByEmail(String email);

    // Recherche tous les utilisateurs par rôle
    List<User> findByRole(Role role);

    // Recherche tous les utilisateurs actifs
    List<User> findByEnabledTrue();

    // Recherche tous les utilisateurs désactivés
    List<User> findByEnabledFalse();

    // Compte le nombre d'utilisateurs par rôle
    long countByRole(Role role);

    // Compte le nombre total d'utilisateurs actifs
    long countByEnabledTrue();

    // Recherche un utilisateur par email (case insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    // Recherche les utilisateurs créés après une certaine date
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(LocalDateTime date);
}
