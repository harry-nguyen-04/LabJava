package vhuwng.order.dto.response;

import vhuwng.order.dto.request.PricingQuoteRequest;

import java.util.List;

public record PricingQuoteResponse(
        Long subtotal,
        Long discountAmount,
        Long taxAmount,
        Long total,
        List<PricingLineResponse> lines
) {

    public static PricingQuoteResponse pending(PricingQuoteRequest request) {
        return new PricingQuoteResponse(
                null,
                null,
                null,
                null,
                request.lines().stream()
                        .map(line -> new PricingLineResponse(line.lineNo(), null))
                        .toList()
        );
    }
}
