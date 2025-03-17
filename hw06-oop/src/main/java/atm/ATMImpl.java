import java.util.Iterator;
import java.util.List;

public class ATMImpl implements ATM {
    private final Storage storage;
    private final AmountToDenominationConverter denominationConverter;

    public ATMImpl(Storage storage, AmountToDenominationConverter denominationConverter) {
        this.storage = storage;
        this.denominationConverter = denominationConverter;
    }

    public void prepare(Denomination valuesHolder) {
        denominationConverter.fillValues(Denomination.values());
    }

    @Override
    public void deposit(Integer amount) {
        List<DenominationAndItsQuantity> denominationsWithQuantity = denominationConverter.convert(amount);
        for (DenominationAndItsQuantity denominationAndItsQuantity : denominationsWithQuantity) {
            storage.storeAmount(denominationAndItsQuantity.denomination(), denominationAndItsQuantity.quantity());
        }
    }

    @Override
    public void withdraw(Integer amount) {
        Integer remainingAmount = getRemainingAmount();
        if (amount > remainingAmount) {
            throw new ATMException("Недостаточно средств для изъятия " + amount + " рублей. Текущий остаток "
                    + remainingAmount + " рублей.");
        }
        List<DenominationAndItsQuantity> denominationsWithQuantity = denominationConverter.convert(amount);
        for (DenominationAndItsQuantity denominationAndItsQuantity : denominationsWithQuantity) {
            storage.withdrawAmount(denominationAndItsQuantity.denomination(), denominationAndItsQuantity.quantity());
        }
    }

    @Override
    public Integer getRemainingAmount() {
        Integer remainingAmount = 0;
        Iterator<DenominationAndItsQuantity> it = storage.iterator();
        while (it.hasNext()) {
            DenominationAndItsQuantity denominationAndItsQuantity = it.next();
            remainingAmount += denominationAndItsQuantity.denomination().getDenominationValue()
                    * denominationAndItsQuantity.quantity();
        }
        return remainingAmount;
    }
}
