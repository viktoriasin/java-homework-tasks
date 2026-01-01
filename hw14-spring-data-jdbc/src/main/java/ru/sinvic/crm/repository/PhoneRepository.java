package ru.sinvic.crm.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import ru.sinvic.crm.domain.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    Optional<Phone> findByClientId(Long clientId);
}
