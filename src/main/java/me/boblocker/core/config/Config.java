package me.boblocker.core.config;

import org.reflections.Reflections;

public interface Config {
    <T> Class<? extends T> getImplClass(Class<T> interfaceType);

    Reflections getScanner();
}
