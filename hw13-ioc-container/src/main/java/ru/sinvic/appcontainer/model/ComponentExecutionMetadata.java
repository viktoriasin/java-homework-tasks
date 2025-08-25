package ru.sinvic.appcontainer.model;

import java.lang.reflect.Method;

public record ComponentExecutionMetadata(
        Integer componentExecutionOrder, String componentName, Method method, Object configObject)
        implements Metadata {}
