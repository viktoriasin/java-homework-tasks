package ru.sinvic.appcontainer.utils;

import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.appcontainer.model.ConfigsExecutionMetadata;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ConfigsParser {

    public ParsedMetadata<ConfigsExecutionMetadata> parseConfigs(Class<?>... configClasses) {
        ParsedMetadata<ConfigsExecutionMetadata> metadata = new ParsedMetadata<>();
        for (Class<?> config : configClasses) {
            if (config.isAnnotationPresent(AppComponentsContainerConfig.class)) {
                AppComponentsContainerConfig annotation = config.getAnnotation(AppComponentsContainerConfig.class);
                metadata.add(new ConfigsExecutionMetadata(annotation.order(), config));
            }
        }
        return metadata;
    }
}
