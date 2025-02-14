import model.Status;
import model.Task;
import model.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import java.util.List;

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

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу.");

        assertEquals(task, history.get(0), "Первая задача в истории должна быть той, которую мы добавили.");

        assertEquals(TaskTypes.TASK, task.getTaskType(), "Тип задачи должен быть TASK.");
    }
}