import java.util.Iterator;

public interface Storage {
    void storeAmount(Denomination denomination, Integer quantity);

    boolean withdrawAmount(Denomination denomination, Integer quantity);

    Integer getDenominationQuantity(Denomination denomination);

    Iterator<DenominationAndItsQuantity> iterator();
}
