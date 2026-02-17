package com.microservices.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.common.dto.ApiResponse;
import com.microservices.common.enums.OrderStatus;
import com.microservices.common.exception.BusinessException;
import com.microservices.common.exception.ResourceNotFoundException;
import com.microservices.order.client.UserServiceClient;
import com.microservices.order.dto.OrderCreateRequest;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.OrderUpdateRequest;
import com.microservices.order.dto.UserDTO;
import com.microservices.order.entity.Order;
import com.microservices.order.mapper.OrderMapper;
import com.microservices.order.repository.OrderRepository;
import com.microservices.order.security.SecurityContextUtil;
import com.microservices.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation du service Order
 * Gère toute la logique métier des commandes
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;
    private final SecurityContextUtil securityContextUtil;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderCreateRequest request) {
        log.info("Creating new order for user: {}", securityContextUtil.getCurrentUserId());

        // Génération du numéro de commande unique
        String orderNumber = generateOrderNumber();

        // Création de l'entité
        Order order = orderMapper.toEntity(request);
        order.setUserId(securityContextUtil.getCurrentUserId());
        order.setOrderNumber(orderNumber);

        // Validation du montant
        if (request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le montant total doit être supérieur à 0");
        }

        // Sauvegarde
        Order savedOrder = orderRepository.save(order);
        log.info(
            "Commande créée avec succès: {} par l'utilisateur: {}",
            savedOrder.getOrderNumber(),
            savedOrder.getUserId()
        );

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(savedOrder);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        log.debug("Récupèration de la commande: {}", orderId);

        Order order = findOrderById(orderId);

        // Vérification des permissions
        checkUserAccessToOrder(order);

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(order);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        log.debug("Récupèration du numéro de commande: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec le numéro: " + orderNumber));

        // Vérification des permissions
        checkUserAccessToOrder(order);

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(order);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Récupèration des commandes - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        // Uniquement ADMIN
        securityContextUtil.requireAdmin();

        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderDTO> getMyOrders(Pageable pageable) {
        UUID currentUserId = securityContextUtil.getCurrentUserId();
        log.debug("Récupèration des commandes de l'utilisateur: {}", currentUserId);

        return getOrdersByUserId(currentUserId, pageable);
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(UUID userId, Pageable pageable) {
        log.debug("Récupèration des commandes de l'utilisateur: {}", userId);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.debug("Récupèration des commandes par statut: {}", status);

        // Uniquement ADMIN
        securityContextUtil.requireAdmin();

        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderDTO> getOrdersByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable) {
        log.debug("Récupèration des commandes de l'utilisateur: {} par statut: {}", userId, status);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        Page<Order> orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(UUID orderId, OrderUpdateRequest request) {
        log.info("Mise à jour de la commande: {}", orderId);

        Order order = findOrderById(orderId);

        // Vérification des permissions
        checkUserAccessToOrder(order);

        // Vérification de la commande dans un état final
        if (order.isFinalStatus()) {
            throw new BusinessException("Impossible de modifier une commande dans un état final:" + order.getStatus());
        }

        // Validation de la transition de statut si demandée
        if (request.getStatus() != null) {
            validateStatusTransition(order, request.getStatus());
        }

        // Mise à jour de la commande
        orderMapper.updateEntityFromRequest(order, request);
        Order updatedOrder = orderRepository.save(order);

        log.info("Commande mise à jour avec succès: {}", orderId);

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(updatedOrder);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Mise à jour du statut de la commande: {} à {}", orderId, newStatus);

        Order order = findOrderById(orderId);

        // Vérification des permissions (ADMIN uniquement)
        securityContextUtil.requireAdmin();

        // Validation de la transition
        validateStatusTransition(order, newStatus);

        // Mise à jour de la commande
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Statut de la commande mis à jour avec succès: {} -> {}", orderId, newStatus);

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(updatedOrder);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(UUID orderId) {
        log.info("Annulation de la commande: {}", orderId);

        Order order = findOrderById(orderId);

        // Vérification des permissions
        checkUserAccessToOrder(order);

        // Vérification si annulable
        if (!order.isCancellable()) {
            throw new BusinessException("La commande ne peut pas être annulée (status actuel: " + order.getStatus() + ")");
        }

        // Annulation de la commande
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Commande annulée avec succès: {}", orderId);

        // Conversion en DTO et enrichissement
        OrderDTO orderDTO = orderMapper.toDTO(cancelledOrder);
        return enrichWithUserInfo(orderDTO);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId) {
        log.info("Suppression de la commande: {}", orderId);

        // Uniquement ADMIN
        securityContextUtil.requireAdmin();

        Order order = findOrderById(orderId);

        // Vérification - ne peut pas supprimmer que des commandes annulées
        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new BusinessException("Seules les commandes annulées peuvent être supprimées");
        }

        orderRepository.delete(order);
        log.info("Commande supprimée avec succès: {}", orderId);
    }

    @Override
    public long countUserOrders(UUID userId) {
        log.debug("Compte des commandes de l'utilisateur: {}", userId);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        return orderRepository.countByUserId(userId);
    }

    @Override
    public long countUserOrdersByStatus(UUID userId, OrderStatus status) {
        log.debug("Compte des commandes de l'utilisateur: {} par statut: {}", userId, status);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        return orderRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public BigDecimal calculateUserTotalAmount(UUID userId) {
        log.debug("Calcul du montant total des commandes de l'utilisateur: {}", userId);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        return orderRepository.sumTotalAmountByUserId(userId);
    }

    @Override
    public List<OrderDTO> getOrdersCreatedAfter(LocalDateTime date) {
        log.debug("Récupèration des commandes créees apres la date: {}", date);

        // Uniquement ADMIN
        securityContextUtil.requireAdmin();

        List<Order> orders = orderRepository.findByCreatedAtAfter(date);
        return orderMapper.toDTOList(orders);
    }

    @Override
    public List<OrderDTO> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Récupèration des commandes entre {} et {}", startDate, endDate);

        // Uniquement ADMIN
        securityContextUtil.requireAdmin();

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        return orderMapper.toDTOList(orders);
    }

    @Override
    public List<OrderDTO> getLatestUserOrders(UUID userId, int limit) {
        log.debug("Récupèration des {} derniers commandes de l'utilisateur: {}", limit, userId);

        // Vérification des permissions
        if (!securityContextUtil.isAdmin() && !userId.equals(securityContextUtil.getCurrentUserId())) {
            throw new BusinessException(
                "Vous n'êtes pas autorisé à voir les statistiques d'un autre utilisateur");
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findLatestByUserId(userId, pageable);
        return orderMapper.toDTOList(orders.getContent());
    }

    @Override
    public OrderDTO enrichWithUserInfo(OrderDTO orderDTO) {
        if (orderDTO == null || orderDTO.getUserId() == null) {
            return orderDTO;
        }

        try {
            log.debug("Récupèration de la commande {} avec les infos de l'utilisateur", orderDTO.getId());

            ApiResponse<UserDTO> response = userServiceClient.getUserById(
                orderDTO.getUserId().toString());

            if (response != null && response.getData() != null) {
                UserDTO user = response.getData();
                orderDTO.setUserEmail(user.getEmail());
                orderDTO.setUserName(user.getFullName());
            }
        } catch (Exception e) {
            log.warn("Erreur de récupération de lq commande avec les infos de l'utilisateur", e.getMessage());
        }

        return orderDTO;
    }

    @Override
    public String generateOrderNumber() {
        String prefix = "ORD";
        String datePattern = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd"));

        String orderNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            // Génère un nombre aléatoire à 6 chiffres
            int randomNum = (int) (Math.random() * 900000) + 100000;
            orderNumber = String.format("%s-%s-%06d", prefix, datePattern, randomNum);
            attempts++;

            if (attempts >= maxAttempts) {
                throw new BusinessException("Impossible de générer un numéro de commande unique après " + maxAttempts + " tentatives");
            }
        } while (orderRepository.existsByOrderNumber(orderNumber));

        log.debug("Génération du numéro unique de commande: {}", orderNumber);
        return orderNumber;
    }
    @Override
    public boolean canUserAccessOrder(UUID orderId, UUID userId) {
        Order order = findOrderById(orderId);
        return securityContextUtil.isAdmin() || order.getUserId().equals(userId);
    }

    // ==================== Méthodes Privées ====================

    // Trouve une commande par ID ou lève une exception
    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'ID: " + orderId));
    }

    // Vérifie si l'utilisateur connecté peut accèder à cette commande
    private void checkUserAccessToOrder(Order order) {
        UUID currentUserId = securityContextUtil.getCurrentUserId();

        if (!securityContextUtil.isAdmin() && !order.getUserId().equals(currentUserId)) {
            throw new BusinessException("Accès non autorisé");
        }
    }

    // Valide une transition de statut
    private void validateStatusTransition(Order order, OrderStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessException("Le nouveau statut ne peut pas être null");
        }

        OrderStatus currentStatus =  order.getStatus();

        if (currentStatus == newStatus) {
            log.debug("Status de la commande non modifié");
            return;
        }

        if (!order.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    String.format("Transition de statut invalide: %s -> %s", currentStatus, newStatus));
        }

        log.debug("Statut de la commande modifié de {} à {}", currentStatus, newStatus);
    }
}
