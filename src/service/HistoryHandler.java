package service;

import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().toUpperCase().equals("GET")) {
            HttpTaskServer.sendResponse(exchange, "{\"error\": \"Unsupported method\"}", 405);
            return;
        }

        List<Task> history = taskManager.getHistory();
        String jsonResponse = gson.toJson(history);
        HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
    }
}