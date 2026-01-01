package ru.sinvic.crm.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import ru.sinvic.crm.domain.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
    Optional<Address> findByClientId(Long clientId);
}
