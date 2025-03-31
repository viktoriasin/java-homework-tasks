package ru.sinvic.crm.model;

import ru.sinvic.jdbc.annotations.GeneratedValue;
import ru.sinvic.jdbc.annotations.Id;

public class Client {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Client() {}

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
