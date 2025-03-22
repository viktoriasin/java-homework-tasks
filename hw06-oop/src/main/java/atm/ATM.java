package atm;

import domain.DenominationAndItsQuantity;
import java.util.List;

public interface ATM {
    void deposit(DenominationAndItsQuantity denominationAndItsQuantity);

    void deposit(List<DenominationAndItsQuantity> denominationAndItsQuantity);

    void withdraw(Integer amount);

    int getRemainingAmount();
}
