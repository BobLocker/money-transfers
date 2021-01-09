package me.boblocker.core.exception;

public class InjectValueAnnotationException extends InjectAnnotationException {
    public InjectValueAnnotationException(String message) {
        super(message);
    }

    public InjectValueAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
