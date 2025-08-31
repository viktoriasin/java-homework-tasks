package ru.sinvic.crm.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sinvic.crm.domain.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {}
