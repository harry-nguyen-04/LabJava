package vhuwng.lab.D3.dto;

import java.util.List;

public record OrderDetailDto(
        int id,
        String code,
        int customerId,
        String customerName,
        int createdBy,
        String status,
        List<LineDto> lines,
        long subtotal,
        int discountPercent,
        long discountAmount,
        int taxPercent,
        long taxAmount,
        long total,
        String currency,
        String createdAt,
        String updatedAt,
        String paidAt,
        String fulfilledAt,
        String cancelledAt
) {}
