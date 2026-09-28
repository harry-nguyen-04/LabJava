package vhuwng.lab.D4.dto.response;

import vhuwng.lab.D4.common.OrderStatus;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public record OrderResponseDto(
        int id,
        String code,
        int customerId,
        String customerName,
        int createdBy,
        OrderStatus status,
        List<LineResponseDto> lines,
        long subtotal,
        int discountPercent,
        long discountAmount,
        int taxPercent,
        long taxAmount,
        long total,
        String currency,
        String createdAt,
        String updatedAt,
        String paidAt,
        String fulfilledAt,
        String cancelledAt
) {
    public OrderResponseDto cancelOrder() {
        if (status == OrderStatus.FULFILLED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be cancelled from status: " + status);
        }

        String now = OffsetDateTime.now(ZoneOffset.ofHours(7)).toString();

        return new OrderResponseDto(
                id,
                code,
                customerId,
                customerName,
                createdBy,
                OrderStatus.CANCELLED,
                lines,
                subtotal,
                discountPercent,
                discountAmount,
                taxPercent,
                taxAmount,
                total,
                currency,
                createdAt,
                now,
                paidAt,
                fulfilledAt,
                now
        );
    }
}
