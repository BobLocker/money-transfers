package me.boblocker.core;

import me.boblocker.core.config.JavaConfig;
import me.boblocker.core.factory.ObjectFactory;
import me.boblocker.core.postconfiguration.ObjectFactoryPostConstructing;

import java.util.HashMap;
import java.util.Map;

public class ApplicationRunner {

    public static <T> WebApplicationContext run(Class<T> baseClassToScan) {
        return run(baseClassToScan.getPackageName());
    }

    public static WebApplicationContext run(String packageToScan){
       return run(packageToScan, new HashMap<>());
    }

    public static WebApplicationContext run(String packageToScan, Map<Class, Class> ifcToImplClass){
        JavaConfig config = new JavaConfig(packageToScan, ifcToImplClass);
        WebApplicationContext context = new WebApplicationContext(config);
        ObjectFactory objectFactory = new ObjectFactory(context);
        context.setObjectFactory(objectFactory);

        context.registerAllSingletons();

        ObjectFactoryPostConstructing objectFactoryPostConstructing = new ObjectFactoryPostConstructing(context);
        objectFactoryPostConstructing.configure();

        context.startWebServer();

        return context;
    }
}
