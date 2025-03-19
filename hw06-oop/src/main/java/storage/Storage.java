package storage;

import denomination.Denomination;
import domain.DenominationAndItsQuantity;
import java.util.Iterator;

public interface Storage extends Iterable<DenominationAndItsQuantity> {
    void storeAmount(Denomination denomination, Integer quantity);

    boolean withdrawAmount(Denomination denomination, Integer quantity);

    Integer getDenominationQuantity(Denomination denomination);

    Iterator<DenominationAndItsQuantity> iterator();
}
