package ru.sinvic.homework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.homework.Log;

public class IoC {
    private static final Logger logger = LoggerFactory.getLogger(IoC.class);

    private IoC() {}

    public static Object createMyClass(Object objectToWrap, Class<?> interfaceClass) {
        InvocationHandler handler = new DemoInvocationHandler(objectToWrap);
        return Proxy.newProxyInstance(IoC.class.getClassLoader(), new Class<?>[] {interfaceClass}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final Object objectToWrap;

        DemoInvocationHandler(Object objectToWrap) {
            this.objectToWrap = objectToWrap;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(Log.class)) {
                logger.info(
                        "executed method: {} for class {}, param: {}",
                        method.getName(),
                        objectToWrap.getClass().getSimpleName(),
                        args);
            }
            return method.invoke(objectToWrap, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "myClass=" + objectToWrap + '}';
        }
    }
}
