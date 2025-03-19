package util;

import domain.DenominationAndItsQuantity;
import java.util.List;

public interface AmountToDenominationConverter {
    List<DenominationAndItsQuantity> convert(Integer amount);
}
