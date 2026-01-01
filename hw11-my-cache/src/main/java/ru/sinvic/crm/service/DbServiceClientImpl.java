package ru.sinvic.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.cachehw.HwCache;
import ru.sinvic.core.repository.DataTemplate;
import ru.sinvic.core.sessionmanager.TransactionRunner;
import ru.sinvic.crm.domain.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> dataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<Long, Client> cache;

    public DbServiceClientImpl(
            TransactionRunner transactionRunner, DataTemplate<Client> dataTemplate, HwCache<Long, Client> cache) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionRunner.doInTransaction(connection -> {
            if (client.getId() == null) {
                var clientId = dataTemplate.insert(connection, client);
                var createdClient = new Client(clientId, client.getName(), client.getAge());
                cache.put(
                        Long.valueOf(createdClient.getId()),
                        createdClient); // для домашки оставлю, но вообще тут часть лонгов будет из кеша лонгов (вплоть
                // до 127), поэтому эти значения не почистятся из кеша
                log.info("created client: {}", createdClient);
                return createdClient;
            }
            dataTemplate.update(connection, client);
            cache.put(Long.valueOf(client.getId()), client);
            log.info("updated client: {}", client);
            return client;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client clientFromCache = cache.get(id);
        if (clientFromCache != null) {
            log.info("found id {} in cache, loading from cache ..", id);
            return Optional.of(clientFromCache);
        } else {
            log.info("not found id {} in cache, loading from db ..", id);
            return transactionRunner.doInTransaction(connection -> {
                var clientOptional = dataTemplate.findById(connection, id);
                clientOptional.ifPresent(client -> cache.put(id, client));
                log.info("client: {}", clientOptional);
                return clientOptional;
            });
        }
    }

    @Override
    public List<Client> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var clientList = dataTemplate.findAll(connection);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
