package ru.sinvic.appcontainer.model;

public interface ComponentProcessingQueue {
    void addNewComponentForExecution(ComponentExecutionMetadata componentExecutionMetadata);

    ComponentExecutionMetadata getNextComponentForExecution();

    boolean hasNextComponentForExecution();
}
