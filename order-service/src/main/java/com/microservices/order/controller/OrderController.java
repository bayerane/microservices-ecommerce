package com.microservices.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.common.dto.ApiResponse;
import com.microservices.common.enums.OrderStatus;
import com.microservices.order.dto.OrderCreateRequest;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.OrderUpdateRequest;
import com.microservices.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Contrôleur REST pour la gestion des commandes
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "API de gestion des commandes")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    
    private final OrderService orderService;

    // Crée une nouvelle commande
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Créer une commande",
        description = "Crée une nouvelle commande pour l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Commande crée avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Données invalide"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Non authentifié"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
        @Valid @RequestBody OrderCreateRequest request
    ) {
        log.info("Réquisition de création de commande");

        OrderDTO order = orderService.createOrder(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                order,
                "Commande créée avec succès: " + order.getOrderNumber()
            ));
    }

    // Récupère une commande par son ID
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Récupérer une commande par ID",
        description = "Récupère les détails d'une commande. Les utilisateurs ne peuvent voir que leurs propres commandes."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commande trouvée",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
        @Parameter(description = "ID de la commande")
        @PathVariable UUID orderId
    ) {
        log.info("Requête de récupération de la commande: {}", orderId);

        OrderDTO order = orderService.getOrderById(orderId);

        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    // Récupère une commande par son numéro
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Récupérer une commande par numéro",
        description = "Récupère une commande par son numéro unique."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commande trouvée",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByNumber(
        @Parameter(description = "Numéro de commande (ex: ORD-20250124-123456)")
        @PathVariable String orderNumber
    ) {
        log.info("Requête de récupération de la commande par son numéro: {}", orderNumber);

        OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);

        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    // Récupère toutes les commandes ADMIN uniquement
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Récupérer toutes les commandes (ADMIN)",
        description = "Récupère la liste paginée de toutes les commandes. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getAllOrders(
        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri")
        @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("Requête de récupération de toutes les commandes - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);

        return ResponseEntity.ok(ApiResponse.success(
            orders,
            String.format("Page %d/%d - Total: %d commandes", page + 1, orders.getTotalPages(),orders.getTotalElements())
        ));
    }

    // Récupère les commandes de l'utilisateur connecté
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Mes commandes",
        description = "Récupère les commandes de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getMyOrders(
        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri")
        @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("Requête de récupération de mes commandes - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderDTO> orders = orderService.getMyOrders(pageable);

        return ResponseEntity.ok(ApiResponse.success(
            orders,
            String.format("Vous avez %d commande(s)", orders.getTotalElements())
        ));
    }
    
    // Récupère les commandes d'un utilisateur
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Commandes d'un utilisateur",
        description = "Récupère les commandes d'un utilisateur spécifique. Un utilisateur ne peut voir que ses propres commandes."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getUserOrders(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,

        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri")
        @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("Requête de récupération de mes commandes - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Récupère les commandes par statut (ADMIN uniquement)
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Commandes par statut (ADMIN)",
        description = "Récupère les commandes ayant un statut donné."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getOrdersByStatus(
        @Parameter(description = "Statut de la commande")
        @PathVariable OrderStatus status,    

        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri")
        @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("Requête de récupération de mes commandes - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(
            orders,
            String.format("%d commande(s) avec le statut %s", orders.getTotalElements(), status)
        ));
    }

    // Récupère les commandes d'un utilisation par statut
    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Commandes d'un utilisateur par statut",
        description = "Recherche les commandes d'un utilisateur avec un statut spécifique"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getUserOrdersByStatus(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,

        @Parameter(description = "Statut de la commande")
        @PathVariable OrderStatus status,

        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Taille de page")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Champ de tri")
        @RequestParam(defaultValue = "createdAt") String sortBy,

        @Parameter(description = "Direction de tri")
        @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("Requête de récupération des commandes de l'utilisateur: {} par statut: {}", userId, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderDTO> orders = orderService.getOrdersByUserIdAndStatus(userId, status, pageable);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Met à jour une commande
    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Mettre à jour une commande",
        description = "Mettre à jour les informations d'une commande. Les utilisateurs ne peuvennt modifier que leurs propres commandes."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commande mise à jour avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Transition de statut invalide"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(
        @Parameter(description = "ID de la commande")
        @PathVariable UUID orderId,

        @Parameter(description = "Commande mise à jour")
        @Valid @RequestBody OrderUpdateRequest request
    ) {
        log.info("Mise à jour de la commande: {}", orderId);

        OrderDTO updatedOrder = orderService.updateOrder(orderId, request);

        return ResponseEntity.ok(ApiResponse.success(
            updatedOrder,
            "Commande mise à jour avec succès"
        ));
    }

    // Met à jour le statut d'une commande (ADMIN uniquement)
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Mettre à jour le statut d'une commande (ADMIN)",
        description = "Change le statut d'une commande. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Statut de la commande mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Transition de statut invalide"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
        @Parameter(description = "ID de la commande")
        @PathVariable UUID orderId,

        @Parameter(description = "Nouveau statut de la commande")
        @RequestParam OrderStatus status
    ) {
        log.info("Mise à jour du statut de la commande: {} à {}", orderId, status);

        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(ApiResponse.success(
            updatedOrder,
            String.format("Statut de la commande mis à jour: %s", status.getLabel())
        ));
    }

    // Annule une commande
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Annuler une commande",
        description = "Annuler une commande si elle est dans un état annulable."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commande annulée avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "La commande ne peut pas être annulée"
        )
    })
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
        @Parameter(description = "ID de la commande")
        @PathVariable UUID orderId
    ) {
        log.info("Annulation de la commande: {}", orderId);

        OrderDTO cancelledOrder = orderService.cancelOrder(orderId);

        return ResponseEntity.ok(ApiResponse.success(
            cancelledOrder,
            "Commande annulée avec succès"
        ));
    }

    // Supprime une commande (ADMIN uniquement)
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Supprimer une commande (ADMIN)",
        description = "Supprime définitivement une commande annulée. Réservé aux administrateurs."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Commande supprimée avec succès"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Commande non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
        @Parameter(description = "ID de la commande")
        @PathVariable UUID orderId
    ) {
        log.info("Suppression de la commande: {}", orderId);

        orderService.deleteOrder(orderId);

        return ResponseEntity.ok(ApiResponse.success(
            null,
            "Commande supprimée avec succès"
        ));
    }

    // Statistiques - Compte les commandes d'un utilisateur
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Compter les commandes d'un utilisateur",
        description = "Retourne le nombre total de commandes d'un utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Nombre total de commandes",
            content = @Content(schema = @Schema(implementation = Long.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Long>> countUserOrders(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId
    ) {
        log.debug("Compte des commandes de l'utilisateur: {}", userId);

        long count = orderService.countUserOrders(userId);

        return ResponseEntity.ok(ApiResponse.success(
            count,
            String.format("%d commande(s) trouvée(s)", count)
        ));
    }

    // Statistiques - Compte les commandes d'un utilisateur par statut
    @GetMapping("/user/{userId}/count/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Compter les commandes par statut",
        description = "Retourne le nombre de commandes d'un utilisateur par statut donné"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Nombre de commandes par statut",
            content = @Content(schema = @Schema(implementation = Long.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<Long>> countUserOrdersByStatus(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,

        @Parameter(description = "Statut de la commande")
        @PathVariable OrderStatus status
    ) {
        log.debug("Compte des commandes de l'utilisateur: {} par statut: {}", userId, status);

        Long countByStatus = orderService.countUserOrdersByStatus(userId, status);

        return ResponseEntity.ok(ApiResponse.success(
            countByStatus,
            String.format("%d commande(s) %s", countByStatus, status.getLabel())
        ));
    }

    // Statistiques -  Calcule le montant total des commandes d'un utilisateur
    @GetMapping("/user/{userId}/total-amount")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Montant total des commandes",
        description = "Calcule le montant total des commandes d'un utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Montant total des commandes",
            content = @Content(schema = @Schema(implementation = BigDecimal.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Utilisateur non rencontré"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<BigDecimal>> calculateUserTotalAmount(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId
    ) {
        log.debug("Calcul du montant total des commandes de l'utilisateur: {}", userId);

        BigDecimal totalAmount = orderService.calculateUserTotalAmount(userId);

        return ResponseEntity.ok(ApiResponse.success(
            totalAmount,
            String.format("Montant total: %.2f FFCA", totalAmount)
        ));
    }

    // Recherche - Commandes créées après date (ADMIN)
    @GetMapping("/search/after")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Commandes après une date (ADMIN)",
        description = "Recherche les commandes créees après une date donnée"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<List<OrderDTO>>> searchOrdersCreatedAfter(
        @Parameter(description = "Date de référence (format: yyyy-MM-dd'T'HH:mm:ss)")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ) {
        log.debug("Recherche commandes cr créées après la date: {}", date);

        List<OrderDTO> orders = orderService.getOrdersCreatedAfter(date);

        return ResponseEntity.ok(ApiResponse.success(
            orders,
            String.format("%d commande(s) trouvée(s)", orders.size())
        ));
    }

    // Recherche - Commandes entre deux dates (ADMIN)
    @GetMapping("/search/between")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Commandes entre deux dates (ADMIN)",
        description = "Recherche les commandes crées entre deux dates données"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Commandes trouvées avec succès",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<List<OrderDTO>>> searchOrdersBetweenDates(
        @Parameter(description = "Date de début (format: yyyy-MM-dd'T'HH:mm:ss)")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

        @Parameter(description = "Date de fin (format: yyyy-MM-dd'T'HH:mm:ss)")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.debug("Recherche commandes crées entre {} et {}", startDate, endDate);

        List<OrderDTO> orders = orderService.getOrdersBetweenDates(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(
            orders,
            String.format("%d commande(s) trouvée(s)", orders.size())
        ));
    }

    // Récupère les dernières commandes d'un utilisateur
    @GetMapping("/user/{userId}/latest")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Dernières commandes d'un utilisateur",
        description = "Récupère les N dernières commandes d'un utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Dernières commandes d'un utilisateur",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Utilisateur non rencontré"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getLatestUserOrders(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,

        @Parameter(description = "Nombre de commandes à retourner")
        @RequestParam(defaultValue = "5") int limit
    ) {
        log.debug("Récupération des {} dernières commandes de l'utilisateur: {}", limit, userId);

        List<OrderDTO> orders = orderService.getLatestUserOrders(userId, limit);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    //Health check endpoint
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Vérifie que le service est opérationnel"
    )
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
            "OK",
            "Order Service en cours de fonctionnement"
        ));
    }
}
