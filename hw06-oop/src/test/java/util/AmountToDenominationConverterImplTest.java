package util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import denomination.Denomination;
import domain.DenominationAndItsQuantity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AmountToDenominationConverterImplTest {

    @Test
    void testConvertCorrectly() {
        AmountToDenominationConverterImpl treeSetAmountToDenominationConverter =
                new AmountToDenominationConverterImpl();

        Set<DenominationAndItsQuantity> testResult = new HashSet<>(treeSetAmountToDenominationConverter.convert(13260));

        Set<DenominationAndItsQuantity> correctResult = new HashSet<>(List.of(
                new DenominationAndItsQuantity(Denomination.FIVE_THOUSAND_RUBLE, 2),
                new DenominationAndItsQuantity(Denomination.TWO_THOUSAND_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.ONE_THOUSAND_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.ONE_HUNDRED_RUBLE, 2),
                new DenominationAndItsQuantity(Denomination.FIFTH_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.TEN_RUBLE, 1)));

        assertEquals(correctResult, testResult);
    }
}
