import java.util.List;

public interface AmountToDenominationConverter {
    void fillValues(Denomination[] values);

    List<DenominationAndItsQuantity> convert(Integer amount);
}
