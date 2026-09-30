package vhuwng.pricing.dto.response;

import java.util.List;

public record PricingQuoteResponse(
        Long subtotal,
        Long discountAmount,
        Long taxAmount,
        Long total,
        List<PricingLineResponse> lines
) {
}
