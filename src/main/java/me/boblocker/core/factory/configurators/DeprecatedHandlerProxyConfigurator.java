package me.boblocker.core.factory.configurators;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DeprecatedHandlerProxyConfigurator implements ProxyConfigurator {
    @Override
    public Object replaceWithProxyIfNeeded(Object obj, Class implClass) {
        if (implClass.isAnnotationPresent(Deprecated.class)) {

            if (implClass.getInterfaces().length == 0) {
                return Enhancer.create(implClass, (net.sf.cglib.proxy.InvocationHandler) (proxy, method, args) -> getInvocationHandlerLogic(obj, method, args));
            }

            return Proxy.newProxyInstance(implClass.getClassLoader(), implClass.getInterfaces(), (proxy, method, args) -> getInvocationHandlerLogic(obj, method, args));
        } else {
            return obj;
        }
    }

    private Object getInvocationHandlerLogic(Object obj, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        System.out.println("******* DEPRECATED ********");
        return method.invoke(obj, args);
    }
}
