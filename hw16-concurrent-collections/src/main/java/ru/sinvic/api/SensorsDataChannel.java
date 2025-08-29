package ru.sinvic.api;

import java.util.concurrent.TimeUnit;
import ru.sinvic.api.model.SensorData;

public interface SensorsDataChannel {
    boolean push(SensorData sensorData);

    boolean isEmpty();

    SensorData take(long timeout, TimeUnit unit) throws InterruptedException;
}
