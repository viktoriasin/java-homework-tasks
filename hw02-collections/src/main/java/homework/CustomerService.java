package homework;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    NavigableMap<Customer, String> customers = new TreeMap<>((o1, o2) -> (int) (o1.getScores() - o2.getScores()));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallestEntry = customers.firstEntry();
        Map.Entry<Customer, String> smallestEntryCopy = null;

        if (smallestEntry != null) {
            smallestEntryCopy = deepCopyCustomerEntry(smallestEntry);
        }

        return smallestEntryCopy;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> nextEntry = customers.higherEntry(customer);
        Map.Entry<Customer, String> nextEntryCopy = null;

        if (nextEntry != null) {
            nextEntryCopy = deepCopyCustomerEntry(nextEntry);
        }

        return nextEntryCopy;
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }

    private Map.Entry<Customer, String> deepCopyCustomerEntry(Map.Entry<Customer, String> originalEntry) {
        Map.Entry<Customer, String> copyEntry;
        Customer customer = originalEntry.getKey();
        copyEntry = Map.entry(
                new Customer(customer.getId(), customer.getName(), customer.getScores()), originalEntry.getValue());
        return copyEntry;
    }
}
