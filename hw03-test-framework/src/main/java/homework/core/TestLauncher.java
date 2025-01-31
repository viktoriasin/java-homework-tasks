package homework.core;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"java:S106", "java:S5361", "java:S112"})
public class TestLauncher {

    private static final Logger logger = LoggerFactory.getLogger(TestLauncher.class);

    private static final String BEFORE_METHODS = "beforeMethods";
    private static final String AFTER_METHODS = "afterMethods";
    private static final String TEST_METHODS = "testMethods";

    private final Class<?> clazz;
    private final Constructor<?> constructor;

    public TestLauncher(String className) {
        try {
            this.clazz = Class.forName(className);
            this.constructor = clazz.getDeclaredConstructor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runTestsAndPrintResults() {
        Map<String, TestResult> testResults = run();
        printResults(testResults);
    }

    private Map<String, Set<Method>> getMethods() {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Map<String, Set<Method>> methods = new HashMap<>();
        methods.put("beforeMethods", getMethods(declaredMethods, Before.class));
        methods.put("afterMethods", getMethods(declaredMethods, After.class));
        methods.put("testMethods", getMethods(declaredMethods, Test.class));
        return methods;
    }

    private Map<String, TestResult> run() {
        Map<String, TestResult> testResults = new HashMap<>();
        Map<String, Set<Method>> methods = getMethods();

        for (Method testMethod : methods.getOrDefault(TEST_METHODS, Collections.emptySet())) {
            Object testObject;
            try {
                testObject = constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            boolean isConfigurationMethodRunSuccessful = true;
            for (Method beforeMethod : methods.getOrDefault(BEFORE_METHODS, Collections.emptySet())) {
                isConfigurationMethodRunSuccessful = runConfigurationMethod(beforeMethod, testObject);
                if (!isConfigurationMethodRunSuccessful) {
                    break;
                }
            }

            if (isConfigurationMethodRunSuccessful) {
                TestResult testResult = runTestMethod(testMethod, testObject);
                testResults.put(testMethod.getName(), testResult);
            }

            for (Method afterMethod : methods.getOrDefault(AFTER_METHODS, Collections.emptySet())) {
                runConfigurationMethod(afterMethod, testObject);
            }
        }

        return testResults;
    }

    private boolean runConfigurationMethod(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (Exception e) {
            logger.error(
                    "Error during running configuration method for testing {} method: {}",
                    testMethod,
                    getErrorMessage(e));
            return false;
        }
        return true;
    }

    private TestResult runTestMethod(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (Exception error) {
            return new TestResult(getErrorMessage(error));
        }
        return new TestResult("");
    }

    private void printResults(Map<String, TestResult> testResults) {
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
