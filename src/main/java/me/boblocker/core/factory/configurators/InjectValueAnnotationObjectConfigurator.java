package me.boblocker.core.factory.configurators;

import lombok.SneakyThrows;
import me.boblocker.core.WebApplicationContext;
import me.boblocker.core.annotation.InjectValue;
import me.boblocker.core.exception.InjectValueAnnotationException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InjectValueAnnotationObjectConfigurator implements ObjectConfigurator {
    private final static String APPLICATION_PROPERTIES = "application.properties";
    private final Map<String, String> properties;

    @SneakyThrows
    public InjectValueAnnotationObjectConfigurator() {
//        URL url = ClassLoader.getSystemClassLoader().getResource(APPLICATION_PROPERTIES);
//        Stream<String> lines = new BufferedReader(new FileReader(new File(url.toURI()))).lines();
        Stream<String> lines = new BufferedReader(new InputStreamReader(
                ClassLoader.getSystemClassLoader().getResourceAsStream(APPLICATION_PROPERTIES)
        )).lines();
        properties = lines
                .map(line -> line.split("="))
                .peek(array -> {
                    array[0] = array[0].strip();
                    array[1] = array[1].strip();
                })
                .collect(Collectors.toMap(array -> array[0], array -> array[1]));
    }

    @Override
    public void configure(Object obj, WebApplicationContext context) {
        Class<?> implClass = obj.getClass();
        for (Field field : implClass.getDeclaredFields()) {
            InjectValue annotation = field.getAnnotation(InjectValue.class);
            if (annotation != null) {
                String propertyName = annotation.value().isEmpty() ? field.getName() : annotation.value();
                String value = properties.get(propertyName);
                if (value == null) {
                    throw new InjectValueAnnotationException("Not found property: " + propertyName);
                }
                field.setAccessible(true);
                if (field.getType().equals(Integer.TYPE)) {
                    int intValue;
                    try {
                        intValue = Integer.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InjectValueAnnotationException("Error with parsing to integer value: " + value, ex);
                    }
                    tryInject(obj, field, intValue);
                } else {
                    tryInject(obj, field, value);
                }
            }
        }
    }

    private void tryInject(Object obj, Field field, Object value) {
        try {
            field.set(obj, value);
        } catch (Exception ex) {
            throw new InjectValueAnnotationException(
                    String.format("Error with inject value: %s to field %s", value, field.getName()),
                    ex);
        }
    }
}
