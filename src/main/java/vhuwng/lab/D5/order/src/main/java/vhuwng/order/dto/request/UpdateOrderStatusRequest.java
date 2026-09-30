package vhuwng.order.dto.request;

import jakarta.validation.constraints.NotNull;
import vhuwng.order.entity.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {
}
