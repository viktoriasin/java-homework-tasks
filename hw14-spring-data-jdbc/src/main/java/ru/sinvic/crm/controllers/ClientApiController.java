package ru.sinvic.crm.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.crm.domain.Address;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.domain.Phone;
import ru.sinvic.crm.dto.ClientCreateDto;
import ru.sinvic.crm.service.DBServiceClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientApiController {

    private static final Logger log = LoggerFactory.getLogger(ClientApiController.class);

    private final DBServiceClient dbServiceClient;

    @GetMapping({"/api/client/{id}"})
    protected ClientCreateDto getClientById(@PathVariable(name = "id") long id) {
        log.info("Received GET request for client with ID {}", id);
        Client client = dbServiceClient.getClient(id);
        Long clientId = client.id();
        Address address = dbServiceClient.getClientAddress(clientId);
        Phone phone = dbServiceClient.getClientPhone(clientId);
        return new ClientCreateDto(clientId, client.name(), address.street(), phone.number());
    }

    @PostMapping("/api/client")
    protected Client clientSave(@RequestBody ClientCreateDto clientCreateDto) {
        log.info("Received POST request to create/update clientFromPostRequest {}", clientCreateDto);
        return dbServiceClient.saveClientWithProfileInfo(
                clientCreateDto.getName(), clientCreateDto.getStreet(), clientCreateDto.getPhoneNumber());
    }

    @GetMapping({"/api/clients"})
    protected List<ClientCreateDto> getClients() {
        log.info("Received GET request to show all clients");
        return dbServiceClient.findAllWithProfileInfo();
    }
}
