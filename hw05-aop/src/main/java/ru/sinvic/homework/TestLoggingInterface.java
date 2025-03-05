package ru.sinvic.homework;

public interface TestLoggingInterface {
    @Log
    int calculation(int param);

    int calculation(int param1, int param2);

    @Log
    String calculation(int param1, int param2, String param3);

    String process(String param);
}
