package ru.sinvic.dataprocessor;

import java.util.List;
import ru.sinvic.model.Measurement;

public interface Loader {

    List<Measurement> load();
}
