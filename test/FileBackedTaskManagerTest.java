import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private final Path filePath = Paths.get("test_tasks.csv");

    @BeforeEach
    void setUp() throws IOException, exception.ManagerLoadFileException {
        Files.deleteIfExists(filePath);
        taskManager = (FileBackedTaskManager) Managers.getFileBackedTaskManager(filePath);
    }

    @Test
    void testAddAndRemoveSubTask() throws Exception {
        Epic epic = new Epic(1, "Epic 1", "Epic Description", Status.NEW, Duration.ofHours(2), LocalDateTime.now(), taskManager);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(2, "SubTask 1", "SubTask Description", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubTask(subTask);

        assertEquals(1, epic.getSubTaskIds().size(), "Эпик должен содержать одну подзадачу.");

        epic.removeSubTask(subTask.getId());
        assertEquals(0, epic.getSubTaskIds().size(), "Эпик не был обновлен после удаления подзадачи.");
    }
}
