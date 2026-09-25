package vhuwng.lab.D3.dto;

public record ShipmentResponseDto(
        int id,
        int orderId,
        String orderCode,
        String carrier,
        String trackingNumber,
        String status,
        String estimatedDelivery,
        String lastUpdated
) {
}
