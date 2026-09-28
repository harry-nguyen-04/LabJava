package vhuwng.lab.D4.dto.response;

import vhuwng.lab.D4.common.OrderStatus;

import java.util.List;

public record CreateOrderResponseDto(
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
}
