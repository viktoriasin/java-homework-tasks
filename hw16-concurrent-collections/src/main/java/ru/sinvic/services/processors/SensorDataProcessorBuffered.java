package ru.sinvic.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.api.SensorDataProcessor;
import ru.sinvic.api.model.SensorData;
import ru.sinvic.lib.SensorDataBufferedWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ArrayBlockingQueue<SensorData> dataBuffer = new ArrayBlockingQueue<>(3000);

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        dataBuffer.add(data);
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        if (!dataBuffer.isEmpty()) {
            List<SensorData> bufferedData = new ArrayList<>();
            dataBuffer.drainTo(bufferedData);
            bufferedData.sort(Comparator.comparing(SensorData::getMeasurementTime));
            try {
                if (!bufferedData.isEmpty()) {
                    writer.writeBufferedData(bufferedData);
                }
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
