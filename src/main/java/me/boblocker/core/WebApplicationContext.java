package me.boblocker.core;

import lombok.Getter;
import lombok.Setter;
import me.boblocker.api.WebServer;
import me.boblocker.core.annotation.Singleton;
import me.boblocker.core.config.Config;
import me.boblocker.core.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplicationContext {
    @Setter
    private ObjectFactory objectFactory;
    private final Map<Class, Object> cache = new ConcurrentHashMap<>();
    @Setter
    private Map<Class, Object> mockObject = new HashMap<>();
    @Getter
    private final Config config;

    public WebApplicationContext(Config config) {
        this.config = config;
    }

    public <T> T getObject(Class<T> type) {
        Class<? extends T> implClass = type;
        if (type.isInterface()) {
            implClass = config.getImplClass(type);
        }
        if (mockObject.containsKey(implClass)) {
            return (T) mockObject.get(implClass);
        }

        if (cache.containsKey(implClass)) {
            return (T) cache.get(implClass);
        }


        T t = objectFactory.createObject(implClass);

        if (implClass.isAnnotationPresent(Singleton.class)) {
            cache.put(implClass, t);
        }

        return t;
    }

    public void registerAllSingletons() {
        Set<Class<?>> singletonClasses = config.getScanner().getTypesAnnotatedWith(Singleton.class);
        for (Class<?> singletonClass : singletonClasses) {
            cache.put(singletonClass, getObject(singletonClass));
        }
    }

    public void startWebServer() {
        WebServer webServer = getObject(WebServer.class);
        webServer.start();
    }
}
