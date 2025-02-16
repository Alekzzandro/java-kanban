import service.*;

import com.google.gson.Gson;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusHours(1));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа при создании задачи");

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Task", tasksFromManager.get(0).getTitle(), "Некорректное название задачи");

        assertNotNull(tasksFromManager.get(0).getDuration(), "Продолжительность задачи не установлена");
        assertNotNull(tasksFromManager.get(0).getStartTime(), "Время начала задачи не установлено");
    }

    @Test
    public void testGetTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа при получении задач");

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new com.google.gson.reflect.TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(tasksFromResponse, "Задачи не возвращаются в ответе");
        assertEquals(1, tasksFromResponse.size(), "Некорректное количество задач в ответе");
        assertEquals("Test Task", tasksFromResponse.get(0).getTitle(), "Некорректное название задачи в ответе");
    }

    @Test
    public void testGetTaskById() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа при получении задачи по ID");

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не возвращается в ответе");
        assertEquals(task.getId(), taskFromResponse.getId(), "Некорректное ID задачи в ответе");
        assertEquals("Test Task", taskFromResponse.getTitle(), "Некорректное название задачи в ответе");
    }

    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный код ответа при получении несуществующей задачи по ID");

        assertEquals("{\"error\": \"Resource not found\"}", response.body(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void testDeleteTaskById() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа при удалении задачи по ID");

        Task deletedTask = manager.getTaskById(task.getId());
        assertNull(deletedTask, "Задача не удалена");
    }

    @Test
    public void testDeleteTaskByIdNotFound() throws Exception {
        // Создаем HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный код ответа при удалении несуществующей задачи по ID");

        assertEquals("{\"error\": \"Resource not found\"}", response.body(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createTask(task);

        task.setTitle("Updated Task");
        task.setStatus(Status.DONE);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/" + task.getId()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа при обновлении задачи");

        Task updatedTask = manager.getTaskById(task.getId());
        assertNotNull(updatedTask, "Задача не найдена");
        assertEquals("Updated Task", updatedTask.getTitle(), "Некорректное название задачи после обновления");
        assertEquals(Status.DONE, updatedTask.getStatus(), "Некорректный статус задачи после обновления");
    }

    @Test
    public void testAddTaskOverlap() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        manager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Status.NEW);
        task2.setDuration(Duration.ofMinutes(45));
        task2.setStartTime(LocalDateTime.now().plusHours(1).plusMinutes(15));
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Неверный код ответа при создании пересекающейся задачи");

        assertEquals("{\"error\": \"Task overlaps with existing tasks\"}", response.body(), "Некорректное сообщение об ошибке");
    }
}