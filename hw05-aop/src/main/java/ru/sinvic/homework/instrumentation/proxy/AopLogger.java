package ru.sinvic.homework.instrumentation.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class AopLogger {

    private static final Logger logger = LoggerFactory.getLogger(AopLogger.class);

    @RuntimeType
    public static Object log(@Origin Method m, @SuperCall Callable<?> c, @AllArguments Object[] arguments)
            throws Exception {
        logger.info("Call to {} with arguments {}", m.getName(), arguments);
        return c.call();
    }
}
