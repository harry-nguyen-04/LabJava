package vhuwng.pricing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PricingLineRequest(
        @NotNull @Min(1) Integer lineNo,
        @NotNull @Min(1) Long quantity,
        @NotNull @Min(0) Long unitPrice
) {
}
