package me.boblocker.exception.handlers;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.boblocker.core.annotation.ExceptionHandler;
import me.boblocker.core.postconfiguration.configurators.WebServerExceptionHandler;
import me.boblocker.exception.AccountNotFoundException;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

@ExceptionHandler(exceptionType = AccountNotFoundException.class)
@Slf4j
public class AccountNotFoundExceptionHandler implements WebServerExceptionHandler<AccountNotFoundException> {
    @Override
    public void handle(AccountNotFoundException exception, Context context) {
        log.error("Error with method - {}, path - {}, body - {}", context.method(), context.path(), context.body());
        context.status(HTTP_NOT_FOUND);
        context.result(exception.getMessage());
    }
}
