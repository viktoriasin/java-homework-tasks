package ru.sinvic.crm.domain;

import ru.sinvic.jdbc.annotations.Id;

public class Client {
    @Id
    private Long id;

    private String name;

    private int age;

    public Client() {}

    public Client(String name, int age) {
        this.id = null;
        this.name = name;
        this.age = age;
    }

    public Client(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", age=" + age + '}';
    }
}
