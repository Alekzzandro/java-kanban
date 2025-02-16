package service;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Resource not found\"}", 404);
    }

    protected void sendConflict(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Task overlaps with existing tasks\"}", 406);
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Internal Server Error\"}", 500);
    }
}