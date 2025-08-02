package ru.sinvic.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.sinvic.crm.service.ClientAuthService;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AdminLoginServlet extends HttpServlet {

    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 30;

    private final transient ClientAuthService clientAuthService;

    public AdminLoginServlet(ClientAuthService clientAuthService) {
        this.clientAuthService = clientAuthService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html");
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter(PARAM_LOGIN);
        String password = request.getParameter(PARAM_PASSWORD);

        if (clientAuthService.authenticate(name, password)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            response.sendRedirect(request.getContextPath() + "/clients");
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }
}
