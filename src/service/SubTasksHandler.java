package service;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.SubTask;
import model.Epic;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class SubTasksHandler extends BaseHttpHandler {
    public SubTasksHandler(TaskManager taskManager) {
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
        if (path.equals("/subtasks")) {
            List<SubTask> subTasks = taskManager.getSubTasks();
            String jsonResponse = gson.toJson(subTasks);
            HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
        } else {
            try {
                int subTaskId = Integer.parseInt(path.substring("/subtasks/".length()));
                SubTask subTask = taskManager.getSubTaskById(subTaskId);
                if (subTask == null) {
                    throw new NotFoundException("Подзадача с ID " + subTaskId + " не найдена");
                }
                String jsonResponse = gson.toJson(subTask);
                HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Type subTaskType = new TypeToken<SubTask>() {
            }.getType();
            SubTask subTask = gson.fromJson(body, subTaskType);

            if (subTask == null) {
                throw new IllegalArgumentException("Неверный формат данных подзадачи");
            }

            if (subTask.getId() == 0) {
                Epic epic = taskManager.getEpicById(subTask.getEpicId());
                if (epic == null) {
                    throw new IllegalArgumentException("Эпик с ID " + subTask.getEpicId() + " не найден");
                }
                taskManager.createSubTask(subTask);
                HttpTaskServer.sendResponse(exchange, "", 201);
            } else {
                if (!taskManager.updateSubTask(subTask)) {
                    throw new NotFoundException("Подзадача с ID " + subTask.getId() + " не найдена");
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
        if (path.equals("/subtasks")) {
            taskManager.deleteAllSubTasks();
            HttpTaskServer.sendResponse(exchange, "", 200);
        } else {
            try {
                int subTaskId = Integer.parseInt(path.substring("/subtasks/".length()));
                SubTask subTask = taskManager.getSubTaskById(subTaskId);
                if (subTask == null) {
                    throw new NotFoundException("Подзадача с ID " + subTaskId + " не найдена");
                }
                taskManager.deleteSubTask(subTaskId);
                HttpTaskServer.sendResponse(exchange, "", 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }
}