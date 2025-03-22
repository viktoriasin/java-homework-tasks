package atm;

import domain.DenominationAndItsQuantity;
import exceptions.ATMException;
import java.util.List;
import storage.Storage;
import util.AmountToDenominationConverter;

public class ATMImpl implements ATM {

    private final Storage storage;
    private final AmountToDenominationConverter denominationConverter;

    public ATMImpl(Storage storage, AmountToDenominationConverter denominationConverter) {
        this.storage = storage;
        this.denominationConverter = denominationConverter;
    }

    @Override
    public void deposit(DenominationAndItsQuantity denominationAndItsQuantity) {
        storage.storeAmount(denominationAndItsQuantity.denomination(), denominationAndItsQuantity.quantity());
    }

    @Override
    public void deposit(List<DenominationAndItsQuantity> denominationAndItsQuantity) {
        for (DenominationAndItsQuantity amount : denominationAndItsQuantity) {
            storage.storeAmount(amount.denomination(), amount.quantity());
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
    public int getRemainingAmount() {
        int remainingAmount = 0;
        for (DenominationAndItsQuantity denominationAndItsQuantity : storage) {
            remainingAmount += denominationAndItsQuantity.denomination().getDenominationValue()
                    * denominationAndItsQuantity.quantity();
        }
        return remainingAmount;
    }
}
