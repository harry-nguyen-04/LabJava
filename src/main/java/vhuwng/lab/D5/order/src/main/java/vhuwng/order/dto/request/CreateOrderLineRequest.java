package vhuwng.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderLineRequest(
        @NotNull @Min(1) Integer lineNo,
        @NotNull @Min(1) Long productId,
        @NotBlank String sku,
        @NotBlank String productName,
        @NotNull @Min(1) Long quantity,
        @NotNull @Min(0) Long unitPrice
) {
}
