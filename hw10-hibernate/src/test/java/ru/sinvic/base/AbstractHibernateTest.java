package ru.sinvic.base;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.sinvic.core.repository.DataTemplateHibernate;
import ru.sinvic.core.repository.HibernateUtils;
import ru.sinvic.core.sessionmanager.TransactionManagerHibernate;
import ru.sinvic.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.sinvic.crm.model.Client;
import ru.sinvic.crm.service.DBServiceClient;
import ru.sinvic.crm.service.DbServiceClientImpl;

import static ru.sinvic.demo.DbServiceDemo.HIBERNATE_CFG_FILE;

public abstract class AbstractHibernateTest {
    protected SessionFactory sessionFactory;
    protected TransactionManagerHibernate transactionManager;
    protected DataTemplateHibernate<Client> clientTemplate;
    protected DBServiceClient dbServiceClient;

    private static TestContainersConfig.CustomPostgreSQLContainer container;

    @BeforeAll
    public static void init() {
        container = TestContainersConfig.CustomPostgreSQLContainer.getInstance();
        container.start();
    }

    @AfterAll
    public static void shutdown() {
        container.stop();
    }

    @BeforeEach
    public void setUp() {
        String dbUrl = System.getProperty("app.datasource.demo-db.jdbcUrl");
        String dbUserName = System.getProperty("app.datasource.demo-db.username");
        String dbPassword = System.getProperty("app.datasource.demo-db.password");

        var migrationsExecutor = new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword);
        migrationsExecutor.executeMigrations();

        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUserName);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class);

        transactionManager = new TransactionManagerHibernate(sessionFactory);
        clientTemplate = new DataTemplateHibernate<>(Client.class);
        dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    protected EntityStatistics getUsageStatistics() {
        Statistics stats = sessionFactory.getStatistics();
        return stats.getEntityStatistics(Client.class.getName());
    }
}
