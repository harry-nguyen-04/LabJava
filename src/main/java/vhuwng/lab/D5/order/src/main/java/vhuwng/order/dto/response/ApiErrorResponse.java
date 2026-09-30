package vhuwng.order.dto.response;

public record ApiErrorResponse(
        int status,
        String code,
        String message
) {
}
