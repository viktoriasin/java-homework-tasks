import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InMemoryStorageImpl implements Storage {
    private final Map<Denomination, Integer> storage = new HashMap<>();

    @Override
    public void storeAmount(Denomination denomination, Integer quantity) {
        storage.putIfAbsent(denomination, 0);
        storage.put(denomination, storage.get(denomination) + quantity);
    }

    @Override
    public boolean withdrawAmount(Denomination denomination, Integer quantity) {
        if (storage.get(denomination) == null || storage.get(denomination) < quantity) {
            return false;
        }
        storage.put(denomination, storage.get(denomination) - quantity);
        return true;
    }

    @Override
    public Integer getDenominationQuantity(Denomination denomination) {
        return storage.getOrDefault(denomination, 0);
    }

    @Override
    public Iterator<DenominationAndItsQuantity> iterator() {
        return storage.entrySet().stream()
                .map(entry -> new DenominationAndItsQuantity(entry.getKey(), entry.getValue()))
                .iterator();
    }
}
