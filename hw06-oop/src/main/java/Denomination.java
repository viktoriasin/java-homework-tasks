package homework;

public enum Denomination {
    ONE_KOPECK(0.01), FIVE_KOPECK(0.05), TEN_KOPECK(0.1),
    ONE_RUBLE(1), FIVE_RUBLE(5), FIFTH_RUBLE(50), ONE_HUNDRED_RUBLE(100), FIVE_HUNDRED_RUBLE(500),
    ONE_THOUSAND_RUBLE(100);

    private final double value;

    Denomination(double value) {
        this.value = value;
    }

    public double getValue() {
        return  value;
    }
}
