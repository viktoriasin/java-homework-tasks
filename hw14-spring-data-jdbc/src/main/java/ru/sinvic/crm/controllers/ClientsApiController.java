package ru.sinvic.crm.controllers;

import jakarta.servlet.http.HttpServlet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.service.DBServiceClient;

@RestController
public class ClientsApiController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ClientsApiController.class);
    private final transient DBServiceClient dbServiceClient;

    public ClientsApiController(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @GetMapping({"/api/clients"})
    protected List<Client> getClients() {
        log.info("Received GET request to show all clients");
        return dbServiceClient.findAll();
    }
}
