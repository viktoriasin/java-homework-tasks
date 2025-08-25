package ru.sinvic.appcontainer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParsedMetadata<T extends Metadata> implements Iterable<T> {
    List<T> metadataContainer = new ArrayList<>();

    public void add(T metadata) {
        metadataContainer.add(metadata);
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
