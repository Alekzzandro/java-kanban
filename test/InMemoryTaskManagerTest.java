import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void testGetPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = taskManager.createTask(new Task(0, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), now));
        Task task2 = taskManager.createTask(new Task(0, "Task 2", "Description", Status.NEW, Duration.ofMinutes(45), now.plusMinutes(35)));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task1, prioritizedTasks.get(0));
        assertEquals(task2, prioritizedTasks.get(1));
    }

    @Test
    public void testUpdateTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(0, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), startTime);
        taskManager.createTask(task);
        task.setTitle("Updated Task");
        task.setStatus(Status.DONE);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(startTime.plusMinutes(10));
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals(Status.DONE, updatedTask.getStatus());
        assertEquals(Duration.ofMinutes(60), updatedTask.getDuration());
        assertEquals(startTime.plusMinutes(10), updatedTask.getStartTime());
    }

    @Test
    public void testCreateTaskWithUniqueIds() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = taskManager.createTask(new Task(1, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), now));
        Task task2 = taskManager.createTask(new Task(2, "Task 2", "Description", Status.NEW, Duration.ofMinutes(45), now.plusMinutes(35)));
        assertNotNull(task1);
        assertNotNull(task2);
        assertEquals(2, taskManager.getTasks().size());
    }


    @Test
    public void testHistoryManagerEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryManagerWithDuplicates() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = taskManager.createTask(new Task(1, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), now));
        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size());
    }


    @Test
    public void testNoExceptionOnValidFile() {
        assertDoesNotThrow(() -> {
            LocalDateTime now = LocalDateTime.now();
            Task task = new Task(1, "Task 1", "Description", Status.NEW, Duration.ofMinutes(30), now);
            taskManager.createTask(task);
        });
    }
}