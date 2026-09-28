package vhuwng.lab.D4.dto.response;

public record LineResponseDto(
        int lineNo,
        int productId,
        String sku,
        String productName,
        int quantity,
        long unitPrice,
        long lineTotal
) {
}
