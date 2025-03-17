public enum Denomination {
    TEN_RUBLE(10),
    FIFTH_RUBLE(50),
    ONE_HUNDRED_RUBLE(100),
    FIVE_HUNDRED_RUBLE(500),
    ONE_THOUSAND_RUBLE(1000),
    TWO_THOUSAND_RUBLE(2000);

    private final Integer denominationValue;

    Denomination(Integer denominationValue) {
        this.denominationValue = denominationValue;
    }

    public Integer getDenominationValue() {
        return denominationValue;
    }
}
