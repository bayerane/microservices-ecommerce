package com.microservices.order.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.microservices.order.dto.OrderCreateRequest;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.OrderUpdateRequest;
import com.microservices.order.entity.Order;

/**
 * Mapper pour convertir entre Entity et DTO
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
public class OrderMapper {
    
    // Convertit une entité Order en OrderDTO
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDTO.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .statusLabel(order.getStatus() != null ? order.getStatus().getLabel() : null)
            .totalAmount(order.getTotalAmount())
            .currency(order.getCurrency())
            .description(order.getDescription())
            .shippingAddress(order.getShippingAddress())
            .shippingCity(order.getShippingCity())
            .shippingCountry(order.getShippingCountry())
            .shippingPostalCode(order.getShippingPostalCode())
            .notes(order.getNotes())
            .cancellable(order.isCancellable())
            .finalStatus(order.isFinalStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }

    // Convertit une liste d'entités en liste de DTOs
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return List.of();
        }

        return orders.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // Convertit un OrderCreateRequest en entité Order
    public Order toEntity(OrderCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Order.builder()
            .totalAmount(request.getTotalAmount())
            .description(request.getDescription())
            .shippingAddress(request.getShippingAddress())
            .shippingCity(request.getShippingCity())
            .shippingCountry(request.getShippingCountry())
            .shippingPostalCode(request.getShippingPostalCode())
            .notes(request.getNotes())
            .build();
    }

    // Met à jour une entité Order avec les données d'un OrderUpdateRequest
    public void updateEntityFromRequest(Order order, OrderUpdateRequest request) {
        if (order == null || request == null) {
            return;
        }

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (request.getShippingAddress() != null) {
            order.setShippingAddress(request.getShippingAddress());
        }
        if (request.getShippingCity() != null) {
            order.setShippingCity(request.getShippingCity());
        }
        if (request.getShippingCountry() != null) {
            order.setShippingCountry(request.getShippingCountry());
        }
        if (request.getShippingPostalCode() != null) {
            order.setShippingPostalCode(request.getShippingPostalCode());
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
    }
}
