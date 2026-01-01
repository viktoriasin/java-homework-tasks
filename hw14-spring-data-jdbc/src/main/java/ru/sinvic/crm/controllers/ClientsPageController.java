package ru.sinvic.crm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientsPageController {

    @GetMapping({"/", "/clients"})
    protected String clientsView() {
        return "clients";
    }
}
