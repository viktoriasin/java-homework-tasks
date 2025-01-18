package homework;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private NavigableMap<Customer, String> customers =
            new TreeMap<>((o1, o2) -> (int) (o1.getScores() - o2.getScores()));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallestEntry = customers.firstEntry();
        if (smallestEntry == null) {
            return null;
        } else {
            return deepCopyCustomerEntry(smallestEntry);
        }
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> nextEntry = customers.higherEntry(customer);
        if (nextEntry == null) {
            return null;
        } else {
            return deepCopyCustomerEntry(nextEntry);
        }
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }

    private Map.Entry<Customer, String> deepCopyCustomerEntry(Map.Entry<Customer, String> originalEntry) {
        Customer customer = originalEntry.getKey();
        Customer customerCopy = new Customer(customer.getId(), customer.getName(), customer.getScores());
        return Map.entry(customerCopy, originalEntry.getValue());
    }
}
