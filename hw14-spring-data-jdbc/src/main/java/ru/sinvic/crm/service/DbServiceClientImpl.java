package ru.sinvic.crm.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.sinvic.crm.domain.Address;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.domain.Phone;
import ru.sinvic.crm.repository.AddressRepository;
import ru.sinvic.crm.repository.ClientRepository;
import ru.sinvic.crm.repository.PhoneRepository;
import ru.sinvic.sessionmanager.TransactionManager;

@Service
@RequiredArgsConstructor
public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final TransactionManager transactionManager;
    private final ClientRepository clientRepository;
    private final PhoneRepository phoneRepository;
    private final AddressRepository addressRepository;

    public Client saveClientWithProfileInfo(String clientName, String street, String phoneNumber) {
        return transactionManager.doInTransaction(() -> {
            Client client = new Client(null, clientName);
            var savedClient = clientRepository.save(client);
            log.info("saved client: {}", savedClient);

            Long newClientId = savedClient.id();
            Address savedAddress = addressRepository.save(new Address(null, street, newClientId));
            log.info("saved clients address: {}", savedAddress);

            Phone savedPhone = phoneRepository.save(new Phone(null, phoneNumber, newClientId));
            log.info("saved clients phone: {}", savedPhone);

            return savedClient;
        });
    }

    @Override
    public Client getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(() -> {
            var clientOptional =
                    clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found!"));
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public Phone getClientPhone(long clientId) {
        return phoneRepository.findByClientId(clientId).orElseThrow(() -> new RuntimeException("Phone not found!"));
    }

    @Override
    public Address getClientAddress(long clientId) {
        return addressRepository.findByClientId(clientId).orElseThrow(() -> new RuntimeException("Address not found!"));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(() -> {
            var clientList = new ArrayList<>(clientRepository.findAll());
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
