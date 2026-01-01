package ru.sinvic.crm.service;

import ru.sinvic.crm.domain.Address;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.domain.Phone;
import ru.sinvic.crm.dto.ClientCreateDto;

import java.util.List;

public interface DBServiceClient {

    Client getClient(long id);

    Phone getClientPhone(long clientId);

    Address getClientAddress(long clientId);

    List<ClientCreateDto> findAllWithProfileInfo();

    Client saveClientWithProfileInfo(String name, String street, String phoneNumber);
}
