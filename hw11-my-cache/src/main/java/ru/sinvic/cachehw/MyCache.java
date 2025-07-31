package ru.sinvic.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    // Надо реализовать эти методы
    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, value, "put");
        }
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, null, "remove");
        }
    }

    @Override
    public V get(K key) {
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, null, "get");
        }
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    public int size() {
        return cache.size();
    }
}
