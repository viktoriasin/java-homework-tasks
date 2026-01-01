package ru.sinvic.crm.service;

import java.util.List;
import java.util.Optional;
import ru.sinvic.crm.domain.Client;

public interface DBServiceClient {

    Optional<Client> getClient(long id);

    List<Client> findAll();

    Client saveClientWithProfileInfo(String name, String street, String phoneNumber);
}
