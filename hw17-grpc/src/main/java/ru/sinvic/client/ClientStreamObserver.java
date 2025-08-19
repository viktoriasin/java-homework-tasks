package ru.sinvic.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.ServerResponse;

public class ClientStreamObserver implements io.grpc.stub.StreamObserver<ServerResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private long lastServerReturnedNumber;

    @Override
    public void onNext(ServerResponse serverResponse) {
        lastServerReturnedNumber = serverResponse.getResponseNumber();
        log.info("new value:{}", lastServerReturnedNumber);
        setLastValue(lastServerReturnedNumber);
    }

    @Override
    public void onError(Throwable e) {
        log.error("got error", e);
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
    }

    private synchronized void setLastValue(long lastServerReturnedNumber) {
        this.lastServerReturnedNumber = lastServerReturnedNumber;
    }

    public synchronized long getLastValueAndReset() {
        long value = this.lastServerReturnedNumber;
        this.lastServerReturnedNumber = 0;
        return value;
    }
}
