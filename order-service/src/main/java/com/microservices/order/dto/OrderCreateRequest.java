package com.microservices.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'une commande
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {
    
    @NotNull(message = "Le montant total est obligatoire.")
    @DecimalMin(value = "0.01", message = "Le montant total doit être supérieur à 0.")
    @Digits(integer = 8, fraction = 2, message = "Le montant total ne peut avoir que deux décimales.")
    private BigDecimal totalAmount;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
    private String description;

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
