package ru.sinvic.dataprocessor;

import ru.sinvic.model.Measurement;

import java.util.List;
import java.util.Map;

public interface Processor {

    Map<String, Double> process(List<Measurement> data);
}
