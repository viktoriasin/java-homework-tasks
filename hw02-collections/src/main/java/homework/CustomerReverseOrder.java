package homework;

import java.util.*;

public class CustomerReverseOrder {

    Set<Customer> customers = new LinkedHashSet<>();
    Iterator<Customer> it;

    public void add(Customer customer) {
        customers.add(customer);
    }

    public Customer take() {
        if (it == null || !it.hasNext()) {
            it = new LinkedList<>(customers).descendingIterator();
        }
        return it.next();
    }
}
