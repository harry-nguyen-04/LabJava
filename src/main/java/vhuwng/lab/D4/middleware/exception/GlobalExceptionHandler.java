package vhuwng.lab.D4.middleware.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vhuwng.lab.D4.dto.response.ApiErrorResponse;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidStatus(
            InvalidStatusTransitionException ex
    ) {

        ApiErrorResponse error = new ApiErrorResponse(
                400,
                "INVALID_STATUS_TRANSITION",
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(HandleValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            HandleValidationException ex
    ) {
        ApiErrorResponse error = new ApiErrorResponse(
                400,
                "VALIDATION_ERROR",
                ex.getMessage()
        );
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ){
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        ApiErrorResponse error = new ApiErrorResponse(
                400,
                "VALIDATION_ERROR",
                message
        );
        return ResponseEntity
                .badRequest()
                .body(error);
    }
}