package vhuwng.lab.D4.dto.request;

import jakarta.validation.constraints.NotNull;
import vhuwng.lab.D4.common.OrderStatus;

public record UpdateOrderStatusRequestDto(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
