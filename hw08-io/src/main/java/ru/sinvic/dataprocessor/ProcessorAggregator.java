package ru.sinvic.dataprocessor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.sinvic.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        return Collections.emptyMap();
    }
}
