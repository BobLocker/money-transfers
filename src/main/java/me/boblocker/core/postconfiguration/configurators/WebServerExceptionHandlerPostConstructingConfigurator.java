package me.boblocker.core.postconfiguration.configurators;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import me.boblocker.api.WebServer;
import me.boblocker.core.WebApplicationContext;
import me.boblocker.core.annotation.ExceptionHandler;
import me.boblocker.core.exception.WebServerException;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@Slf4j
public class WebServerExceptionHandlerPostConstructingConfigurator implements ObjectFactoryPostConstructingConfigurator {
    private final String defaultExceptionMessage = "Internal sever error. Fore more information see server log.";
    private final int defaultExceptionErrorCode = HTTP_INTERNAL_ERROR;
    private Map<Class, WebServerExceptionHandler> exceptionHandlers = new HashMap<>();

    @Override
    public void configure(WebApplicationContext context) {
        WebServer webServer = context.getObject(WebServer.class);
        Reflections scanner = context.getConfig().getScanner();

        Set<Class<? extends WebServerExceptionHandler>> subTypesOf = scanner.getSubTypesOf(WebServerExceptionHandler.class);
        for (Class<? extends WebServerExceptionHandler> aClass : subTypesOf) {
            ExceptionHandler annotation = aClass.getAnnotation(ExceptionHandler.class);
            if (annotation != null) {
                WebServerExceptionHandler object = context.getObject(aClass);
                Class exceptionType = annotation.exceptionType();
                exceptionHandlers.put(exceptionType, object);
            }
        }


        Javalin javalin = webServer.getWebServer();
        javalin.exception(WebServerException.class, (e, ctx) -> {
            log.error(e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null && exceptionHandlers.containsKey(cause.getClass())) {
                exceptionHandlers.get(cause.getClass()).handle( (Exception) cause, ctx);
            } else {
                ctx.status(defaultExceptionErrorCode);
                ctx.result(defaultExceptionMessage);
            }
        });
    }
}
