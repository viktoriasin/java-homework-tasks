import static org.junit.jupiter.api.Assertions.*;

import denomination.Denomination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.InMemoryStorageImpl;
import storage.Storage;

class InMemoryStorageImplTest {
    Storage storage;
    Denomination denomination;
    Denomination denomination2;

    @BeforeEach
    void startUp() {
        storage = new InMemoryStorageImpl();
        denomination = Denomination.TEN_RUBLE;
        denomination2 = Denomination.ONE_HUNDRED_RUBLE;
    }

    @Test
    void testStoreAmountCorrectly() {
        storage.storeAmount(denomination, 1);
        assertEquals(1, storage.getDenominationQuantity(denomination));

        storage.storeAmount(denomination2, 2);
        assertEquals(2, storage.getDenominationQuantity(denomination2));
    }

    @Test
    void testWithdrawAmountCorrectly() {
        storage.storeAmount(denomination, 5);
        storage.storeAmount(denomination2, 2);

        storage.withdrawAmount(denomination, 3);

        assertEquals(2, storage.getDenominationQuantity(denomination));
        assertEquals(2, storage.getDenominationQuantity(denomination2));
    }

    @Test
    void testWithdrawAmountMoreThanContains() {
        storage.storeAmount(denomination, 5);

        assertFalse(storage.withdrawAmount(denomination, 6));
        assertEquals(5, storage.getDenominationQuantity(denomination));
    }
}
