package com.microservices.order.repository;

import java.math.BigDecimal;
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

import com.microservices.common.enums.OrderStatus;
import com.microservices.order.entity.Order;

/**
 * Repository pour la gestion des commandes
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    // Recherche une commande par son nuuméro
    Optional<Order> findByOrderNumber(String orderNumber);

    // Vérifie si un numéro de commande existe
    boolean existsByOrderNumber(String orderNumber);

    // Recherche toutes les commandes d'un utilisateur
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    // Recherche les commandes d'un utilisateur par statut
    Page<Order> findByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable);

    // Recherche toutes les commandes par statut
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Compte les commandes d'un utilisateur
    long countByUserId(UUID userId);

    // Compte les commandes d'un utilisateur par statut
    long countByUserIdAndStatus(UUID userId, OrderStatus status);

    // Calcule le montant total des commandes d'un utilisateur
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.userId = :userId")
    BigDecimal sumTotalAmountByUserId(@Param ("userId") UUID userId);

    // Recherche les commandes créées après une date
    List<Order> findByCreatedAtAfter(LocalDateTime createdAt);

    // Recherche les commandes entre deux dates
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Recherche les commandes d'un utilisateur entre deux dates
    List<Order> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    // Recherche les dernières commandes d'un utilisateur
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    Page<Order> findLatestByUserId(@Param("userId") UUID userId, Pageable pageable);
}
