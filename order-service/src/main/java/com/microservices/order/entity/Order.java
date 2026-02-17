package com.microservices.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.microservices.common.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité Order pour le Order Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "FCFA";

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "notes", length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Vérifie si la commande peut être annulée
    public boolean isCancellable() {
        return status != null && status.isCancellable();
    }

    // Vérifie si la commande est dans un état final
    public boolean isFinalStatus() {
        return status != null && status.isFinalStatus();
    }

    // Vérifie si la transition de statut est valide
    public boolean canTransitionTo(OrderStatus newStatus) {
        return status != null && status.canTransitionTo(newStatus);
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (currency == null) {
            currency = "FCFA";
        }
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", orderNumber='" + orderNumber + '\'' +
            ", userId=" + userId +
            ", status=" + status +
            ", totalAmount=" + totalAmount +
            ", createdAt=" + createdAt +
            '}';
    }
}
