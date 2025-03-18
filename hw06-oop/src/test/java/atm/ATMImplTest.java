import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import atm.ATM;
import atm.ATMImpl;
import denomination.Denomination;
import domain.DenominationAndItsQuantity;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import storage.Storage;
import util.AmountToDenominationConverter;

@ExtendWith(MockitoExtension.class)
class ATMImplTest {

    private static final int AMOUNT_TO_WITHDRAW = 13260;
    private static final int AMOUNT_TO_WITHDRAW2 = 13270;
    private static final DenominationAndItsQuantity DENOMINATION_TO_DEPOSIT =
            new DenominationAndItsQuantity(Denomination.FIVE_HUNDRED_RUBLE, 3);

    @Mock
    Storage storage;

    @Mock
    AmountToDenominationConverter denominationConverter;

    ATM atm;

    private static List<DenominationAndItsQuantity> generateTestListOfDenominationAndItsQuantity() {
        return List.of(
                new DenominationAndItsQuantity(Denomination.FIVE_THOUSAND_RUBLE, 2),
                new DenominationAndItsQuantity(Denomination.TWO_THOUSAND_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.ONE_THOUSAND_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.ONE_HUNDRED_RUBLE, 2),
                new DenominationAndItsQuantity(Denomination.FIFTH_RUBLE, 1),
                new DenominationAndItsQuantity(Denomination.TEN_RUBLE, 1));
    }

    @BeforeEach
    void setUp() {
        atm = new ATMImpl(storage, denominationConverter);
    }

    @Test
    void testDepositCorrectly() {
        atm.deposit(DENOMINATION_TO_DEPOSIT);
        verify(storage, times(1)).storeAmount(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(storage, times(1)).storeAmount(Denomination.FIVE_HUNDRED_RUBLE, 3);
    }

    @Test
    void testRemainingAmount() {
        when(storage.iterator()).thenReturn(Collections.emptyIterator());
        assertEquals(0, atm.getRemainingAmount());

        when(storage.iterator())
                .thenReturn(generateTestListOfDenominationAndItsQuantity().iterator());
        assertEquals(AMOUNT_TO_WITHDRAW, atm.getRemainingAmount());
    }

    @Test
    void testWithdrawCorrectly() {
        when(denominationConverter.convert(AMOUNT_TO_WITHDRAW))
                .thenReturn(generateTestListOfDenominationAndItsQuantity());
        when(storage.iterator())
                .thenReturn(generateTestListOfDenominationAndItsQuantity().iterator());

        atm.deposit(DENOMINATION_TO_DEPOSIT);
        atm.withdraw(AMOUNT_TO_WITHDRAW);

        verify(storage, times(6)).withdrawAmount(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(storage, times(1)).withdrawAmount(Denomination.FIVE_THOUSAND_RUBLE, 2);
        verify(storage, times(1)).withdrawAmount(Denomination.TWO_THOUSAND_RUBLE, 1);
        verify(storage, times(1)).withdrawAmount(Denomination.ONE_THOUSAND_RUBLE, 1);
        verify(storage, times(1)).withdrawAmount(Denomination.ONE_HUNDRED_RUBLE, 2);
        verify(storage, times(1)).withdrawAmount(Denomination.FIFTH_RUBLE, 1);
        verify(storage, times(1)).withdrawAmount(Denomination.TEN_RUBLE, 1);
        assertEquals(0, atm.getRemainingAmount());
    }

    @Test
    void testWithdrawMoreThanInStorage() {
        when(storage.iterator())
                .thenReturn(generateTestListOfDenominationAndItsQuantity().iterator());

        atm.deposit(DENOMINATION_TO_DEPOSIT);
        assertFalse(atm.withdraw(AMOUNT_TO_WITHDRAW2));
    }
}
