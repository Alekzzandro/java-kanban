import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import service.FileBackedTaskManager;
import service.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private final Path filePath = Paths.get("test_tasks.csv");

    @BeforeEach
    void setUp() throws IOException, exception.ManagerLoadFileException {
        Files.deleteIfExists(filePath);
        taskManager = (FileBackedTaskManager) Managers.getFileBackedTaskManager(filePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(filePath);
    }

    @Test
    void testDeleteTaskAndLoad() throws Exception {
        Task task = new Task(6, "Test Task", "Description", Status.NEW);
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        assertNull(taskManager.getTaskById(task.getId()), "Задача не была удалена.");
    }

    @Test
    void testDeleteEpicAndLoad() throws Exception {
        Epic epic = new Epic(7, "Test Epic", "Epic Description", Status.NEW);
        taskManager.createEpic(epic);

        taskManager.deleteEpic(epic.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не был удален.");
    }

    @Test
    void testDeleteSubTaskAndLoad() throws Exception {
        Epic epic = new Epic(8, "Test Epic", "Epic Description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(9, "Test SubTask", "SubTask Description", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);

        taskManager.deleteSubTask(subTask.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        assertNull(taskManager.getSubTaskById(subTask.getId()), "Подзадача не была удалена.");
    }

    @Test
    void testEpicStatusUpdateAfterSubTaskDeletion() throws Exception {
        Epic epic = new Epic(10, "Test Epic", "Epic Description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(11, "Test SubTask", "SubTask Description", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);

        taskManager.deleteSubTask(subTask.getId());

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(updatedEpic, "Эпик не найден.");

        assertTrue(taskManager.getSubTasksByEpic(epic.getId()).isEmpty(), "Эпик содержит удаленную подзадачу.");

        assertEquals(Status.NEW, updatedEpic.getStatus(), "Статус эпика должен быть NEW после удаления всех подзадач.");
    }
}