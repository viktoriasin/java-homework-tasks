package homework.core;

import static org.assertj.core.api.Assertions.*;

import homework.SimpleCalculator;
import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;

@SuppressWarnings({"java:S106", "java:S5960", "java:S1144", "java:S112", "java:S1172", "java:S1181"})
public class SimpleCalculatorTest {

    private SimpleCalculator simpleCalculator;

    @Before
    public void setUp() {
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

    @After
    public void tearDown() {
        System.out.println("Tear down;");
    }
}
