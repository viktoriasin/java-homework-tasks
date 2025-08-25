package ru.sinvic.appcontainer.model;

import java.util.*;

public class ExecutionProcessingQueue<T> {
    private final PriorityQueue<T> tasks;

    public ExecutionProcessingQueue(Comparator<T> comparator) {
        this.tasks = new PriorityQueue<>(comparator);
    }

    public void addNewComponentForExecution(T executionMetadata) {
        tasks.add(executionMetadata);
    }

    public T getNextForExecution() {
        return tasks.poll();
    }

    public boolean hasNextForExecution() {
        return !tasks.isEmpty();
    }
}
