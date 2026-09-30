package vhuwng.pricing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PricingQuoteRequest(
        @NotNull @Min(0) @Max(100) Integer discountPercent,
        @NotNull @Min(0) @Max(100) Integer taxPercent,
        @NotNull List<@Valid PricingLineRequest> lines
) {
}
