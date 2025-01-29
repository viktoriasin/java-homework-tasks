package homework.core;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"java:S106", "java:S5361", "java:S112"})
public class TestLauncher {

    private final String className;
    private final Map<String, TestResult> testResults = new HashMap<>();
    private Set<Method> beforeMethods;
    private Set<Method> afterMethods;
    private Set<Method> testMethods;
    private Constructor<?> constructor;

    public TestLauncher(String className) {
        this.className = className;
    }

    public void runTestsAndPrintResults() {
        prepare();
        run();
        printResults();
    }

    private void prepare() {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
            constructor = clazz.getDeclaredConstructor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Method[] declaredMethods = clazz.getDeclaredMethods();
        beforeMethods = getMethods(declaredMethods, Before.class);
        afterMethods = getMethods(declaredMethods, After.class);
        testMethods = getMethods(declaredMethods, Test.class);
    }

    private void run() {
        for (Method testMethod : testMethods) {
            Object testObject;
            try {
                testObject = constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (Method beforeMethod : beforeMethods) {
                runConfigurationMethod(beforeMethod, testObject);
            }

            TestResult testResult = runTestMethod(testMethod, testObject);
            testResults.put(testMethod.getName(), testResult);

            for (Method afterMethod : afterMethods) {
                runConfigurationMethod(afterMethod, testObject);
            }
        }
    }

    private void runConfigurationMethod(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (Exception e) {
            throw new RuntimeException(getErrorMessage(e));
        }
    }

    private TestResult runTestMethod(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (Exception error) {
            return new TestResult(getErrorMessage(error));
        }
        return new TestResult("");
    }

    private void printResults() {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failsCount = new AtomicInteger();

        System.out.println();
        System.out.println("Test results:");
        testResults.forEach((testName, testResult) -> {
            boolean isTestSucceed = testResult.errorMessage().isEmpty();

            if (isTestSucceed) {
                successCount.getAndIncrement();
            } else {
                failsCount.getAndIncrement();
            }

            var msg = isTestSucceed ? "Success" : "Error: " + testResult.errorMessage();
            System.out.printf("%s: %s%n", testName, msg);
        });
        System.out.printf("Success test count: %d%n", successCount.get());
        System.out.printf("Failed test count: %d%n", failsCount.get());
        System.out.printf("Total test count: %d%n", (successCount.get() + failsCount.get()));
    }

    private Set<Method> getMethods(Method[] allMethods, Class<? extends Annotation> annotationClass) {
        Set<Method> methods = new HashSet<>();
        for (Method currentMethod : allMethods) {
            if (currentMethod.isAnnotationPresent(annotationClass)) {
                methods.add(currentMethod);
            }
        }
        return methods;
    }

    private String getErrorMessage(Exception error) {
        String message;
        if (error instanceof InvocationTargetException invocationError) {
            message = invocationError.getTargetException().getMessage().replaceAll("\n", "");
        } else {
            message = error.getMessage();
        }
        return message;
    }
}
