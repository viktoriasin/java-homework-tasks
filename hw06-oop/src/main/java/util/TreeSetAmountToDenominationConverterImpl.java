import java.util.*;

public class TreeSetAmountToDenominationConverterImpl implements AmountToDenominationConverter {
    private final TreeSet<Denomination> denominationSet =
            new TreeSet<>(Comparator.comparing(Denomination::getDenominationValue));

    @Override
    public void fillValues(Denomination[] values) {
        denominationSet.addAll(List.of(values));
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
