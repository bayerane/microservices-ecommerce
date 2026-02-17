package com.microservices.order.dto;

import com.microservices.common.enums.OrderStatus;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la mise à jour d'une commande
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateRequest {
    
    private OrderStatus status;

    @Size(max = 500, message = "L'adresse de livraison ne peut pas dépasser 500 caractères.")
    private String shippingAddress;

    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères.")
    private String shippingCity;

    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères.")
    private String shippingCountry;

    @Size(max = 20, message = "Le code postal ne peut pas dépasser 20 caractères.")
    private String shippingPostalCode;

    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères.")
    private String notes;
}
