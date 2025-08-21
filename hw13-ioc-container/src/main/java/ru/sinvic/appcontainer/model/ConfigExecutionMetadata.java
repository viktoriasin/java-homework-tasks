package ru.sinvic.appcontainer.model;

public record ConfigExecutionMetadata(Integer configExecutionOrder, Class<?> configClass) implements Metadata {}
