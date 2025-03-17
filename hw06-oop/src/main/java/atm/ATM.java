public interface ATM {
    void deposit(Integer amount);

    void withdraw(Integer amount);

    Integer getRemainingAmount();
}
