package ru.sinvic.dataprocessor;

import ru.otus.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();
}
