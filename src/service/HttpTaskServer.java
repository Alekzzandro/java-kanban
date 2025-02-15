package service;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exception.ManagerLoadFileException;

import java.nio.file.Paths;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.err.println("Не удалось создать сервер: " + e.getMessage());
            return;
        }

        Map<String, HttpHandler> handlers = new HashMap<>();
        handlers.put("/tasks", new TasksHandler(taskManager));
        handlers.put("/subtasks", new SubTasksHandler(taskManager));
        handlers.put("/epics", new EpicsHandler(taskManager));
        handlers.put("/history", new HistoryHandler(taskManager));
        handlers.put("/prioritized", new PrioritizedHandler(taskManager));

        for (Map.Entry<String, HttpHandler> entry : handlers.entrySet()) {
            server.createContext(entry.getKey(), entry.getValue());
        }

        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер остановлен");
        }
    }

    protected static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

    protected static void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Resource not found\"}", 404);
    }

    protected static void sendConflict(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Task overlaps with existing tasks\"}", 406);
    }

    protected static void sendInternalServerError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"error\": \"Internal Server Error\"}", 500);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public static void main(String[] args) {
        TaskManager taskManager = null;
        try {
            taskManager = Managers.getFileBackedTaskManager(Paths.get("tasks.csv"));
        } catch (IOException | ManagerLoadFileException e) {
            System.err.println("Ошибка при создании менеджера задач: " + e.getMessage());
            return;
        }

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}