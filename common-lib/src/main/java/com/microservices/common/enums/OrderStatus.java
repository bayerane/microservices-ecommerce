package com.microservices.common.enums;

/**
 * Énumération des statuts possibles d'une commande
 * 
 * @author Baye Rane
 * @version 1.0
 */
public enum OrderStatus {
    
    // Commande créée, en attente de confirmation
    PENDING("PENDING", "En attente", false, true),
    // Commande confirmée, en préparation
    CONFIRMED("CONFIRMED", "Confirmée", false, true),
    // Commande expédiée
    SHIPPED("SHIPPED", "Expédiée", false, false),
    // Commande livrée avec succès
    DELIVERED("DELIVERED", "Livrée", true, false),
    // Commande annulée
    CANCELLED("CANCELED", "Annulée", true, false);

    private final String code;
    private final String label;
    private final boolean isFinalStatus;
    private final boolean isCancellable;

    OrderStatus(String code, String label, boolean isFinalStatus, boolean isCancellable) {
        this.code = code;
        this.label = label;
        this.isFinalStatus = isFinalStatus;
        this.isCancellable = isCancellable;
    }

    // Récupère le code du statut
    public String getCode() {
        return code;
    }

    // Récupère le libellé du statut
    public String getLabel() {
        return label;
    }

    // Vérifie si le statut est final(ne peut plus changer)
    public boolean isFinalStatus() {
        return isFinalStatus;
    }

    // Vérifie si une commande avec ce statut peut être annulée
    public boolean isCancellable() {
        return isCancellable;
    }

    // Convertit une chaîne en enum OrderStatus
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Code de statut invalide: " + code);
    }

    // Vérifie si une transition de statut est valide
    public boolean canTransitionTo(OrderStatus targetStatus) {
        if (this.isFinalStatus) {
            return false;
        }

        return switch (this) {
            case PENDING -> targetStatus == CONFIRMED || targetStatus == CANCELLED;
            case CONFIRMED -> targetStatus == SHIPPED || targetStatus == CANCELLED;
            case SHIPPED -> targetStatus == DELIVERED;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return this.code;
    }
}
