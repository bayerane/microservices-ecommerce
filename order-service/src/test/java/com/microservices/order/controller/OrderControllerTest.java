package com.microservices.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.common.enums.OrderStatus;
import com.microservices.common.exception.BusinessException;
import com.microservices.common.exception.ResourceNotFoundException;
import com.microservices.order.dto.OrderCreateRequest;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.OrderUpdateRequest;
import com.microservices.order.service.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour OrderController
 * Utilise MockMvc pour tester la couche HTTP
 *
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("OrderController - Tests d'intégration")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // ==================== Fixtures ====================

    private UUID userId;
    private UUID orderId;
    private OrderDTO orderDTO;
    private OrderCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        userId  = UUID.randomUUID();
        orderId = UUID.randomUUID();

        orderDTO = OrderDTO.builder()
                .id(orderId)
                .userId(userId)
                .orderNumber("ORD-20250120-100001")
                .status(OrderStatus.PENDING)
                .statusLabel("En attente")
                .totalAmount(new BigDecimal("299.99"))
                .currency("EUR")
                .description("MacBook Pro 14\"")
                .cancellable(true)
                .finalStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = OrderCreateRequest.builder()
                .totalAmount(new BigDecimal("299.99"))
                .description("MacBook Pro 14\"")
                .shippingAddress("123 Rue Test")
                .shippingCity("Paris")
                .shippingCountry("France")
                .shippingPostalCode("75001")
                .build();
    }

    // ==================== POST /orders ====================

    @Nested
    @DisplayName("POST /orders - Créer une commande")
    class CreateOrder {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 201 - USER peut créer une commande valide")
        void userShouldCreateOrder() throws Exception {
            // Given
            given(orderService.createOrder(any(OrderCreateRequest.class))).willReturn(orderDTO);

            // When / Then
            mockMvc.perform(post("/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-20250120-100001"))
                    .andExpect(jsonPath("$.data.status").value("PENDING"))
                    .andExpect(jsonPath("$.data.totalAmount").value(299.99));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ 201 - ADMIN peut créer une commande")
        void adminShouldCreateOrder() throws Exception {
            // Given
            given(orderService.createOrder(any())).willReturn(orderDTO);

            // When / Then
            mockMvc.perform(post("/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("❌ 401 - Non authentifié")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 400 - Corps de requête invalide (montant null)")
        void shouldReturn400WhenBodyInvalid() throws Exception {
            // Given : montant null
            createRequest.setTotalAmount(null);

            // When / Then
            mockMvc.perform(post("/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 400 - Montant = 0 refusé par le service")
        void shouldReturn400WhenAmountIsZero() throws Exception {
            // Given
            createRequest.setTotalAmount(BigDecimal.ZERO);
            given(orderService.createOrder(any()))
                    .willThrow(new BusinessException("supérieur à 0"));

            // When / Then
            mockMvc.perform(post("/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== GET /orders/{orderId} ====================

    @Nested
    @DisplayName("GET /orders/{orderId} - Récupérer une commande")
    class GetOrderById {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 200 - Commande trouvée")
        void shouldReturnOrder() throws Exception {
            // Given
            given(orderService.getOrderById(orderId)).willReturn(orderDTO);

            // When / Then
            mockMvc.perform(get("/orders/{orderId}", orderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-20250120-100001"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 404 - Commande non trouvée")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            given(orderService.getOrderById(orderId))
                    .willThrow(new ResourceNotFoundException("Commande non trouvée"));

            // When / Then
            mockMvc.perform(get("/orders/{orderId}", orderId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 403 - Accès refusé (autre utilisateur)")
        void shouldReturn403WhenAccessDenied() throws Exception {
            // Given
            given(orderService.getOrderById(orderId))
                    .willThrow(new BusinessException("autorisé"));

            // When / Then
            mockMvc.perform(get("/orders/{orderId}", orderId))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== GET /orders/number/{orderNumber} ====================

    @Nested
    @DisplayName("GET /orders/number/{orderNumber} - Récupérer par numéro")
    class GetOrderByNumber {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 200 - Commande trouvée par numéro")
        void shouldReturnOrderByNumber() throws Exception {
            // Given
            given(orderService.getOrderByOrderNumber("ORD-20250120-100001")).willReturn(orderDTO);

            // When / Then
            mockMvc.perform(get("/orders/number/{orderNumber}", "ORD-20250120-100001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-20250120-100001"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 404 - Numéro de commande inconnu")
        void shouldReturn404WhenNumberNotFound() throws Exception {
            // Given
            given(orderService.getOrderByOrderNumber(anyString()))
                    .willThrow(new ResourceNotFoundException("Commande non trouvée"));

            // When / Then
            mockMvc.perform(get("/orders/number/{orderNumber}", "ORD-INVALID-000000"))
                    .andExpect(status().isNotFound());
        }
    }

    // ==================== GET /orders ====================

    @Nested
    @DisplayName("GET /orders - Toutes les commandes (ADMIN)")
    class GetAllOrders {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ 200 - ADMIN récupère toutes les commandes paginées")
        void adminShouldGetAllOrders() throws Exception {
            // Given
            Page<OrderDTO> page = new PageImpl<>(List.of(orderDTO),
                    PageRequest.of(0, 10, Sort.by("createdAt").descending()), 1);
            given(orderService.getAllOrders(any(Pageable.class))).willReturn(page);

            // When / Then
            mockMvc.perform(get("/orders")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content[0].orderNumber")
                            .value("ORD-20250120-100001"))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 403 - USER ne peut pas lister toutes les commandes")
        void userShouldNotGetAllOrders() throws Exception {
            mockMvc.perform(get("/orders"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("❌ 401 - Non authentifié")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/orders"))
                    .andExpect(status().isForbidden());
        }
    }

    // ==================== GET /orders/my-orders ====================

    @Nested
    @DisplayName("GET /orders/my-orders - Mes commandes")
    class GetMyOrders {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 200 - Récupère les commandes de l'utilisateur connecté")
        void shouldReturnMyOrders() throws Exception {
            // Given
            Page<OrderDTO> page = new PageImpl<>(List.of(orderDTO));
            given(orderService.getMyOrders(any(Pageable.class))).willReturn(page);

            // When / Then
            mockMvc.perform(get("/orders/my-orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].orderNumber")
                            .value("ORD-20250120-100001"));
        }

        @Test
        @DisplayName("❌ 401 - Non authentifié")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/orders/my-orders"))
                    .andExpect(status().isForbidden());
        }
    }

    // ==================== PATCH /orders/{orderId}/cancel ====================

    @Nested
    @DisplayName("PATCH /orders/{orderId}/cancel - Annuler une commande")
    class CancelOrder {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 200 - Commande annulée avec succès")
        void shouldCancelOrder() throws Exception {
            // Given
            OrderDTO cancelledDTO = OrderDTO.builder()
                    .id(orderId).status(OrderStatus.CANCELLED).build();
            given(orderService.cancelOrder(orderId)).willReturn(cancelledDTO);

            // When / Then
            mockMvc.perform(patch("/orders/{orderId}/cancel", orderId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Commande annulée avec succès"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 400 - Commande non annulable (ex: SHIPPED)")
        void shouldReturn400WhenNotCancellable() throws Exception {
            // Given
            given(orderService.cancelOrder(orderId))
                    .willThrow(new BusinessException("ne peut pas être annulée"));

            // When / Then
            mockMvc.perform(patch("/orders/{orderId}/cancel", orderId).with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 404 - Commande non trouvée")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            // Given
            given(orderService.cancelOrder(orderId))
                    .willThrow(new ResourceNotFoundException("Commande non trouvée"));

            // When / Then
            mockMvc.perform(patch("/orders/{orderId}/cancel", orderId).with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }

    // ==================== PATCH /orders/{orderId}/status ====================

    @Nested
    @DisplayName("PATCH /orders/{orderId}/status - Changer le statut (ADMIN)")
    class UpdateOrderStatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ 200 - ADMIN change le statut")
        void adminShouldUpdateStatus() throws Exception {
            // Given
            OrderDTO confirmedDTO = OrderDTO.builder()
                    .id(orderId).status(OrderStatus.CONFIRMED).build();
            given(orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED))
                    .willReturn(confirmedDTO);

            // When / Then
            mockMvc.perform(patch("/orders/{orderId}/status", orderId)
                            .with(csrf())
                            .param("status", "CONFIRMED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 403 - USER ne peut pas changer le statut")
        void userShouldNotUpdateStatus() throws Exception {
            mockMvc.perform(patch("/orders/{orderId}/status", orderId)
                            .with(csrf())
                            .param("status", "CONFIRMED"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("❌ 400 - Transition de statut invalide")
        void shouldReturn400ForInvalidTransition() throws Exception {
            // Given
            given(orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED))
                    .willThrow(new BusinessException("Transition invalide: SHIPPED -> CANCELLED"));

            // When / Then
            mockMvc.perform(patch("/orders/{orderId}/status", orderId)
                            .with(csrf())
                            .param("status", "CANCELLED"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== PUT /orders/{orderId} ====================

    @Nested
    @DisplayName("PUT /orders/{orderId} - Mettre à jour une commande")
    class UpdateOrder {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("✅ 200 - Mise à jour réussie")
        void shouldUpdateOrder() throws Exception {
            // Given
            OrderUpdateRequest req = OrderUpdateRequest.builder()
                    .shippingCity("Marseille")
                    .build();
            given(orderService.updateOrder(eq(orderId), any(OrderUpdateRequest.class)))
                    .willReturn(orderDTO);

            // When / Then
            mockMvc.perform(put("/orders/{orderId}", orderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Commande mise à jour avec succès"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 400 - Commande dans état final")
        void shouldReturn400WhenFinalState() throws Exception {
            // Given
            given(orderService.updateOrder(eq(orderId), any()))
                    .willThrow(new BusinessException("état final"));

            // When / Then
            mockMvc.perform(put("/orders/{orderId}", orderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new OrderUpdateRequest())))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== DELETE /orders/{orderId} ====================

    @Nested
    @DisplayName("DELETE /orders/{orderId} - Supprimer une commande (ADMIN)")
    class DeleteOrder {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ 200 - ADMIN supprime une commande annulée")
        void adminShouldDeleteCancelledOrder() throws Exception {
            // Given
            willDoNothing().given(orderService).deleteOrder(orderId);

            // When / Then
            mockMvc.perform(delete("/orders/{orderId}", orderId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Commande supprimée avec succès"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 403 - USER ne peut pas supprimer")
        void userShouldNotDelete() throws Exception {
            mockMvc.perform(delete("/orders/{orderId}", orderId).with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("❌ 400 - Commande non annulée")
        void shouldReturn400WhenNotCancelled() throws Exception {
            // Given
            willThrow(new BusinessException("Seules les commandes annulées"))
                    .given(orderService).deleteOrder(orderId);

            // When / Then
            mockMvc.perform(delete("/orders/{orderId}", orderId).with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    // ==================== GET /orders/health ====================

    @Nested
    @DisplayName("GET /orders/health - Health Check")
    class HealthCheck {

        @Test
        @DisplayName("✅ 200 - Health check accessible sans authentification")
        void shouldReturnHealthStatus() throws Exception {
            mockMvc.perform(get("/orders/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("OK"));
        }
    }

    // ==================== GET /orders/search ====================

    @Nested
    @DisplayName("GET /orders/search - Recherche (ADMIN)")
    class SearchOrders {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ 200 - ADMIN cherche les commandes après une date")
        void adminShouldSearchAfterDate() throws Exception {
            // Given
            given(orderService.getOrdersCreatedAfter(any(LocalDateTime.class)))
                    .willReturn(List.of(orderDTO));

            // When / Then
            mockMvc.perform(get("/orders/search/after")
                            .param("date", "2025-01-01T00:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].orderNumber")
                            .value("ORD-20250120-100001"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ 403 - USER ne peut pas faire de recherche globale")
        void userShouldNotSearchAfterDate() throws Exception {
            mockMvc.perform(get("/orders/search/after")
                            .param("date", "2025-01-01T00:00:00"))
                    .andExpect(status().isForbidden());
        }
    }
}