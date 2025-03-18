package util;

import denomination.Denomination;
import domain.DenominationAndItsQuantity;
import java.util.*;

@SuppressWarnings("java:S1171")
public class AmountToDenominationConverterImpl implements AmountToDenominationConverter {
    private final TreeSet<Denomination> denominationSet =
            new TreeSet<>(Comparator.comparing(Denomination::getDenominationValue));

    {
        denominationSet.addAll(List.of(Denomination.values()));
    }

    @Override
    public List<DenominationAndItsQuantity> convert(Integer amount) {
        List<DenominationAndItsQuantity> result = new ArrayList<>();
        Iterator<Denomination> denominationIterator = denominationSet.descendingIterator();
        while (denominationIterator.hasNext()) {
            Denomination next = denominationIterator.next();
            Integer denominationValue = next.getDenominationValue();
            Integer denominationQuantity = amount / denominationValue;
            if (denominationQuantity != 0) {
                result.add(new DenominationAndItsQuantity(next, denominationQuantity));
                amount -= denominationQuantity * denominationValue;
            }
        }
        return result;
    }
}
