package ru.sinvic.appcontainer.model;

import java.util.*;

public class ComponentProcessingQueueImpl implements ComponentProcessingQueue {
    private final PriorityQueue<ComponentExecutionMetadata> components =
            new PriorityQueue<>(Comparator.comparing(ComponentExecutionMetadata::componentExecutionOrder));

    @Override
    public void addNewComponentForExecution(ComponentExecutionMetadata componentExecutionMetadata) {
        components.add(componentExecutionMetadata);
    }

    @Override
    public ComponentExecutionMetadata getNextComponentForExecution() {
        return components.poll();
    }

    @Override
    public boolean hasNextComponentForExecution() {
        return !components.isEmpty();
    }
}
