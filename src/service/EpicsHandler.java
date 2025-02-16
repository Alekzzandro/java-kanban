package service;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.Epic;
import model.SubTask;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    HttpTaskServer.sendResponse(exchange, "{\"error\": \"Unsupported method\"}", 405);
            }
        } catch (IllegalArgumentException e) {
            HttpTaskServer.sendConflict(exchange);
        } catch (NotFoundException e) {
            HttpTaskServer.sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            e.printStackTrace();
            HttpTaskServer.sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getEpics();
            String jsonResponse = gson.toJson(epics);
            HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
        } else if (path.startsWith("/epics/") && path.endsWith("/subtasks")) {
            int epicId = Integer.parseInt(path.substring("/epics/".length(), path.length() - "/subtasks".length()));
            Epic epic = taskManager.getEpicById(epicId);
            if (epic == null) {
                throw new NotFoundException("Эпик с ID " + epicId + " не найден");
            }
            List<SubTask> subTasks = taskManager.getSubTasksByEpic(epicId);
            String jsonResponse = gson.toJson(subTasks);
            HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
        } else {
            try {
                int epicId = Integer.parseInt(path.substring("/epics/".length()));
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    throw new NotFoundException("Эпик с ID " + epicId + " не найден");
                }
                String jsonResponse = gson.toJson(epic);
                HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Type epicType = new TypeToken<Epic>() {
            }.getType();
            Epic epic = gson.fromJson(body, epicType);

            if (epic == null) {
                throw new IllegalArgumentException("Неверный формат данных эпика");
            }

            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                int newEpicId = epic.getId();
                HttpTaskServer.sendResponse(exchange, "{\"id\": " + newEpicId + "}", 201);
            } else {
                if (!taskManager.updateEpic(epic)) {
                    throw new NotFoundException("Эпик с ID " + epic.getId() + " не найден");
                }
                HttpTaskServer.sendResponse(exchange, "", 201);
            }
        } catch (JsonSyntaxException e) {
            HttpTaskServer.sendConflict(exchange);
        } catch (IllegalArgumentException e) {
            HttpTaskServer.sendConflict(exchange);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            e.printStackTrace();
            HttpTaskServer.sendInternalServerError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/epics")) {
            taskManager.deleteAllEpics();
            HttpTaskServer.sendResponse(exchange, "", 200);
        } else {
            try {
                int epicId = Integer.parseInt(path.substring("/epics/".length()));
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    throw new NotFoundException("Эпик с ID " + epicId + " не найден");
                }
                taskManager.deleteEpic(epicId);
                HttpTaskServer.sendResponse(exchange, "", 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }
}