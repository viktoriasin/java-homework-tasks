package ru.sinvic.servlet;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import ru.sinvic.core.sessionmanager.DataBaseOperationException;
import ru.sinvic.crm.domain.Client;
import ru.sinvic.crm.service.DBServiceClient;

public class ClientApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DBServiceClient dbServiceClient;
    private final transient Gson gson;

    public ClientApiServlet(Gson gson, DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    private static String extractBodyContentFromRequest(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getServletContext().log("Received GET request for client with ID " + extractIdFromRequest(request));
        Client client = dbServiceClient.getClient(extractIdFromRequest(request)).orElse(null);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(client));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getServletContext().log("Received POST request to create/update clientFromPostRequest");
        try {
            String jsonString = extractBodyContentFromRequest(request);

            Client clientFromPostRequest = gson.fromJson(jsonString, Client.class);

            Client savedClient = dbServiceClient.saveClient(clientFromPostRequest);
            getServletContext().log("Client saved to database");

            sendSuccessResponse(response, savedClient);
        } catch (DataBaseOperationException e) {
            handleErrorResponse(response, "Error saving client to database", e);
        } catch (JsonSyntaxException e) {
            handleErrorResponse(response, "Error parsing JSON string", e);
        } catch (JsonIOException e) {
            handleErrorResponse(response, "Error converting object to JSON", e);
        } catch (Exception e) {
            handleErrorResponse(response, "General error processing POST request", e);
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, Client savedClient) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(savedClient));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }

    private void handleErrorResponse(HttpServletResponse response, String message, Throwable e) {
        getServletContext().log(message, e);
        response.setStatus(SC_INTERNAL_SERVER_ERROR);
    }
}
