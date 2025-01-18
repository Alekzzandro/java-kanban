import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddNewTask() throws Exception {
        Task task = new Task(1, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getId(), savedTask.getId(), "ID задачи не совпадает.");
        assertEquals(task.getTitle(), savedTask.getTitle(), "Название задачи не совпадает.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи не совпадает.");
        assertEquals(task.getTaskType(), savedTask.getTaskType(), "Тип задачи не совпадает.");
    }

    @Test
    void testTaskHistory() throws Exception {
        Task task = new Task(5, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");

        Task historyTask = history.get(0);
        assertEquals(task.getId(), historyTask.getId(), "ID задачи в истории не совпадает.");
        assertEquals(task.getTitle(), historyTask.getTitle(), "Название задачи в истории не совпадает.");
        assertEquals(task.getDescription(), historyTask.getDescription(), "Описание задачи в истории не совпадает.");
        assertEquals(task.getStatus(), historyTask.getStatus(), "Статус задачи в истории не совпадает.");
        assertEquals(task.getTaskType(), historyTask.getTaskType(), "Тип задачи в истории не совпадает.");
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        Task task = new Task(6, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(updatedTask, "Обновленная задача не найдена.");

        assertEquals(Status.DONE, updatedTask.getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void testDeleteTask() throws Exception {
        Task task = new Task(7, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());

        Task deletedTask = taskManager.getTaskById(task.getId());
        assertNull(deletedTask, "Задача не удалена.");
    }

    @Test
    void testDeleteAllTasks() throws Exception {
        Task task1 = new Task(8, "Test Task 1", "Description 1", Status.NEW, TaskTypes.TASK);
        Task task2 = new Task(9, "Test Task 2", "Description 2", Status.NEW, TaskTypes.TASK);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();

        Task deletedTask1 = taskManager.getTaskById(task1.getId());
        Task deletedTask2 = taskManager.getTaskById(task2.getId());

        assertNull(deletedTask1, "Первая задача не удалена.");
        assertNull(deletedTask2, "Вторая задача не удалена.");
    }
}