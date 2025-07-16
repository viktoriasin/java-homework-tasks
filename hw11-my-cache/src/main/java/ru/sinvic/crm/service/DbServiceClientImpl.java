package ru.sinvic.crm.service;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.cachehw.HwCache;
import ru.sinvic.core.repository.DataTemplate;
import ru.sinvic.core.sessionmanager.TransactionRunner;
import ru.sinvic.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> dataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<Long, WeakReference<Client>> cache;

    public DbServiceClientImpl(
            TransactionRunner transactionRunner,
            DataTemplate<Client> dataTemplate,
            HwCache<Long, WeakReference<Client>> cache) {
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
                log.info("created client: {}", createdClient);
                return createdClient;
            }
            dataTemplate.update(connection, client);
            log.info("updated client: {}", client);
            return client;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        if (cache.get(id) != null && cache.get(id).get() != null) {
            log.info("found id {} in cache, loading from cache ..", id);
            return Optional.ofNullable(cache.get(id).get());
        } else {
            log.info("not found id {} in cache, loading from db ..", id);
            return transactionRunner.doInTransaction(connection -> {
                var clientOptional = dataTemplate.findById(connection, id);
                clientOptional.ifPresent(client -> cache.put(id, new WeakReference<>(client)));
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
