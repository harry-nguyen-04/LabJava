package vhuwng.lab.D2.dto;

public record SumOfTotalByStatus(
        long DRAFT,
        long PENDING_PAYMENT,
        long PAID,
        long FULFILLED,
        long CANCELLED
) {

}
