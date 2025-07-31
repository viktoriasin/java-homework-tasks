package ru.sinvic.demo;

import java.util.List;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.core.repository.DataTemplateHibernate;
import ru.sinvic.core.repository.HibernateUtils;
import ru.sinvic.core.sessionmanager.TransactionManagerHibernate;
import ru.sinvic.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.sinvic.crm.model.Address;
import ru.sinvic.crm.model.Client;
import ru.sinvic.crm.model.Phone;
import ru.sinvic.crm.service.DbServiceClientImpl;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        dbServiceClient.saveClient(new Client(
                null,
                "dbServiceFirst",
                new Address(null, "AnyStreet"),
                List.of(new Phone(null, "13-555-22"), new Phone(null, "14-666-333"))));

        var clientSecond = dbServiceClient.saveClient(new Client(
                null,
                "dbServiceSecond",
                new Address(null, "Street1"),
                List.of(new Phone(null, "13-555-21"), new Phone(null, "14-666-353"))));
        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);
        ///
        dbServiceClient.saveClient(new Client(
                clientSecondSelected.getId(),
                "dbServiceSecondUpdated",
                new Address(null, "Street1"),
                List.of(new Phone(null, "13-555-21"), new Phone(null, "14-666-353"))));
        var clientUpdated = dbServiceClient
                .getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }
}
