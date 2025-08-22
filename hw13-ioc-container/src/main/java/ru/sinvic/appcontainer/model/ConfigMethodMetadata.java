package ru.sinvic.appcontainer.model;

import java.lang.reflect.Method;

public record ConfigMethodMetadata(Integer componentExecutionOrder, String componentName, Method configMethod) {}
