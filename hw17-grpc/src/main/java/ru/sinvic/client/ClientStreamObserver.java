package ru.sinvic.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.ServerResponse;

public class ClientStreamObserver implements io.grpc.stub.StreamObserver<ServerResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    private final CountDownLatch latch;
    private final CountDownLatch latchForClient;
    private boolean isExecutorIsStarted = false;

    private final AtomicLong index = new AtomicLong(0);
    private AtomicLong currentValue = new AtomicLong(0);

    private volatile long lastServerReturnedNumber;
    private volatile long lastClientUsedNumber;

    public ClientStreamObserver(CountDownLatch latch) {
        this.latch = latch;
        latchForClient = new CountDownLatch(1);
    }

    @Override
    public void onNext(ServerResponse serverResponse) {
        if (!isExecutorIsStarted) {
            Runnable task = () -> {
                var currentIndex = index.incrementAndGet();

                long delta;
                if (lastClientUsedNumber != lastServerReturnedNumber) {
                    lastClientUsedNumber = lastServerReturnedNumber;
                    delta = lastClientUsedNumber;
                } else {
                    delta = 0;
                }
                currentValue.addAndGet(1 + delta);

                log.info("currentValue:{}", currentValue);

                if (currentIndex == 1) {
                    latchForClient.countDown();
                    ;
                }
                if (currentIndex == 50) {
                    executor.shutdown();
                }
            };

            executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
            isExecutorIsStarted = true;
            try {
                latchForClient.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lastServerReturnedNumber = serverResponse.getResponseNumber();
        log.info("new value:{}", lastServerReturnedNumber);
    }

    @Override
    public void onError(Throwable e) {
        log.error("got error", e);
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
        latch.countDown();
    }
}
