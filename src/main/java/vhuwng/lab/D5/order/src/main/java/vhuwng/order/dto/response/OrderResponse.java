package vhuwng.order.dto.response;

import vhuwng.order.entity.OrderStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String code,
        Long customerId,
        String customerName,
        Long createdBy,
        OrderStatus status,
        List<OrderLineResponse> lines,
        PricingStatus pricingStatus,
        Long subtotal,
        Integer discountPercent,
        Long discountAmount,
        Integer taxPercent,
        Long taxAmount,
        Long total,
        String currency,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime paidAt,
        OffsetDateTime fulfilledAt,
        OffsetDateTime cancelledAt
) {
}
