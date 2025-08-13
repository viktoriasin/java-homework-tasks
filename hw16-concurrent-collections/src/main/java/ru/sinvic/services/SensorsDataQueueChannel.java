package ru.sinvic.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.api.SensorsDataChannel;
import ru.sinvic.api.model.SensorData;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SensorsDataQueueChannel implements SensorsDataChannel {
    private static final Logger log = LoggerFactory.getLogger(SensorsDataQueueChannel.class);

    private final BlockingQueue<SensorData> sensorsDataQueue;

    public SensorsDataQueueChannel(int sensorsDataQueueCapacity) {
        sensorsDataQueue = new ArrayBlockingQueue<>(sensorsDataQueueCapacity);
    }

    @Override
    public boolean push(SensorData sensorData) {
        var pushResult = sensorsDataQueue.offer(
                sensorData); // используем неблокирующий метод - так как какое то показание датчика из огромного
        // множества потерять не так уже и страшно
        if (!pushResult) {
            log.warn("Очередь показаний переполнена");
        }
        return pushResult;
    }

    @Override
    public boolean isEmpty() {
        return sensorsDataQueue.isEmpty();
    }

    @Override
    public SensorData take(long timeout, TimeUnit unit) throws InterruptedException {
        return sensorsDataQueue.poll(timeout, unit);
    }
}
