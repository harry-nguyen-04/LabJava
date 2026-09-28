package vhuwng.lab.D4.dto.response;

public record ApiErrorResponse(
        int status,
        String code,
        String message
) {

}
