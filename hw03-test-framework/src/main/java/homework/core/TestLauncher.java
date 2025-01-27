package homework.core;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"java:S106", "java:S5960", "java:S1144", "java:S112", "java:S1172", "java:S1181", "java:S1117"})
public class TestLauncher {
    private final String className;
    private List<Method> beforeMethods;
    private List<Method> afterMethods;
    private List<Method> testMethods;
    private Map<String, TestRunInformation> testResults;
    Constructor<?> constructor;

    public TestLauncher(String className) {
        this.className = className;
    }

    private void prepare() throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Class.forName(className);
        constructor = clazz.getDeclaredConstructor();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        beforeMethods = getMethods(declaredMethods, Before.class);
        afterMethods = getMethods(declaredMethods, After.class);
        testMethods = getMethods(declaredMethods, Test.class);
        testResults = new HashMap<>();
    }

    public void testClassAndPrintResults()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
                    IllegalAccessException {
        prepare();
        run();
        printResults();
    }

    private void printResults() {
        int successCount = 0;
        int failsCount = 0;

        System.out.println("\t\t========TEST RESULTS==========\t\t");
        System.out.println();
        for (Map.Entry<String, TestRunInformation> mapEntry : testResults.entrySet()) {
            String methodName = mapEntry.getKey();
            TestRunInformation info = mapEntry.getValue();
            boolean isTestSucceed = info.errorMessage().isEmpty();
            if (isTestSucceed) {
                successCount++;
            } else {
                failsCount++;
            }

            System.out.print("Test name: " + methodName + " Result: " + (isTestSucceed ? "success" : "fail") + "\n");
            if (!isTestSucceed) {
                System.out.print("Error message: " + info.errorMessage());
            }
            System.out.println();
        }

        System.out.println("|\tSuccess counts\t|\tError counts\t|\tTotal count\t|");
        System.out.println("|\t" + successCount + "\t|\t" + failsCount + "\t|\t" + successCount + failsCount + "\t|");
        System.out.println("\t\t===================================\t\t");
    }

    private void run() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Method testMethod : testMethods) {
            Object testObject = constructor.newInstance();
            for (Method beforeMethod : beforeMethods) {
                runHelperMethod(beforeMethod, testObject);
            }

            TestRunInformation testRunInformation = runTest(testMethod, testObject);
            testResults.put(testMethod.getName(), testRunInformation);

            for (Method afterMethod : afterMethods) {
                runHelperMethod(afterMethod, testObject);
            }
        }
    }

    private List<Method> getMethods(Method[] allMethods, Class<? extends Annotation> methodType) {
        List<Method> beforeMethods = new ArrayList<>();
        for (Method currentMethod : allMethods) {
            if (currentMethod.isAnnotationPresent(methodType)) {
                beforeMethods.add(currentMethod);
            }
        }
        return beforeMethods;
    }

    private void runHelperMethod(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private TestRunInformation runTest(Method testMethod, Object testObject) {
        try {
            testMethod.invoke(testObject);
        } catch (Error | IllegalAccessException | InvocationTargetException error) {

            return new TestRunInformation(error.getMessage());
        }
        return new TestRunInformation("");
    }
}
