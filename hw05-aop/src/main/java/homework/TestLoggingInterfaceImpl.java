package homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggingInterfaceImpl implements TestLoggingInterface {
    private static final Logger logger = LoggerFactory.getLogger(TestLoggingInterfaceImpl.class);

    @Override
    public void calculation(int param) {
        logInfo("Doing calculation for param: {}", param);
    }

    @Override
    public void calculation(int param1, int param2) {
        logInfo("Doing calculation for param: {}", param1, param2);
    }

    @Override
    public void calculation(int param1, int param2, String param3) {
        logInfo("Doing calculation for param: {}", param1, param2, param3);
    }

    @Override
    public void process(String param) {
        logInfo("Process param: {}", param);
    }

    @Override
    public String toString() {
        return "TestLoggingInterfaceImpl{}";
    }

    private void logInfo(String message, Object... params) {
        logger.atInfo().setMessage(message).addArgument(() -> params).log();
    }
}
