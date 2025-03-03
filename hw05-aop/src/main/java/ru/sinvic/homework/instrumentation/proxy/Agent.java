package ru.sinvic.homework.instrumentation.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    private Agent() {}

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("premain");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {
                if (className.equals("ru/sinvic/homework/instrumentation/proxy/TestLoggingInterfaceImpl")) {
                    return addProxyMethod(classfileBuffer);
                }
                return classfileBuffer;
            }
        });
    }

    private static byte[] addProxyMethod(byte[] classfileBuffer) {
        return null;
    }
}
