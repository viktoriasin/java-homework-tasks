package ru.sinvic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.cachehw.MyCache;
import ru.sinvic.core.repository.executor.DbExecutorImpl;
import ru.sinvic.core.sessionmanager.TransactionRunnerJdbc;
import ru.sinvic.crm.datasource.DriverManagerDataSource;
import ru.sinvic.crm.model.Client;
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
        var cache = new MyCache<Long, WeakReference<Client>>();

        List<Client> clientList = new ArrayList<>(List.of(
                new Client("dbServiceFirst", 20),
                new Client("dbServiceSecond", 30),
                new Client("dbServiceThird", 40),
                new Client("dbServiceFourth", 50),
                new Client("Ivan", 60)));
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient, cache);
        clientList.replaceAll(dbServiceClient::saveClient);

        // сначала загружаем клиентов из базы (смотрим вывод логов)
        getClientsInLoop(clientList, dbServiceClient);
        // при повторном обращении должны загрузить всех из кэша (смотрим вывод логов)
        getClientsInLoop(clientList, dbServiceClient);
        // явно вызываем сборку мусора
        System.gc();
        // теперь должна быть опять загрузка из базы (смотрим вывод логов)
        getClientsInLoop(clientList, dbServiceClient);
    }

    private static void getClientsInLoop(List<Client> clientList, DbServiceClientImpl dbServiceClient) {
        for (int i = 0; i < clientList.size(); i++) {
            int finalI = i;
            dbServiceClient
                    .getClient(clientList.get(i).getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Client not found, id:" + clientList.get(finalI).getId()));
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
