package ru.sinvic.homework.instrumentation.proxy;

import static net.bytebuddy.matcher.ElementMatchers.*;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import ru.sinvic.homework.Log;

// java -javaagent:proxyDemo.jar -jar proxyDemo.jar
@SuppressWarnings("all")
class Agent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(named("ru.sinvic.homework.TestLoggingInterfaceImpl"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(
                            DynamicType.Builder<?> builder,
                            TypeDescription typeDescription,
                            ClassLoader classLoader,
                            JavaModule javaModule,
                            ProtectionDomain protectionDomain) {
                        return builder.method(isAnnotatedWith(Log.class))
                                .intercept(MethodDelegation.to(AopLogger.class));
                    }
                })
                .installOn(instrumentation);
    }
}
