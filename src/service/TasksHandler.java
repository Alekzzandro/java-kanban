package service;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.Task;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
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
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getTasks();
            String jsonResponse = gson.toJson(tasks);
            HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
        } else {
            try {
                int taskId = Integer.parseInt(path.substring("/tasks/".length()));
                Task task = taskManager.getTaskById(taskId);
                if (task == null) {
                    throw new NotFoundException("Задача с ID " + taskId + " не найдена");
                }
                String jsonResponse = gson.toJson(task);
                HttpTaskServer.sendResponse(exchange, jsonResponse, 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Type taskType = new TypeToken<Task>() {
            }.getType();
            Task task = gson.fromJson(body, taskType);

            if (task == null) {
                throw new IllegalArgumentException("Неверный формат данных задачи");
            }

            if (task.getId() == 0) {
                taskManager.createTask(task);
                int newTaskId = task.getId();
                HttpTaskServer.sendResponse(exchange, "{\"id\": " + newTaskId + "}", 201);
            } else {
                if (!taskManager.updateTask(task)) {
                    throw new NotFoundException("Задача с ID " + task.getId() + " не найдена");
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
        if (path.equals("/tasks")) {
            taskManager.deleteAllTasks();
            HttpTaskServer.sendResponse(exchange, "", 200);
        } else {
            try {
                int taskId = Integer.parseInt(path.substring("/tasks/".length()));
                Task task = taskManager.getTaskById(taskId);
                if (task == null) {
                    throw new NotFoundException("Задача с ID " + taskId + " не найдена");
                }
                taskManager.deleteTask(taskId);
                HttpTaskServer.sendResponse(exchange, "", 200);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                HttpTaskServer.sendNotFound(exchange);
            }
        }
    }
}