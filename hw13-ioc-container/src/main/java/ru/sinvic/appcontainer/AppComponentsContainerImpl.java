package ru.sinvic.appcontainer;

import java.util.*;
import ru.sinvic.appcontainer.api.AppComponentsContainer;
import ru.sinvic.appcontainer.model.*;
import ru.sinvic.appcontainer.utils.ComponentInitializer;
import ru.sinvic.appcontainer.utils.ConfigParser;
import ru.sinvic.appcontainer.utils.ConfigsParser;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        prepareContainer(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClass) {
        ConfigsParser configsParser = new ConfigsParser();
        ParsedMetadata<ConfigsExecutionMetadata> parsedConfigs = configsParser.parseConfigs(initialConfigClass);
        ExecutionProcessingQueue<ConfigsExecutionMetadata> configsExecutionMetadataExecutionProcessingQueue =
                new ExecutionProcessingQueue<>(Comparator.comparing(ConfigsExecutionMetadata::configExecutionOrder));
        for (ConfigsExecutionMetadata configsExecutionMetadata : parsedConfigs) {
            configsExecutionMetadataExecutionProcessingQueue.addNewComponentForExecution(configsExecutionMetadata);
        }
        while (configsExecutionMetadataExecutionProcessingQueue.hasNextForExecution()) {
            ConfigsExecutionMetadata nextConfigForExecution =
                    configsExecutionMetadataExecutionProcessingQueue.getNextForExecution();
            prepareContainer(nextConfigForExecution.configClass());
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        C componentForReturn = null;
        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                if (componentForReturn != null) {
                    throw new RuntimeException(String.format(
                            "Two component with the same type in container are prohibited! Type: %s", componentClass));
                }
                componentForReturn = (C) component;
            }
        }
        if (componentForReturn == null) {
            throw new RuntimeException(String.format("Component %s is not found in container", componentClass));
        }
        return componentForReturn;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        C c = (C) appComponentsByName.get(componentName);
        if (c == null) {
            throw new RuntimeException(String.format("Component %s is not found in container", componentName));
        }
        return c;
    }

    private void prepareContainer(Class<?> initialConfigClass) {
        ConfigParser configParser = new ConfigParser();
        ParsedMetadata<ComponentExecutionMetadata> parsedMetadata = configParser.parseConfig(initialConfigClass);
        ComponentInitializer componentInitializer = new ComponentInitializer(parsedMetadata);
        List<InitializedComponent> initializedComponents = componentInitializer.initializeComponents();
        addComponentsToContainers(initializedComponents);
    }

    private void addComponentsToContainers(List<InitializedComponent> initializedComponents) {
        for (InitializedComponent initializedComponent : initializedComponents) {
            appComponents.add(initializedComponent.componentObject());
            appComponentsByName.put(initializedComponent.componentName(), initializedComponent.componentObject());
        }
    }
}
