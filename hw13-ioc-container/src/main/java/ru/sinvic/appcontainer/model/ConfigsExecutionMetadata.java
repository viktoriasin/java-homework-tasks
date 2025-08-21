package ru.sinvic.appcontainer.model;

public record ConfigsExecutionMetadata(Integer configExecutionOrder, Class<?> configClass) implements Metadata {}
