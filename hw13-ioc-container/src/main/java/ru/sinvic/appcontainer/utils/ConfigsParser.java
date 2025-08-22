package ru.sinvic.appcontainer.utils;

import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.appcontainer.model.ConfigsExecutionMetadata;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ConfigsParser {

    private static final Class<AppComponentsContainerConfig> CONFIG_CLASS_ANNOTATION =
            AppComponentsContainerConfig.class;

    public static ParsedMetadata<ConfigsExecutionMetadata> parseConfigs(Class<?>... configClasses) {
        ParsedMetadata<ConfigsExecutionMetadata> metadata = new ParsedMetadata<>();
        for (Class<?> config : configClasses) {
            if (config.isAnnotationPresent(CONFIG_CLASS_ANNOTATION)) {
                AppComponentsContainerConfig annotation = config.getAnnotation(CONFIG_CLASS_ANNOTATION);
                metadata.add(new ConfigsExecutionMetadata(annotation.order(), config));
            }
        }
        return metadata;
    }

    public static ParsedMetadata<ConfigsExecutionMetadata> parseConfigs(String packageName) {
        Reflections reflections = new Reflections(packageName, new TypeAnnotationsScanner());
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(CONFIG_CLASS_ANNOTATION);
        return parseConfigs(typesAnnotatedWith.toArray(new Class<?>[0]));
    }
}
