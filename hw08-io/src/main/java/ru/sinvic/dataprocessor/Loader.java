package ru.sinvic.dataprocessor;

import ru.sinvic.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();
}
