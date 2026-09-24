package vhuwng.lab.D2.dto;

public record RevenueByCustomerDto(
        Integer customerId,
        String customerName,
        Integer orders,
        Long revenue) {}
