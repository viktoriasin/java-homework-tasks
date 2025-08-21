package ru.sinvic.appcontainer.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import ru.sinvic.appcontainer.model.ComponentExecutionMetadata;
import ru.sinvic.appcontainer.model.ExecutionProcessingQueue;
import ru.sinvic.appcontainer.model.ParsedMetadata;

public class ComponentExecutor {
    private final ExecutionProcessingQueue<ComponentExecutionMetadata> componentProcessingQueue =
            new ExecutionProcessingQueue<>(Comparator.comparing(ComponentExecutionMetadata::componentExecutionOrder));
    private final ParsedMetadata<ComponentExecutionMetadata> parsedMetadata;
    private final List<Object> componentsObject = new ArrayList<>();
    private final Map<String, Object> componentsByName = new HashMap<>();

    public ComponentExecutor(ParsedMetadata<ComponentExecutionMetadata> parsedMetadata) {
        this.parsedMetadata = parsedMetadata;
        for (ComponentExecutionMetadata componentExecutionMetadata : parsedMetadata) {
            componentProcessingQueue.addNewComponentForExecution(componentExecutionMetadata);
        }
    }

    public void executeComponents() {
        while (componentProcessingQueue.hasNextForExecution()) {
            ComponentExecutionMetadata component = componentProcessingQueue.getNextForExecution();
            Method method = component.method();
            Object configObject = getConfigObject(parsedMetadata.getConfigClass());
            List<Object> parameters = getComponentConstructorParameters(method);
            Object invoked = getComponentObject(method, configObject, parameters);
            componentsObject.add(invoked);
            componentsByName.put(component.componentName(), invoked);
        }
    }

    public List<Object> getComponentsObject() {
        return componentsObject;
    }

    public Map<String, Object> getComponentsByName() {
        return componentsByName;
    }

    private List<Object> getComponentConstructorParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterCLass : parameterTypes) {
            for (Object component : componentsObject) {
                if (parameterCLass.isAssignableFrom(component.getClass())) {
                    parameters.add(component);
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
                            "Error creating component object with method %s with parameters %s %s",
                            method.getName(),
                            parameters.get(0).getClass(),
                            parameters.get(0).getClass().getInterfaces()[0].getTypeName()),
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
