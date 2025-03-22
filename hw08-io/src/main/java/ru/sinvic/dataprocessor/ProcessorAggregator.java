package ru.sinvic.dataprocessor;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.sinvic.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        return data.stream().collect(groupingBy(Measurement::name, Collectors.summingDouble(Measurement::value)));
    }
}
