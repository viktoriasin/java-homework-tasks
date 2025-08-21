package ru.sinvic.appcontainer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParsedMetadata<T extends Metadata> implements Iterable<T> {
    private final Class<?> configClass;
    List<T> metadataContainer = new ArrayList<>();

    public ParsedMetadata(Class<?> configClass) {
        this.configClass = configClass;
    }

    public void add(T metadata) {
        metadataContainer.add(metadata);
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final Iterator<T> it = metadataContainer.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }
        };
    }
}
