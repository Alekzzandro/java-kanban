package service;

import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().toUpperCase().equals("GET")) {
            HttpTaskServer.sendResponse(exchange, "{\"error\": \"Unsupported method\"}", 405);
            return;
        }

        List<Task> prioritizedTasks = ((InMemoryTaskManager) taskManager).getPrioritizedTasks();
        String jsonResponse = gson.toJson(prioritizedTasks);
        HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
    }
}