package ru.sinvic.homework;

import ru.sinvic.homework.core.SimpleCalculatorTest;
import ru.sinvic.homework.core.TestLauncher;

public class Main {
    public static void main(String[] args) {
        TestLauncher testLauncher = new TestLauncher(SimpleCalculatorTest.class.getName());
        testLauncher.runTestsAndPrintResults();
    }
}
