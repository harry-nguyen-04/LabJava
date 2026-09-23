package vhuwng.lab.D1.dto;

public record RevenueByCustomerDto(
        Integer customerId,
        String customerName,
        Integer orders,
        Long revenue) {}
