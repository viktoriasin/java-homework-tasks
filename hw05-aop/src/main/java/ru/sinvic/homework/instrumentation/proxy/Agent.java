package ru.sinvic.homework.instrumentation.proxy;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import ru.sinvic.homework.Log;

@SuppressWarnings("all")
class Agent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(named(
                        "ru.sinvic.homework.TestLoggingInterfaceImpl")) // ElementMatchers.named("TestLoggingInterface")
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(
                            DynamicType.Builder<?> builder,
                            TypeDescription typeDescription,
                            ClassLoader classLoader,
                            JavaModule javaModule,
                            ProtectionDomain protectionDomain) {
                        System.out.println("тип: " + typeDescription.getCanonicalName());

                        //                        ElementMatcher.Junction<MethodDescription> matcher = hasAnnotation(new
                        // InheritedAnnotationMatcher<>());
                        //                        return
                        // builder.method(matcher).intercept(MethodDelegation.to(Tmp.class));
                        return builder.method(isAnnotatedWith(Log.class))
                                .intercept(MethodDelegation.to(AopLogger.class));
                    }
                })
                .installOn(instrumentation);
    }
}
