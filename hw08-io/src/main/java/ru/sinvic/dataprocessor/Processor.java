package ru.sinvic.dataprocessor;

import java.util.List;
import java.util.Map;
import ru.sinvic.model.Measurement;

public interface Processor {

    Map<String, Double> process(List<Measurement> data);
}
