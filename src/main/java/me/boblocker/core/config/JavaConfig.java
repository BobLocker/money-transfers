package me.boblocker.core.config;

import lombok.Getter;
import me.boblocker.core.exception.ConfigException;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

public class JavaConfig implements Config {
    @Getter
    private final Reflections scanner;
    private final Map<Class, Class> ifcToImplClass;

    public JavaConfig(String packageToScan, Map<Class, Class> ifcToImplClass) {
        this.scanner = new Reflections(packageToScan);
        this.ifcToImplClass = ifcToImplClass;
    }

    @Override
    public <T> Class<? extends T> getImplClass(Class<T> ifc) {
        return ifcToImplClass.computeIfAbsent(ifc, aClass -> {
            Set<Class<? extends T>> classes = scanner.getSubTypesOf(ifc);

            if (classes.size() != 1)
                throw new ConfigException("Zero or more then one implementation of interface " + ifc.getName());

            return classes.iterator().next();
        });
    }
}
