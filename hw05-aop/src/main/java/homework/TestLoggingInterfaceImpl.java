package homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggingInterfaceImpl implements TestLoggingInterface {
    private static final Logger logger = LoggerFactory.getLogger(TestLoggingInterfaceImpl.class);

    @Override
    public void calculation(int param) {
        logger.info("Executing:{}", param);
    }

    @Override
    public String toString() {
        return "TestLoggingInterfaceImpl{}";
    }
}
