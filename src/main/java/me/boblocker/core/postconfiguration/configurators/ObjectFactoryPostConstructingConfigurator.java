package me.boblocker.core.postconfiguration.configurators;

import me.boblocker.core.WebApplicationContext;

public interface ObjectFactoryPostConstructingConfigurator {
    void configure(WebApplicationContext context);
}
