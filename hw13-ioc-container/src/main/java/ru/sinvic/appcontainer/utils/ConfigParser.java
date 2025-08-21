package ru.sinvic.appcontainer.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.Metadata;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ConfigParser<T extends Metadata> {

    public ParsedMetadata<T> parseConfig(Class<?> configClass) {
        checkConfig(configClass);
        ParsedMetadata<T> parsedMetadata = new ParsedMetadata<>(configClass);
        for (Method method : configClass.getDeclaredMethods()) {
            ComponentExecutionMetadata componentExecutionMetadata = parseConfigMethod(method);
            if (componentExecutionMetadata != null) {
                parsedMetadata.add((T) componentExecutionMetadata);
            }
        }
        return parsedMetadata;
    }

    private ComponentExecutionMetadata parseConfigMethod(Method configMethod) {
        if (configMethod.isAnnotationPresent(AppComponent.class)) {
            AppComponent annotation = configMethod.getAnnotation(AppComponent.class);
            String componentName = annotation.name();
            return new ComponentExecutionMetadata(annotation.order(), componentName, configMethod);
        }
        return null;
    }

    private void checkConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        checkUniqueComponent(configClass);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void checkUniqueComponent(Class<?> configClass) {
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
