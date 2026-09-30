package vhuwng.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import vhuwng.order.entity.OrderStatus;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String code,
        @NotNull @Min(1) Long customerId,
        @NotBlank String customerName,
        @NotNull @Min(1) Long createdBy,
        @NotNull OrderStatus status,
        @NotNull @Min(0) @Max(100) Integer discountPercent,
        @NotNull @Min(0) @Max(100) Integer taxPercent,
        @NotBlank String currency,
        @NotEmpty List<@Valid CreateOrderLineRequest> lines
) {
}
