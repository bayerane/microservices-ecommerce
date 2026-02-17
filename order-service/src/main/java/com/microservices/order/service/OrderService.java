package com.microservices.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.microservices.common.enums.OrderStatus;
import com.microservices.order.dto.OrderCreateRequest;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.OrderUpdateRequest;

/**
 * Interface du service Order
 * Gère toute la logique métier des commandes
 * 
 * @author Baye Rane
 * @version 1.0
 */
public interface OrderService {
    
    // Crée une nouvelle commande
    OrderDTO createOrder(OrderCreateRequest request);

    // Récupère une commande par sin ID
    // Vérifie les permissions (USER voit seulement ses commandes)
    OrderDTO getOrderById(UUID orderId);

    // Récupère une commande par son numéro
    // Vérifie les permissions
    OrderDTO getOrderByOrderNumber(String orderNumber);

    // Récupère toutes les commandes (AD?IN uniquement)
    Page<OrderDTO> getAllOrders(Pageable pageable);

    // Récupère les commandes de l'utilisateur connecté
    Page<OrderDTO> getMyOrders(Pageable pageable);

    // Récupère les commandes d'un utilisateur spécifique
    // L'utilisateur peut seulement voir ses propres commandes
    // ADMIN peut voir toutes les commandes
    Page<OrderDTO> getOrdersByUserId(UUID userId, Pageable pageable);

    // Récupère les commandes par statut (ADMIN uniquement)
    Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    // Récupère les commandes de l'utlisateur par statut
    Page<OrderDTO> getOrdersByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable);

    // Met à jour une commande
    // Vérifie les permissions et les transitions de statut
    OrderDTO updateOrder(UUID orderId, OrderUpdateRequest request);

    // Met à jour le statut d'une commande
    // Valide les transitions de statut autorisées
    OrderDTO updateOrderStatus(UUID orderId, OrderStatus newStatus);

    // Annule une commande
    // Vérifie si la commande est annulable
    OrderDTO cancelOrder(UUID orderId);

    // Supprime une commande (ADMIN uniquement)
    void deleteOrder(UUID orderId);

    // Compte les commandes d'un utilisateur
    long countUserOrders(UUID userId);

    // Compte les commandes d'un utilisateur par statut
    long countUserOrdersByStatus(UUID userId, OrderStatus status);

    // Calcule le montant total des commandes d'un utilisateur
    BigDecimal calculateUserTotalAmount(UUID userId);

    // Récupère les commandes créées après une date
    List<OrderDTO> getOrdersCreatedAfter(LocalDateTime date);

    // Récupère les commandes entre deux dates
    List<OrderDTO> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    // Récupère les dernières commandes d'un utilisateur
    List<OrderDTO> getLatestUserOrders(UUID userId, int limit);

    // Enrichit un OrderDTO avec les informations utilisateur
    // Utilise Feign pour appeler User Service
    OrderDTO enrichWithUserInfo(OrderDTO orderDTO);

    // Génère un numéro de commande unique
    // Format: ORD-YYYYMMDD-XXXX
    String generateOrderNumber();

    // Vérifie si un utilisateur peut accéder à une commande
    boolean canUserAccessOrder(UUID orderId, UUID userId);
}
