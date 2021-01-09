package me.boblocker.exception.handlers;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.boblocker.core.annotation.ExceptionHandler;
import me.boblocker.core.postconfiguration.configurators.WebServerExceptionHandler;
import me.boblocker.exception.TransferException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@ExceptionHandler(exceptionType = TransferException.class)
@Slf4j
public class TransferExceptionHandler implements WebServerExceptionHandler<TransferException> {
    @Override
    public void handle(TransferException exception, Context context) {
        log.error("Error with method - {}, path - {}, body - {}", context.method(), context.path(), context.body());
        context.status(HTTP_BAD_REQUEST);
        context.result(exception.getMessage());
    }
}
