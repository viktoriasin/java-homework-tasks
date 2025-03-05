package ru.sinvic.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class TestLoggingInterfaceImpl implements TestLoggingInterface {
    private static final Logger logger = LoggerFactory.getLogger(TestLoggingInterfaceImpl.class);

    @Log
    @Override
    public int calculation(int param) {
        return param * param;
    }

    @Override
    public int calculation(int param1, int param2) {
        return param1 * param2;
    }

    @Log
    @Override
    public String calculation(int param1, int param2, String param3) {
        return param3 + Integer.toString(param1 * param2);
    }

    @Override
    public String process(String param) {
        return param.toUpperCase();
    }

    @Override
    public String toString() {
        return "TestLoggingInterfaceImpl{}";
    }
}
