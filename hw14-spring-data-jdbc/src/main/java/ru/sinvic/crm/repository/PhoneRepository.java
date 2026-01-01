package ru.sinvic.crm.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sinvic.crm.domain.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {}
