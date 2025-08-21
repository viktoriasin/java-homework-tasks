package ru.sinvic.appcontainer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParsedConfig implements Iterable<ComponentExecutionMetadata> {
    private final Class<?> configClass;
    List<ComponentExecutionMetadata> componentsParsed = new ArrayList<>();

    public ParsedConfig(Class<?> configClass) {
        this.configClass = configClass;
    }

    public void add(ComponentExecutionMetadata componentExecutionMetadata) {
        componentsParsed.add(componentExecutionMetadata);
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    @Override
    public Iterator<ComponentExecutionMetadata> iterator() {
        return new Iterator<ComponentExecutionMetadata>() {
            private final Iterator<ComponentExecutionMetadata> it = componentsParsed.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public ComponentExecutionMetadata next() {
                return it.next();
            }
        };
    }
}
