package test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddNewTask() {
        Task task = new Task(0, "Test Task", "Description", Status.NEW);
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void testTaskHistory() {
        Task task = new Task(0, "Test Task", "Description", Status.NEW);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size(), "История должна содержать одну задачу.");
    }
}