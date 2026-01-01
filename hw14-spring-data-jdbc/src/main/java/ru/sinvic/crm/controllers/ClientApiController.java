package ru.sinvic.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.dto.ClientCreateDto;
import ru.sinvic.crm.service.DBServiceClient;

@RestController
public class ClientApiController {

    private static final Logger log = LoggerFactory.getLogger(ClientApiController.class);

    private final DBServiceClient dbServiceClient;

    public ClientApiController(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @GetMapping({"/api/client/{id}"})
    protected Client getClientById(@PathVariable(name = "id") long id) {
        log.info("Received GET request for client with ID {}", id);
        return dbServiceClient.getClient(id).orElse(null);
    }

    @PostMapping("/api/client")
    protected Client clientSave(@RequestBody ClientCreateDto clientCreateDto) {
        log.info("Received POST request to create/update clientFromPostRequest " + clientCreateDto);
        return dbServiceClient.saveClientWithProfileInfo(
                clientCreateDto.getName(), clientCreateDto.getStreet(), clientCreateDto.getPhoneNumber());
    }
}
