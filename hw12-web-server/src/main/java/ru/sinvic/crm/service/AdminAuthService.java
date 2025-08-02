package ru.sinvic.crm.service;

public class AdminAuthService implements ClientAuthService {

    private final String adminLogin;
    private final String adminPassword;

    public AdminAuthService(String adminLogin, String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }

    @Override
    public boolean authenticate(String login, String password) {
        return login.equals(adminLogin) && password.equals(adminPassword);
    }
}
