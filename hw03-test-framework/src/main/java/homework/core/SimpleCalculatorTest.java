package homework.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import homework.SimpleCalculator;
import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S5960")
public class SimpleCalculatorTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCalculatorTest.class);

    public SimpleCalculatorTest() {
        logger.debug("New instance created;");
    }

    private SimpleCalculator simpleCalculator;

    @Before
    public void setUp() {
        logger.debug("Set up;");
        simpleCalculator = new SimpleCalculator();
    }

    @Test
    public void testSum() {
        int result = 1 + 2;
        assertThat(result).isEqualTo(simpleCalculator.sum(1, 2));
    }

    @Test
    public void testSubtract() {
        int result = 4 - 2;
        assertThat(result).isEqualTo(simpleCalculator.subtract(4, 2));
    }

    @Test
    public void testDivideByZero() {
        assertThatThrownBy(() -> simpleCalculator.divide(1, 0))
                .isInstanceOf(ArithmeticException.class)
                .hasMessageContaining("/ by zero");
    }

    @After
    public void tearDown() {
        logger.debug("Tear down;");
    }
}
