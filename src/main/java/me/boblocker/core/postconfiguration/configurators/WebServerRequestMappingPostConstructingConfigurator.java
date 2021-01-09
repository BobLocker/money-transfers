package me.boblocker.core.postconfiguration.configurators;

import io.javalin.plugin.openapi.annotations.HttpMethod;
import me.boblocker.api.WebServer;
import me.boblocker.core.WebApplicationContext;
import me.boblocker.core.annotation.RequestMapping;
import me.boblocker.core.annotation.RestController;
import me.boblocker.core.exception.WebServerException;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class WebServerRequestMappingPostConstructingConfigurator implements ObjectFactoryPostConstructingConfigurator {

    @Override
    public void configure(WebApplicationContext context) {
        WebServer webServer = context.getObject(WebServer.class);
        Reflections scanner = context.getConfig().getScanner();

        Set<Class<?>> typesAnnotatedWith = scanner.getTypesAnnotatedWith(RestController.class);

        for (Class<?> aClass : typesAnnotatedWith) {
            Object t = context.getObject(aClass);
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                RequestMapping annotation = declaredMethod.getAnnotation(RequestMapping.class);
                if (annotation != null) {
                    HttpMethod method = annotation.method();
                    String path = annotation.path();
                    switch (method) {
                        case GET:
                            webServer.getWebServer().get(path, ctx -> {
                                try {
                                    declaredMethod.invoke(t, ctx);
                                } catch (InvocationTargetException e) {
                                    Throwable cause = e.getCause();
                                    throw new WebServerException(cause.getMessage(), cause);
                                }
                            });
                            break;
                        case POST:
                            webServer.getWebServer().post(path, ctx -> {
                                try {
                                    declaredMethod.invoke(t, ctx);
                                } catch (InvocationTargetException e) {
                                    Throwable cause = e.getCause();
                                    throw new WebServerException(cause.getMessage(), cause);
                                }
                            });
                            break;
                        case DELETE:
                            webServer.getWebServer().delete(path, ctx -> {
                                try {
                                    declaredMethod.invoke(t, ctx);
                                } catch (InvocationTargetException e) {
                                    Throwable cause = e.getCause();
                                    throw new WebServerException(cause.getMessage(), cause);
                                }
                            });
                            break;
                    }
                }
            }
        }
    }
}
