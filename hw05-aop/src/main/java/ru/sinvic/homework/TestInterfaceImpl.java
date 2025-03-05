package ru.sinvic.homework;

public class TestInterfaceImpl implements TestInterface {
    @Log
    @Override
    public int doWork(int param) {
        return param + 1;
    }
}
