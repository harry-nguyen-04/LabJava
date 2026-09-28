package vhuwng.lab.D4.middleware.exception;

public class InvalidStatusTransitionException extends RuntimeException{
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
