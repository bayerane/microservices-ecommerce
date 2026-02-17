package com.microservices.order.service;

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
import com.microservices.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Tests unitaires pour OrderServiceImpl
 *
 * @author Baye Rane
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService - Tests Unitaires")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private UserServiceClient userServiceClient;
    @Mock private SecurityContextUtil securityContextUtil;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ==================== Fixtures ====================

    private UUID userId;
    private UUID orderId;
    private Order order;
    private OrderDTO orderDTO;
    private OrderCreateRequest createRequest;
    private OrderUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        userId  = UUID.randomUUID();
        orderId = UUID.randomUUID();

        order = Order.builder()
                .id(orderId)
                .userId(userId)
                .orderNumber("ORD-20250120-100001")
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("299.99"))
                .currency("EUR")
                .description("MacBook Pro 14\"")
                .shippingAddress("123 Rue Test")
                .shippingCity("Paris")
                .shippingCountry("France")
                .shippingPostalCode("75001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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

        updateRequest = OrderUpdateRequest.builder()
                .shippingAddress("456 Rue Nouvelle")
                .shippingCity("Lyon")
                .build();
    }

    // ==================== createOrder ====================

    @Nested
    @DisplayName("createOrder()")
    class CreateOrder {

        @Test
        @DisplayName("✅ Doit créer une commande avec succès")
        void shouldCreateOrderSuccessfully() {
            // Given
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);
            given(orderMapper.toEntity(createRequest)).willReturn(order);
            given(orderRepository.save(any(Order.class))).willReturn(order);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.createOrder(createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getOrderNumber()).isEqualTo("ORD-20250120-100001");
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(result.getTotalAmount()).isEqualByComparingTo("299.99");

            then(orderRepository).should().save(any(Order.class));
            then(orderMapper).should().toDTO(order);
        }

        @Test
        @DisplayName("❌ Doit échouer si le montant est nul")
        void shouldFailWhenAmountIsZero() {
            // Given
            createRequest.setTotalAmount(BigDecimal.ZERO);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);
            given(orderMapper.toEntity(createRequest)).willReturn(order);

            // When / Then
            assertThatThrownBy(() -> orderService.createOrder(createRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("supérieur à 0");

            then(orderRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("❌ Doit échouer si le montant est négatif")
        void shouldFailWhenAmountIsNegative() {
            // Given
            createRequest.setTotalAmount(new BigDecimal("-10.00"));
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);
            given(orderMapper.toEntity(createRequest)).willReturn(order);

            // When / Then
            assertThatThrownBy(() -> orderService.createOrder(createRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("✅ Doit générer un numéro de commande unique")
        void shouldGenerateUniqueOrderNumber() {
            // Given : premier numéro déjà existant, deuxième libre
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.existsByOrderNumber(anyString()))
                    .willReturn(true)   // 1er essai : existe déjà
                    .willReturn(false);  // 2ème essai : libre
            given(orderMapper.toEntity(createRequest)).willReturn(order);
            given(orderRepository.save(any(Order.class))).willReturn(order);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.createOrder(createRequest);

            // Then
            assertThat(result).isNotNull();
            then(orderRepository).should(times(2)).existsByOrderNumber(anyString());
        }
    }

    // ==================== getOrderById ====================

    @Nested
    @DisplayName("getOrderById()")
    class GetOrderById {

        @Test
        @DisplayName("✅ Doit retourner la commande pour son propriétaire")
        void shouldReturnOrderForOwner() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.getOrderById(orderId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("✅ Doit retourner la commande pour un ADMIN")
        void shouldReturnOrderForAdmin() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(true);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.getOrderById(orderId);

            // Then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande n'existe pas")
        void shouldFailWhenOrderNotFound() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> orderService.getOrderById(orderId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ressource non trouvée");
        }

        @Test
        @DisplayName("❌ Doit refuser l'accès à un autre utilisateur")
        void shouldDenyAccessToOtherUser() {
            // Given
            UUID otherUserId = UUID.randomUUID();
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(otherUserId);

            // When / Then
            assertThatThrownBy(() -> orderService.getOrderById(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("autorisé");
        }
    }

    // ==================== updateOrder ====================

    @Nested
    @DisplayName("updateOrder()")
    class UpdateOrder {

        @Test
        @DisplayName("✅ Doit mettre à jour les champs d'une commande PENDING")
        void shouldUpdatePendingOrder() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.save(order)).willReturn(order);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.updateOrder(orderId, updateRequest);

            // Then
            assertThat(result).isNotNull();
            then(orderMapper).should().updateEntityFromRequest(order, updateRequest);
            then(orderRepository).should().save(order);
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande est DELIVERED (état final)")
        void shouldFailWhenOrderIsDelivered() {
            // Given
            order.setStatus(OrderStatus.DELIVERED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);

            // When / Then
            assertThatThrownBy(() -> orderService.updateOrder(orderId, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("final");
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande est CANCELLED (état final)")
        void shouldFailWhenOrderIsCancelled() {
            // Given
            order.setStatus(OrderStatus.CANCELLED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);

            // When / Then
            assertThatThrownBy(() -> orderService.updateOrder(orderId, updateRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== updateOrderStatus ====================

    @Nested
    @DisplayName("updateOrderStatus()")
    class UpdateOrderStatus {

        @Test
        @DisplayName("✅ ADMIN peut confirmer une commande PENDING")
        void adminShouldConfirmPendingOrder() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            // given(securityContextUtil.isAdmin()).willReturn(true);
            // order.status = PENDING → CONFIRMED est une transition valide
            given(orderRepository.save(order)).willReturn(order);

            OrderDTO confirmedDTO = OrderDTO.builder()
                    .id(orderId).status(OrderStatus.CONFIRMED).build();
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);

            // Then
            assertThat(result).isNotNull();
            then(orderRepository).should().save(order);
        }

        @Test
        @DisplayName("❌ Doit échouer pour un USER (non ADMIN)")
        void shouldFailForNonAdmin() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            willThrow(new BusinessException("droits d'administrateur"))
                    .given(securityContextUtil).requireAdmin();

            // When / Then
            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("administrateur");
        }

        @Test
        @DisplayName("❌ Doit échouer pour une transition invalide (SHIPPED → CANCELLED)")
        void shouldFailForInvalidTransition() {
            // Given
            order.setStatus(OrderStatus.SHIPPED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            //given(securityContextUtil.isAdmin()).willReturn(true);

            // When / Then
            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("invalide");
        }
    }

    // ==================== cancelOrder ====================

    @Nested
    @DisplayName("cancelOrder()")
    class CancelOrder {

        @Test
        @DisplayName("✅ Doit annuler une commande PENDING")
        void shouldCancelPendingOrder() {
            // Given
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.save(order)).willReturn(order);

            //OrderDTO cancelledDTO = OrderDTO.builder()
             //       .id(orderId).status(OrderStatus.CANCELLED).build();
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            OrderDTO result = orderService.cancelOrder(orderId);

            // Then
            assertThat(result).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            then(orderRepository).should().save(order);
        }

        @Test
        @DisplayName("✅ Doit annuler une commande CONFIRMED")
        void shouldCancelConfirmedOrder() {
            // Given
            order.setStatus(OrderStatus.CONFIRMED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.save(order)).willReturn(order);
            given(orderMapper.toDTO(order)).willReturn(orderDTO);
            given(userServiceClient.getUserById(anyString()))
                    .willReturn(ApiResponse.success(buildUserDTO()));

            // When
            assertThatCode(() -> orderService.cancelOrder(orderId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande est SHIPPED (non annulable)")
        void shouldFailWhenOrderIsShipped() {
            // Given
            order.setStatus(OrderStatus.SHIPPED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);

            // When / Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("annulée");
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande est DELIVERED")
        void shouldFailWhenOrderIsDelivered() {
            // Given
            order.setStatus(OrderStatus.DELIVERED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);

            // When / Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== deleteOrder ====================

    @Nested
    @DisplayName("deleteOrder()")
    class DeleteOrder {

        @Test
        @DisplayName("✅ ADMIN peut supprimer une commande CANCELLED")
        void adminShouldDeleteCancelledOrder() {
            // Given
            order.setStatus(OrderStatus.CANCELLED);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // When
            orderService.deleteOrder(orderId);

            // Then
            then(orderRepository).should().delete(order);
        }

        @Test
        @DisplayName("❌ Doit échouer pour un USER non ADMIN")
        void shouldFailForNonAdmin() {
            // Given
            willThrow(new BusinessException("droits d'administrateur"))
                    .given(securityContextUtil).requireAdmin();

            // When / Then
            assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                    .isInstanceOf(BusinessException.class);
            then(orderRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("❌ Doit échouer si la commande n'est pas CANCELLED")
        void shouldFailWhenOrderIsNotCancelled() {
            // Given : commande PENDING
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // When / Then
            assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("annulées");
            then(orderRepository).should(never()).delete(any());
        }
    }

    // ==================== Statistiques ====================

    @Nested
    @DisplayName("Statistiques")
    class Statistics {

        @Test
        @DisplayName("✅ Doit compter les commandes d'un utilisateur")
        void shouldCountUserOrders() {
            // Given
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.countByUserId(userId)).willReturn(4L);

            // When
            long count = orderService.countUserOrders(userId);

            // Then
            assertThat(count).isEqualTo(4L);
        }

        @Test
        @DisplayName("✅ Doit calculer le montant total d'un utilisateur")
        void shouldCalculateTotalAmount() {
            // Given
            BigDecimal expected = new BigDecimal("1299.97");
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);
            given(orderRepository.sumTotalAmountByUserId(userId)).willReturn(expected);

            // When
            BigDecimal result = orderService.calculateUserTotalAmount(userId);

            // Then
            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("❌ Doit refuser les statistiques d'un autre utilisateur")
        void shouldDenyStatsForOtherUser() {
            // Given
            UUID otherUserId = UUID.randomUUID();
            given(securityContextUtil.isAdmin()).willReturn(false);
            given(securityContextUtil.getCurrentUserId()).willReturn(userId);

            // When / Then
            assertThatThrownBy(() -> orderService.countUserOrders(otherUserId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("autorisé");
        }

        @Test
        @DisplayName("✅ ADMIN peut voir les statistiques de n'importe quel utilisateur")
        void adminShouldAccessAnyUserStats() {
            // Given
            UUID otherUserId = UUID.randomUUID();
            given(securityContextUtil.isAdmin()).willReturn(true);
            given(orderRepository.countByUserId(otherUserId)).willReturn(2L);

            // When
            long count = orderService.countUserOrders(otherUserId);

            // Then
            assertThat(count).isEqualTo(2L);
        }
    }

    // ==================== generateOrderNumber ====================

    @Nested
    @DisplayName("generateOrderNumber()")
    class GenerateOrderNumber {

        @Test
        @DisplayName("✅ Doit générer un numéro au bon format ORD-YYYYMMDD-XXXXXX")
        void shouldGenerateCorrectFormat() {
            // Given
            given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);

            // When
            String number = orderService.generateOrderNumber();

            // Then
            assertThat(number).matches("ORD-\\d{8}-\\d{6}");
        }

        @Test
        @DisplayName("❌ Doit lever une exception après 10 tentatives sans numéro unique")
        void shouldFailAfterMaxAttempts() {
            // Given : toujours en collision
            given(orderRepository.existsByOrderNumber(anyString())).willReturn(true);

            // When / Then
            assertThatThrownBy(() -> orderService.generateOrderNumber())
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("tentatives");
        }
    }

    // ==================== enrichWithUserInfo ====================

    @Nested
    @DisplayName("enrichWithUserInfo()")
    class EnrichWithUserInfo {

        @Test
        @DisplayName("✅ Doit enrichir le DTO avec les infos utilisateur")
        void shouldEnrichWithUserInfo() {
            // Given
            UserDTO userDTO = buildUserDTO();
            given(userServiceClient.getUserById(userId.toString()))
                    .willReturn(ApiResponse.success(userDTO));

            // When
            OrderDTO enriched = orderService.enrichWithUserInfo(orderDTO);

            // Then
            assertThat(enriched.getUserEmail()).isEqualTo("john.doe@example.com");
            assertThat(enriched.getUserName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("✅ Doit retourner le DTO intact si User Service échoue")
        void shouldReturnDTOIntactWhenUserServiceFails() {
            // Given
            given(userServiceClient.getUserById(anyString()))
                    .willThrow(new RuntimeException("Service unavailable"));

            // When
            OrderDTO result = orderService.enrichWithUserInfo(orderDTO);

            // Then : pas d'exception levée, DTO retourné sans infos user
            assertThat(result).isNotNull();
            assertThat(result.getUserEmail()).isNull();
        }

        @Test
        @DisplayName("✅ Doit retourner null si le DTO est null")
        void shouldReturnNullForNullDTO() {
            // When
            OrderDTO result = orderService.enrichWithUserInfo(null);

            // Then
            assertThat(result).isNull();
            then(userServiceClient).should(never()).getUserById(any());
        }
    }

    // ==================== Recherches par dates ====================

    @Nested
    @DisplayName("Recherches par dates (ADMIN)")
    class DateSearches {

        @Test
        @DisplayName("✅ ADMIN peut rechercher des commandes après une date")
        void adminShouldSearchAfterDate() {
            // Given
            LocalDateTime date = LocalDateTime.now().minusDays(7);
            given(orderRepository.findByCreatedAtAfter(date)).willReturn(List.of(order));
            given(orderMapper.toDTOList(List.of(order))).willReturn(List.of(orderDTO));

            // When
            List<OrderDTO> results = orderService.getOrdersCreatedAfter(date);

            // Then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("✅ ADMIN peut rechercher des commandes entre deux dates")
        void adminShouldSearchBetweenDates() {
            // Given
            LocalDateTime start = LocalDateTime.now().minusDays(30);
            LocalDateTime end   = LocalDateTime.now();
            given(orderRepository.findByCreatedAtBetween(start, end))
                    .willReturn(List.of(order));
            given(orderMapper.toDTOList(List.of(order))).willReturn(List.of(orderDTO));

            // When
            List<OrderDTO> results = orderService.getOrdersBetweenDates(start, end);

            // Then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("❌ USER ne peut pas rechercher toutes les commandes après une date")
        void userShouldNotSearchAfterDate() {
            // Given
            willThrow(new BusinessException("administrateur"))
                    .given(securityContextUtil).requireAdmin();

            // When / Then
            assertThatThrownBy(() -> orderService.getOrdersCreatedAfter(LocalDateTime.now()))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== Helpers ====================

    private UserDTO buildUserDTO() {
        return UserDTO.builder()
                .id(userId)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .build();
    }
}