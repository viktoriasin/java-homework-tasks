package ru.sinvic.homework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.homework.Log;
import ru.sinvic.homework.TestLoggingInterface;
import ru.sinvic.homework.TestLoggingInterfaceImpl;

public class IoC {
    private static final Logger logger = LoggerFactory.getLogger(IoC.class);

    private IoC() {}

    public static TestLoggingInterface createMyClass() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLoggingInterfaceImpl());
        return (TestLoggingInterface) Proxy.newProxyInstance(
                IoC.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface myClass;

        DemoInvocationHandler(TestLoggingInterface myClass) {
            this.myClass = myClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(Log.class)) {
                logger.atInfo()
                        .setMessage("executed method: calculation, param: {}")
                        .addArgument(() -> args)
                        .log();
            }

            return method.invoke(myClass, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "myClass=" + myClass + '}';
        }
    }
}
