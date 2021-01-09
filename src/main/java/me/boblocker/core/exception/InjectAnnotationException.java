package me.boblocker.core.exception;

public class InjectAnnotationException extends RuntimeException {
    public InjectAnnotationException(String message) {
        super(message);
    }

    public InjectAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
