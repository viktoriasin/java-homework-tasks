package util;

import denomination.Denomination;

public record DenominationAndItsQuantity(Denomination denomination, Integer quantity) {
    public Integer getAmount() {
        return denomination.getDenominationValue() * quantity;
    }
}
