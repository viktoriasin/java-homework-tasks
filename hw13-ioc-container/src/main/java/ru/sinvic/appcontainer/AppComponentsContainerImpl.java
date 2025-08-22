package ru.sinvic.appcontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.sinvic.appcontainer.api.AppComponentsContainer;
import ru.sinvic.appcontainer.model.InitializedComponent;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    private final AppComponentContainerInitializer appComponentContainerInitializer;

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        this(new Class<?>[] {initialConfigClass});
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClass) {
        appComponentContainerInitializer = new AppComponentContainerInitializer(initialConfigClass);
        initializeContainer();
    }

    public AppComponentsContainerImpl(String packageName) {
        appComponentContainerInitializer = new AppComponentContainerInitializer(packageName);
        initializeContainer();
    }

    private void initializeContainer() {
        appComponentContainerInitializer.initializeContainer();
        List<InitializedComponent> initializedComponents = appComponentContainerInitializer.getInitializedComponent();
        containerAddAll(initializedComponents);
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

    private void containerAddAll(List<InitializedComponent> initializedComponents) {
        for (InitializedComponent initializedComponent : initializedComponents) {
            appComponents.add(initializedComponent.componentObject());
            appComponentsByName.put(initializedComponent.componentName(), initializedComponent.componentObject());
        }
    }
}
