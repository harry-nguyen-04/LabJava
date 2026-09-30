package vhuwng.order.dto.request;

public record PricingLineRequest(
        Integer lineNo,
        Long quantity,
        Long unitPrice
) {
}
