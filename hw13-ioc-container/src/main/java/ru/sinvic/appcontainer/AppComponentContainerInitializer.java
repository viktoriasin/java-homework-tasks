package ru.sinvic.appcontainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ru.sinvic.appcontainer.model.*;
import ru.sinvic.appcontainer.utils.ComponentInitializer;
import ru.sinvic.appcontainer.utils.ConfigParser;
import ru.sinvic.appcontainer.utils.ConfigsParser;

public class AppComponentContainerInitializer {
    private final ParsedMetadata<ConfigsExecutionMetadata> parsedConfigs;
    private final List<InitializedComponent> initializedComponents = new ArrayList<>();

    public AppComponentContainerInitializer(Class<?>... initialConfigClass) {
        parsedConfigs = ConfigsParser.parseConfigs(initialConfigClass);
    }

    public AppComponentContainerInitializer(String packageName) {
        parsedConfigs = ConfigsParser.parseConfigs(packageName);
    }

    public void initializeContainer() {
        ExecutionProcessingQueue<ConfigsExecutionMetadata> configsExecutionProcessingQueue =
                getConfigsExecutionQueue(parsedConfigs);
        processConfigsQueue(configsExecutionProcessingQueue);
    }

    public List<InitializedComponent> getInitializedComponent() {
        return initializedComponents;
    }

    private static ExecutionProcessingQueue<ConfigsExecutionMetadata> getConfigsExecutionQueue(
            ParsedMetadata<ConfigsExecutionMetadata> parsedConfigs) {
        ExecutionProcessingQueue<ConfigsExecutionMetadata> configsExecutionProcessingQueue =
                new ExecutionProcessingQueue<>(Comparator.comparing(ConfigsExecutionMetadata::configExecutionOrder));
        for (ConfigsExecutionMetadata configsExecutionMetadata : parsedConfigs) {
            configsExecutionProcessingQueue.addNewComponentForExecution(configsExecutionMetadata);
        }
        return configsExecutionProcessingQueue;
    }

    private void processConfigsQueue(
            ExecutionProcessingQueue<ConfigsExecutionMetadata> configsExecutionProcessingQueue) {
        List<ParsedMetadata<ComponentExecutionMetadata>> componentsMetadata = new ArrayList<>();
        while (configsExecutionProcessingQueue.hasNextForExecution()) {
            ConfigsExecutionMetadata nextConfigForExecution = configsExecutionProcessingQueue.getNextForExecution();
            ParsedMetadata<ComponentExecutionMetadata> parsedComponentMetadata = ConfigParser.parseConfig(
                    nextConfigForExecution.configClass(), nextConfigForExecution.configExecutionOrder());
            componentsMetadata.add(parsedComponentMetadata);
        }
        initializedComponents.addAll(initializeComponents(componentsMetadata));
    }

    private List<InitializedComponent> initializeComponents(
            List<ParsedMetadata<ComponentExecutionMetadata>> parsedMetadata) {
        ComponentInitializer componentInitializer = new ComponentInitializer(parsedMetadata);
        List<InitializedComponent> initializedComponents = componentInitializer.initializeComponents();
        return initializedComponents;
    }
}
