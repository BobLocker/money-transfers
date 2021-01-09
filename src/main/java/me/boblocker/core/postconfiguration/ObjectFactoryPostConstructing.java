package me.boblocker.core.postconfiguration;

import lombok.SneakyThrows;
import me.boblocker.core.WebApplicationContext;
import me.boblocker.core.postconfiguration.configurators.ObjectFactoryPostConstructingConfigurator;

import java.util.ArrayList;
import java.util.List;

public class ObjectFactoryPostConstructing {

    private final WebApplicationContext context;
    private final List<ObjectFactoryPostConstructingConfigurator> configurators = new ArrayList<>();

    @SneakyThrows
    public ObjectFactoryPostConstructing(WebApplicationContext context) {
        this.context = context;
        for (Class<? extends ObjectFactoryPostConstructingConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectFactoryPostConstructingConfigurator.class)) {
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
    }

    public void configure() {
        for (ObjectFactoryPostConstructingConfigurator configurator : configurators) {
            configurator.configure(context);
        }
    }
}
