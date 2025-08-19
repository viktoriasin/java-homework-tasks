package ru.sinvic.client;

import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.ClientMessage;
import ru.sinvic.RemoteServiceGrpc;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        log.info("Client is starting...");

        var latch = new CountDownLatch(1);
        var newStub = RemoteServiceGrpc.newStub(channel);
        newStub.getNumbers(
                ClientMessage.newBuilder().setFirstValue(1L).setLastValue(30L).build(),
                new ClientStreamObserver(latch));

        latch.await();

        channel.shutdown();
    }
}
