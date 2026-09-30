package vhuwng.order.dto.response;

public record OrderLineResponse(
        Integer lineNo,
        Long productId,
        String sku,
        String productName,
        Long quantity,
        Long unitPrice,
        Long lineTotal
) {
}
