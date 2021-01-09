package me.boblocker.core.factory.configurators;

import me.boblocker.core.WebApplicationContext;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.core.exception.InjectValueAnnotationException;

import java.lang.reflect.Field;

public class InjectByTypeAnnotationObjectConfigurator implements ObjectConfigurator {
    @Override
    public void configure(Object obj, WebApplicationContext context) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectByType.class)) {
                tryInject(obj, context, field);
            }
        }
    }

    private void tryInject(Object obj, WebApplicationContext context, Field field) {
        try {
            field.setAccessible(true);
            Object value = context.getObject(field.getType());
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new InjectValueAnnotationException(
                    String.format("Error with inject field %s in object %s", field.getName(), obj.getClass()),
                    e);
        }
    }
}
