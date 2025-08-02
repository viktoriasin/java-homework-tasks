package ru.sinvic.crm.service;

public interface ClientAuthService {
    boolean authenticate(String login, String password);
}
