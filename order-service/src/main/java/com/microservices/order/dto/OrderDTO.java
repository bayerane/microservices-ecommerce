package com.microservices.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.common.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les données de commande
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    
    private UUID id;
    private UUID userId;
    private String orderNumber;
    private OrderStatus status;
    private String statusLabel;
    private BigDecimal totalAmount;
    private String currency;
    private String description;
    private String shippingAddress;
    private String shippingCity;
    private String shippingCountry;
    private String shippingPostalCode;
    private String notes;
    private Boolean cancellable;
    private Boolean finalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations utilisateur (si chargées via feign)
    private String userEmail;
    private String userName;
}
