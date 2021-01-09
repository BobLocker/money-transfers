package me.boblocker.core.exception;

public class WebServerException extends RuntimeException {

    public WebServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServerException(Throwable cause) {
        super(cause);
    }
}
