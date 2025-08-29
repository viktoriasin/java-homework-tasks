package ru.sinvic.lib;

import java.util.List;
import ru.sinvic.api.model.SensorData;

public interface SensorDataBufferedWriter {
    void writeBufferedData(List<SensorData> bufferedData);
}
