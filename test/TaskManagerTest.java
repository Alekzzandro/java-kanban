import model.*;
import org.junit.jupiter.api.*;
import service.TaskManagerImplement;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManagerImplement> {
    protected T taskManager;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    public void testCreateTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(0, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), startTime);
        task = taskManager.createTask(task);
        assertNotNull(task);
        assertEquals("Task 1", task.getTitle());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(30), task.getDuration());
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    public void testCreateEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(0, "Epic 1", "Epic description", Status.NEW, Duration.ofHours(5), startTime, taskManager);
        epic = taskManager.createEpic(epic);
        assertNotNull(epic);
        assertEquals("Epic 1", epic.getTitle());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Duration.ofHours(5), epic.getDuration());
        assertEquals(startTime, epic.getStartTime());
    }

    @Test
    public void testCreateSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = taskManager.createEpic(new Epic(0, "Epic 1", "Epic description", Status.NEW, Duration.ofHours(5), startTime, taskManager));
        SubTask subTask = new SubTask(0, "SubTask 1", "SubTask description", Status.NEW, epic.getId(), Duration.ofHours(2), startTime);
        subTask = taskManager.createSubTask(subTask);
        assertNotNull(subTask);
        assertEquals("SubTask 1", subTask.getTitle());
        assertEquals(Status.NEW, subTask.getStatus());
        assertEquals(Duration.ofHours(2), subTask.getDuration());
        assertEquals(startTime, subTask.getStartTime());
    }

    @Test
    public void testHistoryManagerEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryManagerWithDuplicates() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = taskManager.createTask(new Task(0, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), startTime));
        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testNoExceptionOnValidFile() {
        assertDoesNotThrow(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            Task task = new Task(0, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), startTime);
            taskManager.createTask(task);
        });
    }
}