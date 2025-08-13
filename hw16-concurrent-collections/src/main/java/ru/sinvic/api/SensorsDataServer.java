package ru.sinvic.api;

import ru.sinvic.api.model.SensorData;

public interface SensorsDataServer {
    void onReceive(SensorData sensorData);
}
