package test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddToHistory() {
        Task task = new Task(1, "Test Task", "Description", Status.NEW);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать одну задачу.");
    }
}