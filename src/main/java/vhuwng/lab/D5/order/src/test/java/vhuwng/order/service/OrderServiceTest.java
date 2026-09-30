package vhuwng.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vhuwng.order.client.PricingClient;
import vhuwng.order.dto.request.UpdateOrderStatusRequest;
import vhuwng.order.dto.response.OrderResponse;
import vhuwng.order.dto.response.PricingLineResponse;
import vhuwng.order.dto.response.PricingQuoteResponse;
import vhuwng.order.dto.response.PricingStatus;
import vhuwng.order.entity.OrderEntity;
import vhuwng.order.entity.OrderLineEntity;
import vhuwng.order.entity.OrderStatus;
import vhuwng.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PricingClient pricingClient;

    private OrderService orderService;
    private OrderEntity order;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, pricingClient);
        order = sampleOrder();
    }

    @Test
    void mapsReadyPricingIntoOrderResponse() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(pricingClient.quote(any())).thenReturn(new PricingQuoteResponse(
                20_000L,
                2_000L,
                1_440L,
                19_440L,
                List.of(new PricingLineResponse(1, 20_000L))
        ));

        OrderResponse result = orderService.getAllOrders().getFirst();

        assertThat(result.pricingStatus()).isEqualTo(PricingStatus.READY);
        assertThat(result.subtotal()).isEqualTo(20_000L);
        assertThat(result.discountAmount()).isEqualTo(2_000L);
        assertThat(result.taxAmount()).isEqualTo(1_440L);
        assertThat(result.total()).isEqualTo(19_440L);
        assertThat(result.lines().getFirst().lineTotal()).isEqualTo(20_000L);
    }

    @Test
    void connectionFailureReturnsPendingInsteadOfThrowing() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(pricingClient.quote(any())).thenThrow(new RuntimeException("connection refused"));

        OrderResponse result = orderService.getAllOrders().getFirst();

        assertThat(result.pricingStatus()).isEqualTo(PricingStatus.PENDING);
        assertThat(result.total()).isNull();
        assertThat(result.lines().getFirst().lineTotal()).isNull();
    }

    @Test
    void timeoutReturnsPendingInsteadOfThrowing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(pricingClient.quote(any())).thenThrow(new RuntimeException("timeout"));

        OrderResponse result = orderService.getOrderById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.pricingStatus()).isEqualTo(PricingStatus.PENDING);
        assertThat(result.subtotal()).isNull();
        assertThat(result.total()).isNull();
    }

    @Test
    void statusUpdatePersistsOrderWithoutCallingPricing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse result = orderService.updateStatus(
                1L,
                new UpdateOrderStatusRequest(OrderStatus.PAID)
        );

        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).save(order);
    }

    @Test
    void pricingReturningNullIsAlsoHandledAsPending() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(pricingClient.quote(any())).thenReturn(null);

        OrderResponse result = orderService.getAllOrders().getFirst();

        assertThat(result.pricingStatus()).isEqualTo(PricingStatus.PENDING);
        assertThat(result.total()).isNull();
    }

    @Test
    void missingOrderIsRejectedWithoutCallingPricing() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found: 99");
        verifyNoInteractions(pricingClient);
    }

    @Test
    void cancelPersistsCancelledStatusWithoutCallingPricing() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse result = orderService.cancelOrder(1L);

        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(order);
        verifyNoInteractions(pricingClient);
    }

    private OrderEntity sampleOrder() {
        OffsetDateTime now = OffsetDateTime.parse("2026-09-29T10:00:00+07:00");
        OrderEntity sample = new OrderEntity(
                1L,
                "ORD-001",
                10L,
                "An",
                20L,
                OrderStatus.PAID,
                BigDecimal.TEN,
                BigDecimal.valueOf(8),
                "VND",
                now,
                now,
                now,
                null,
                null
        );
        sample.addLine(new OrderLineEntity(
                1L,
                1,
                100L,
                "SKU-1",
                "Sample product",
                2,
                10_000L
        ));
        return sample;
    }
}
