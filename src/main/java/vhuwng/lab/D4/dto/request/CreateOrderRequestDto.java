package vhuwng.lab.D4.dto.request;

import jakarta.validation.constraints.NotNull;
import vhuwng.lab.D4.common.OrderStatus;
import vhuwng.lab.D4.dto.response.LineResponseDto;

import java.util.List;

public record CreateOrderRequestDto(
        @NotNull(message = "Code is required")
        String code,

        @NotNull(message = "Customer ID is required")
        int customerId,

        String customerName,

        @NotNull(message = "Created by is required")
        int createdBy,

        @NotNull(message = "Status is required")
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
