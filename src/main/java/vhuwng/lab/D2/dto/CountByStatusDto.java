package vhuwng.lab.D2.dto;

public record CountByStatusDto(
        int DRAFT,
        int PENDING_PAYMENT,
        int PAID,
        int FULFILLED,
        int CANCELLED
) {
}
