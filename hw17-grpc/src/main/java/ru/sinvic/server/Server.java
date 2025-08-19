package ru.sinvic.server;

import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Remote server is starting...");

        RemoteServiceImpl remoteService = new RemoteServiceImpl();
        var server =
                ServerBuilder.forPort(SERVER_PORT).addService(remoteService).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received shutdown request");
            server.shutdown();
            log.info("Server stopped");
        }));

        log.info("server waiting for client connections...");
        server.awaitTermination();
    }
}
