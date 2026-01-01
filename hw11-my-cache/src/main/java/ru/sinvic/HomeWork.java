package ru.sinvic;

import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.cachehw.HwListener;
import ru.sinvic.cachehw.MyCache;
import ru.sinvic.core.repository.executor.DbExecutorImpl;
import ru.sinvic.core.sessionmanager.TransactionRunnerJdbc;
import ru.sinvic.crm.datasource.DriverManagerDataSource;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.service.DbServiceClientImpl;
import ru.sinvic.jdbc.mapper.DataTemplateJdbc;
import ru.sinvic.jdbc.mapper.EntityClassMetaData;
import ru.sinvic.jdbc.mapper.EntitySQLMetaData;
import ru.sinvic.jdbc.mapper.InstanceHelper;
import ru.sinvic.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.sinvic.jdbc.mapper.metadata.EntitySQLMetaDataImpl;

@SuppressWarnings({"java:S125", "java:S1481", "java:S1215"})
public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        // Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        // Работа с клиентом
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<Client>(
                dbExecutor,
                entitySQLMetaDataClient,
                new InstanceHelper<>(entityClassMetaDataClient)); // реализация DataTemplate, универсальная
        var cache = new MyCache<Long, Client>();
        HwListener<Long, Client> listener = new HwListener<Long, Client>() {
            @Override
            public void notify(Long key, Client value, String action) {
                log.info("key:{}, value:{}, action: {}", key, value, action);
            }
        };
        cache.addListener(listener);
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient, cache);

        List<Client> clientList = createClients();
        saveClients(clientList, dbServiceClient);
        getClientsInLoop(clientList, dbServiceClient);
        System.gc();
        log.info("Итоговый размер кэша: {}", cache.size());

        // ИТОГ запуска: [main] INFO ru.sinvic.HomeWork -- Итоговый размер кэша: 127
        // Видим, что потерлись все значения, кроме 127, которые закешировались в кеше логнгов через внутренние
        // механизмы java
    }

    private static void saveClients(List<Client> clientList, DbServiceClientImpl dbServiceClient) {
        for (Client client : clientList) {
            dbServiceClient.saveClient(client);
        }
    }

    private static List<Client> createClients() {
        Random random = new Random();
        return LongStream.rangeClosed(1, 100_000)
                .mapToObj(id -> new Client(generateRandomName(random), random.nextInt(18, 80)))
                .toList();
    }

    private static String generateRandomName(Random random) {
        int len = random.nextInt(5, 11);
        char[] chars = new char[len];
        for (int i = 0; i < len; ++i) {
            chars[i] = (char) ('a' + random.nextInt(26));
        }
        return new String(chars);
    }

    private static void getClientsInLoop(List<Client> clientList, DbServiceClientImpl dbServiceClient) {
        for (int i = 1; i < clientList.size() + 1; i++) {
            int finalI = i;
            dbServiceClient.getClient(finalI).orElseThrow(() -> new RuntimeException("Client not found, id:" + finalI));
        }
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db.migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
