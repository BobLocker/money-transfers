package me.boblocker.core.factory.configurators;

import me.boblocker.core.WebApplicationContext;

public interface ObjectConfigurator {
    void configure(Object obj, WebApplicationContext context);
}
