package me.boblocker.exception.handlers;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.boblocker.core.annotation.ExceptionHandler;
import me.boblocker.core.postconfiguration.configurators.WebServerExceptionHandler;
import me.boblocker.exception.AccountException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@ExceptionHandler(exceptionType = AccountException.class)
@Slf4j
public class AccountExceptionHandler implements WebServerExceptionHandler<AccountException> {
    @Override
    public void handle(AccountException exception, Context context) {
        log.error("Error with method - {}, path - {}, body - {}", context.method(), context.path(), context.body());
        context.status(HTTP_BAD_REQUEST);
        context.result(exception.getMessage());
    }
}
