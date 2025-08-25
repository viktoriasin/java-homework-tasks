package ru.sinvic.appcontainer.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.ConfigMethodMetadata;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ConfigParser {

    public static ParsedMetadata<ComponentExecutionMetadata> parseConfig(
            Class<?> configClass, Integer configExecutionOrder) {
        checkConfig(configClass);
        ParsedMetadata<ComponentExecutionMetadata> parsedMetadata = new ParsedMetadata<>();
        Object configObject = getConfigObject(configClass);
        for (Method method : configClass.getDeclaredMethods()) {
            ConfigMethodMetadata componentExecutionMetadata = parseConfigMethod(method);
            if (componentExecutionMetadata != null) {
                ComponentExecutionMetadata executionMetadata = new ComponentExecutionMetadata(
                        componentExecutionMetadata.componentExecutionOrder() + configExecutionOrder,
                        componentExecutionMetadata.componentName(),
                        componentExecutionMetadata.configMethod(),
                        configObject);
                parsedMetadata.add(executionMetadata);
            }
        }
        return parsedMetadata;
    }

    private static Object getConfigObject(Class<?> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new RuntimeException("Error creating config object", e);
        }
    }

    private static ConfigMethodMetadata parseConfigMethod(Method configMethod) {
        if (configMethod.isAnnotationPresent(AppComponent.class)) {
            AppComponent annotation = configMethod.getAnnotation(AppComponent.class);
            String componentName = annotation.name();
            return new ConfigMethodMetadata(annotation.order(), componentName, configMethod);
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
