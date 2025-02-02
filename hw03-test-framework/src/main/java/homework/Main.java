package homework;

import homework.core.SimpleCalculatorTest;
import homework.core.TestLauncher;

public class Main {
    public static void main(String[] args) {
        TestLauncher testLauncher = new TestLauncher(SimpleCalculatorTest.class.getName());
        testLauncher.runTestsAndPrintResults();
    }
}
