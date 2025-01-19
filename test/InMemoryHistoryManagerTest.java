import model.Status;
import model.Task;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndRetrieveHistory() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void removeTaskFromHistory() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void reAddTaskUpdatesPosition() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }
}
