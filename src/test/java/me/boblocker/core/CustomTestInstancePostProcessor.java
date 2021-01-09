package me.boblocker.core;

import me.boblocker.core.annotation.ContextForTest;
import me.boblocker.core.config.JavaConfig;
import me.boblocker.core.factory.ObjectFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CustomTestInstancePostProcessor implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object obj, ExtensionContext extensionContext) throws Exception {

        ContextForTest annotation = obj.getClass().getAnnotation(ContextForTest.class);
        String packageToScan = annotation.value();

        Map<Class, Object> mockObject = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Mock.class)) {
                Class<?> filedType = field.getType();
                Object mock = Mockito.mock(filedType);
                mockObject.put(filedType, mock);
                field.setAccessible(true);
                field.set(obj, mock);
            }
        }

        JavaConfig config = new JavaConfig(packageToScan, new HashMap<>());
        WebApplicationContext context = new WebApplicationContext(config);
        context.setMockObject(mockObject);
        ObjectFactory objectFactory = new ObjectFactory(context);
        context.setObjectFactory(objectFactory);

        objectFactory.configure(obj);
    }
}
