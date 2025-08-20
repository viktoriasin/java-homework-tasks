package ru.sinvic.client;

import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.ClientMessage;
import ru.sinvic.RemoteServiceGrpc;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static long currentValue;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        log.info("Client is starting...");

        RemoteServiceGrpc.RemoteServiceStub asyncStub = RemoteServiceGrpc.newStub(channel);
        new Client().clientAction(asyncStub);

        log.info("Client is shutting down...");
        channel.shutdown();
    }

    private void clientAction(RemoteServiceGrpc.RemoteServiceStub asyncStub) {
        ClientStreamObserver clientStreamObserver = new ClientStreamObserver();
        asyncStub.getNumbers(makeClientRequest(), clientStreamObserver);

        for (int i = 0; i < 50; i++) {
            log.info("Current value: {}", getNextValue(clientStreamObserver));
            sleep();
        }
    }

    private ClientMessage makeClientRequest() {
        return ClientMessage.newBuilder().setFirstValue(1L).setLastValue(30L).build();
    }

    public long getNextValue(ClientStreamObserver clientStreamObserver) {
        currentValue = currentValue + clientStreamObserver.getLastValueAndReset() + 1;
        return currentValue;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
