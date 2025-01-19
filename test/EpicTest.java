import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicCannotAddSelfAsSubTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW, Duration.ZERO, now, taskManager);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubTask(epic.getId());
        });
        assertEquals("Эпик не может добавлять себя как подзадачу", exception.getMessage());
    }


    @Test
    void testEpicToString() {
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1", "Epic Description", Status.NEW, Duration.ofHours(5), now, null);
        String expectedString = "Epic{id=1, title='Epic 1', description='Epic Description', status=NEW, duration=PT5H, startTime=" + now + ", endTime=" + now.plusHours(5) + "}";
        assertEquals(expectedString, epic.toString().replace("null", "Optional.empty"), "Строковое представление эпика должно быть корректным");
    }
}