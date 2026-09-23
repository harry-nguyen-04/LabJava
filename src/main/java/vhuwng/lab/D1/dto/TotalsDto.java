package vhuwng.lab.D1.dto;

public record TotalsDto(
        int orderCount,
        long revenue,
        int revenueOrderCount,
        String firstOrderDate,
        String lastOrderDate) {}
