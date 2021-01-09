package me.boblocker.core.postconfiguration.configurators;

import io.javalin.http.Context;

public interface WebServerExceptionHandler<T extends Exception> {
    void handle(T exception, Context context);
}
