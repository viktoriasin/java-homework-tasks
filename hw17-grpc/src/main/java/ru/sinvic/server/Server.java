package ru.sinvic.server;

import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
Разработать клиент-серверное приложение с применением технологии gRPC.

Серверная часть.
сервер по запросу клиента генерирует последовательность чисел.
запрос от клиента содержит начальное значение (firstValue) и конечное(lastValue).
Раз в две секунды сервер генерирует новое значение и "стримит" его клиенту:
firstValue + 1
firstValue + 2
    ...
lastValue

Клиентская часть.
клиент отправдяет запрос серверу для получения последовательности чисел от 0 до 30.
клиент запускает цикл от 0 до 50.
раз в секунду выводит в консоль число (currentValue) по такой формуле:
currentValue = [currentValue] + [ПОСЛЕДНЕЕ число от сервера] + 1
начальное значение: currentValue = 0
Число, полученное от сервера должно учитываться только один раз.
    Обратите внимание, сервер может вернуть несколько чисел, надо взять именно ПОСЛЕДНЕЕ.

Должно получиться примерно так:
currentValue:1
число от сервера:2
currentValue:4 <--- число от сервера учитываем только один раз
currentValue:5 <--- тут число от сервера уже не учитывается.
число от сервера:3
currentValue:9
currentValue:10
    new value:4
currentValue:15
currentValue:16

Для коммуникации используйте gRPC.
Клиент и сервер не обязательно разделять по модулям.
Можно сделать один модуль с двумя main-классами для клиента и сервера.

Пример лога работы клиента (new value - это значение полученное от сервера)

21:44:04.782 [main] INFO ru.otus.numbers.client.NumbersClient - numbers Client is starting...
    21:44:04.932 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:1
    21:44:05.140 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:2
    21:44:05.933 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:4
    21:44:06.933 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:5
    21:44:07.113 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:3
    21:44:07.934 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:9
    21:44:08.934 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:10
    21:44:09.112 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:4
    21:44:09.935 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:15
    21:44:10.935 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:16
    21:44:11.113 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:5
    21:44:11.935 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:22
    21:44:12.936 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:23
    21:44:13.113 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:6
    21:44:13.936 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:30
    21:44:14.937 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:31
    21:44:15.114 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:7
    21:44:15.938 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:39
    21:44:16.938 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:40
    21:44:17.113 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:8
    21:44:17.939 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:49
    21:44:18.939 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:50
    21:44:19.113 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:9
    21:44:19.940 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:60
    21:44:20.940 [main] INFO ru.otus.numbers.client.NumbersClient - currentValue:61
    21:44:21.114 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - new value:10
    21:44:21.119 [grpc-default-executor-0] INFO r.o.n.client.ClientStreamObserver - request completed

**/

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
