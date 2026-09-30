package vhuwng.pricing.dto.response;

public record ApiErrorResponse(
        int status,
        String code,
        String message
) {
}
