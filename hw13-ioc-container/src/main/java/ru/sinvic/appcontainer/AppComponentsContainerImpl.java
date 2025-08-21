package ru.sinvic.appcontainer;

import java.util.*;
import ru.sinvic.appcontainer.api.AppComponentsContainer;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.ParsedMetadata;
import ru.sinvic.appcontainer.utils.ComponentExecutor;
import ru.sinvic.appcontainer.utils.ConfigParser;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents;
    private final Map<String, Object> appComponentsByName;

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        ConfigParser<ComponentExecutionMetadata> configParser = new ConfigParser<>();
        ParsedMetadata<ComponentExecutionMetadata> parsedMetadata = configParser.parseConfig(initialConfigClass);
        ComponentExecutor componentExecutor = new ComponentExecutor(parsedMetadata);
        componentExecutor.executeComponents();
        appComponents = componentExecutor.getComponentsObject();
        appComponentsByName = componentExecutor.getComponentsByName();
    }

    //    public AppComponentsContainerImpl(Class<?> ... initialConfigClass) {
    //        for (Class<?> configClass : initialConfigClass) {
    //            ParsedMetadata parsedConfig = ConfigParser.parseConfig(initialConfigClass);
    //            ComponentExecutor componentExecutor = new ComponentExecutor(parsedConfig);
    //            componentExecutor.executeComponents();
    //            appComponents = componentExecutor.getComponentsObject();
    //            appComponentsByName = componentExecutor.getComponentsByName();
    //        }
    //    }

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
}
