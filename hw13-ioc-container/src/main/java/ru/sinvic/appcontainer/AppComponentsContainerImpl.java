package ru.sinvic.appcontainer;

import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainer;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final ConfigMethodsMetadata configMethodsMetadata = new ConfigMethodsMetadata();
    private final Set<String> componentNames = new HashSet<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
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

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        for (Method method : configClass.getDeclaredMethods()) {
            configMethodsMetadata.addMethodMetadata(method);
        }

        processComponents(configClass);
    }

    private void processComponents(Class<?> configClass) {
        Iterator<MethodMetadata> iterator = configMethodsMetadata.iterator();
        while (iterator.hasNext()) {
            MethodMetadata methodMetadata = iterator.next();
            Method method = methodMetadata.method();
            Object configObject = getConfigObject(configClass);
            List<Object> parameters = getComponentConstructorParameters(method);
            Object invoked = getComponentObject(method, configObject, parameters);
            appComponents.add(invoked);
            appComponentsByName.put(methodMetadata.componentName(), invoked);
        }
    }

    private List<Object> getComponentConstructorParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterCLass : parameterTypes) {
            for (Object component : appComponents) {
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

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private class ConfigMethodsMetadata implements Iterable<MethodMetadata> {
        private final PriorityQueue<MethodMetadata> methods =
                new PriorityQueue<>(Comparator.comparing(MethodMetadata::methodExecutionOrder));

        public void addMethodMetadata(Method method) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String componentName = annotation.name();
                if (componentNames.contains(componentName)) {
                    throw new RuntimeException(
                        String.format("Two components with the same name %s are prohibited!", componentName));
                }
                methods.add(new MethodMetadata(annotation.order(), componentName, method));
                componentNames.add(componentName);
            }
        }

        @Override
        public Iterator<MethodMetadata> iterator() {
            return new Iterator<>() {
                //                final PriorityQueue<MethodMetadata> copied = new
                // PriorityQueue<>(methods.comparator());
                //
                //                {
                //                    copied.addAll(methods);
                //                }

                @Override
                public boolean hasNext() {
                    return !methods.isEmpty();
                }

                @Override
                public MethodMetadata next() {
                    return methods.poll();
                }
            };
        }
    }

    private record MethodMetadata(Integer methodExecutionOrder, String componentName, Method method) {}
}
