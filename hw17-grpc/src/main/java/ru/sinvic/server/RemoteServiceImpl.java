package ru.sinvic.server;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.ClientMessage;
import ru.sinvic.RemoteServiceGrpc;
import ru.sinvic.ServerResponse;

public class RemoteServiceImpl extends RemoteServiceGrpc.RemoteServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(RemoteServiceImpl.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Override
    public void getNumbers(ClientMessage request, StreamObserver<ServerResponse> responseObserver) {
        log.info("Receive client request from {} to {}", request.getFirstValue(), request.getLastValue());
        AtomicLong currentValue = new AtomicLong(request.getFirstValue());

        Runnable task = () -> {
            var value = currentValue.incrementAndGet();
            ServerResponse response =
                    ServerResponse.newBuilder().setResponseNumber(value).build();
            responseObserver.onNext(response);
            if (value == request.getLastValue()) {
                executor.shutdown();
                responseObserver.onCompleted();
                log.info("sequence of numbers finished");
            }
        };

        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }
}
