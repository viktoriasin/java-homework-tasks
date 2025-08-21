package ru.sinvic.appcontainer.utils;

import java.util.*;
import ru.sinvic.appcontainer.model.ConfigsExecutionMetadata;
import ru.sinvic.appcontainer.model.ExecutionProcessingQueue;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ConfigExecutor {
    private final ExecutionProcessingQueue<ConfigsExecutionMetadata> configProcessingQueue =
            new ExecutionProcessingQueue<>(Comparator.comparing(ConfigsExecutionMetadata::configExecutionOrder));
    private final ParsedMetadata<ConfigsExecutionMetadata> parsedMetadata;

    public ConfigExecutor(ParsedMetadata<ConfigsExecutionMetadata> parsedMetadata) {
        this.parsedMetadata = parsedMetadata;
        for (ConfigsExecutionMetadata configsExecutionMetadata : parsedMetadata) {
            configProcessingQueue.addNewComponentForExecution(configsExecutionMetadata);
        }
    }

    public void processConfig() {
        while (configProcessingQueue.hasNextForExecution()) {
            ConfigsExecutionMetadata nextConfigForExecution = configProcessingQueue.getNextForExecution();
        }
    }
}
