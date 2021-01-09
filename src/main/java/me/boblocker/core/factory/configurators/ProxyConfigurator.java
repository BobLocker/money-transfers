package me.boblocker.core.factory.configurators;

public interface ProxyConfigurator {
    Object replaceWithProxyIfNeeded(Object obj, Class implClass);
}
