package vhuwng.order.dto.request;

import java.util.List;

public record PricingQuoteRequest(
        Integer discountPercent,
        Integer taxPercent,
        List<PricingLineRequest> lines
) {
}
