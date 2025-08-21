package ru.sinvic.appcontainer.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.ComponentProcessingQueue;
import ru.sinvic.appcontainer.model.ComponentProcessingQueueImpl;
import ru.sinvic.appcontainer.model.ParsedConfig;

public class ConfigParser {

    public static ParsedConfig parseConfig(Class<?> configClass) {
        checkConfig(configClass);
        ParsedConfig parsedConfig = new ParsedConfig(configClass);
        ComponentProcessingQueue components = new ComponentProcessingQueueImpl();
        for (Method method : configClass.getDeclaredMethods()) {
            ComponentExecutionMetadata componentExecutionMetadata = parseConfigMethod(method);
            if (componentExecutionMetadata != null) {
                parsedConfig.add(componentExecutionMetadata);
            }
        }
        return parsedConfig;
    }

    private static ComponentExecutionMetadata parseConfigMethod(Method configMethod) {
        if (configMethod.isAnnotationPresent(AppComponent.class)) {
            AppComponent annotation = configMethod.getAnnotation(AppComponent.class);
            String componentName = annotation.name();
            return new ComponentExecutionMetadata(annotation.order(), componentName, configMethod);
        }
        return null;
    }

    private static void checkConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        checkUniqueComponent(configClass);
    }

    private static void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private static void checkUniqueComponent(Class<?> configClass) {
        Set<String> componentNames = new HashSet<>();
        for (Method method : configClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String componentName = annotation.name();
                if (componentNames.contains(componentName)) {
                    throw new RuntimeException(
                            String.format("Two components with the same name %s are prohibited!", componentName));
                }
                componentNames.add(componentName);
            }
        }
    }
}
