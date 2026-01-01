package ru.sinvic.crm.service;

import java.util.List;
import ru.sinvic.crm.domain.Address;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.domain.Phone;

public interface DBServiceClient {

    Client getClient(long id);

    Phone getClientPhone(long clientId);

    Address getClientAddress(long clientId);

    List<Client> findAll();

    Client saveClientWithProfileInfo(String name, String street, String phoneNumber);
}
