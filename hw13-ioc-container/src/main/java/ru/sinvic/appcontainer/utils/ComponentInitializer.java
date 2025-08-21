package ru.sinvic.appcontainer.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.ExecutionProcessingQueue;
import ru.sinvic.appcontainer.model.InitializedComponent;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ComponentInitializer {
    private final ExecutionProcessingQueue<ComponentExecutionMetadata> componentProcessingQueue =
            new ExecutionProcessingQueue<>(Comparator.comparing(ComponentExecutionMetadata::componentExecutionOrder));
    private final ParsedMetadata<ComponentExecutionMetadata> parsedMetadata;
    private final List<InitializedComponent> components = new ArrayList<>();

    public ComponentInitializer(ParsedMetadata<ComponentExecutionMetadata> parsedMetadata) {
        this.parsedMetadata = parsedMetadata;
        for (ComponentExecutionMetadata componentExecutionMetadata : parsedMetadata) {
            componentProcessingQueue.addNewComponentForExecution(componentExecutionMetadata);
        }
    }

    public List<InitializedComponent> initializeComponents() {
        while (componentProcessingQueue.hasNextForExecution()) {
            ComponentExecutionMetadata component = componentProcessingQueue.getNextForExecution();
            Method method = component.method();
            Object configObject = getConfigObject(parsedMetadata.getConfigClass());
            List<Object> parameters = getComponentConstructorParameters(method);
            Object compoentnObject = getComponentObject(method, configObject, parameters);
            components.add(new InitializedComponent(component.componentName(), compoentnObject));
        }
        return components;
    }

    private List<Object> getComponentConstructorParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterCLass : parameterTypes) {
            for (InitializedComponent component : components) {
                Object componentObject = component.componentObject();
                if (parameterCLass.isAssignableFrom(componentObject.getClass())) {
                    parameters.add(componentObject);
                }
            }
        }
        return parameters;
    }

    private static Object getComponentObject(Method method, Object o, List<Object> parameters) {
        try {
            if (parameters.isEmpty()) {
                return method.invoke(o);
            } else {
                return method.invoke(o, parameters.toArray());
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new RuntimeException(
                    String.format(
                            "Error creating component object with method %s with parameters %s",
                            method.getName(), parameters),
                    e);
        }
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

    public ExecutionProcessingQueue<ComponentExecutionMetadata> getComponentProcessingQueue() {
        return componentProcessingQueue;
    }
}
