package ru.sinvic.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.LoggerFactory;
import ru.sinvic.core.repository.DataTemplateHibernate;
import ru.sinvic.core.repository.HibernateUtils;
import ru.sinvic.core.sessionmanager.TransactionManagerHibernate;
import ru.sinvic.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.sinvic.crm.model.Address;
import ru.sinvic.crm.model.Client;
import ru.sinvic.crm.model.Phone;
import ru.sinvic.crm.service.AdminAuthService;
import ru.sinvic.crm.service.DbServiceClientImpl;
import ru.sinvic.helpers.FileSystemHelper;
import ru.sinvic.server.ClientWebServer;
import ru.sinvic.server.ClientWebServerImpl;

public class WebServer {

    public static final String PROPERTIES_FILE_PATH = "config.properties";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    static {
        LoggerFactory.getLogger(WebServer.class);
    }

    private static final int WEB_SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {

        ///
        var configuration = createHibernateConfiguration();
        makeMigrations(configuration);
        var sessionFactory = createHibernateSessionFactory(configuration);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        ///

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        var properties = getPropertiesFile();
        var username = properties.getProperty("admin.username");
        var password = properties.getProperty("admin.password");
        var authService = new AdminAuthService(username, password);

        ClientWebServer clientWebServer = new ClientWebServerImpl(WEB_SERVER_PORT, dbServiceClient, gson, authService);

        clientWebServer.start();
        clientWebServer.join();
    }

    private static Configuration createHibernateConfiguration() {
        return new Configuration().configure(HIBERNATE_CFG_FILE);
    }

    private static void makeMigrations(Configuration configuration) {
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }

    private static SessionFactory createHibernateSessionFactory(Configuration configuration) {
        return HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
    }

    private static Properties getPropertiesFile() {
        Properties properties = new Properties();
        InputStream resourceAsStream = FileSystemHelper.getResourceAsStream(PROPERTIES_FILE_PATH);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Resource \"%s\" could not be load", PROPERTIES_FILE_PATH));
        }
        return properties;
    }
}
