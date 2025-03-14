public record Denome(Denomination denomination, Integer quantity) {
    public Double getAmount() {
        return denomination.getDenominationValue() * quantity;
    }
}
