import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    void testAddNewTaskAndSaveToFile() throws Exception {
        Task task = new Task(1, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");

        assertEquals(task.getId(), savedTask.getId(), "ID задачи не совпадает.");
        assertEquals(task.getTitle(), savedTask.getTitle(), "Название задачи не совпадает.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи не совпадает.");
        assertEquals(task.getTaskType(), savedTask.getTaskType(), "Тип задачи не совпадает.");
    }

    @Test
    void testDeleteTaskAndSaveToFile() throws Exception {
        Task task = new Task(6, "Test Task", "Description", Status.NEW, TaskTypes.TASK);
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        Task deletedTask = taskManager.getTaskById(task.getId());
        assertNull(deletedTask, "Задача не удалена.");
    }

    @Test
    void testDeleteEpicAndSaveToFile() throws Exception {
        Epic epic = new Epic(7, "Test Epic", "Epic Description", Status.NEW, TaskTypes.EPIC);
        taskManager.createEpic(epic);

        taskManager.deleteEpic(epic.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        Epic deletedEpic = (Epic) taskManager.getTaskById(epic.getId());
        assertNull(deletedEpic, "Эпик не удален.");
    }

    @Test
    void testDeleteSubTaskAndSaveToFile() throws Exception {
        Epic epic = new Epic(8, "Test Epic", "Epic Description", Status.NEW, TaskTypes.EPIC);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(9, "Test SubTask", "SubTask Description", epic.getId(), Status.NEW, TaskTypes.SUBTASK);
        taskManager.createSubTask(subTask);

        taskManager.deleteSubTask(subTask.getId());

        taskManager = (FileBackedTaskManager) Managers.loadFromFile(filePath);

        SubTask deletedSubTask = (SubTask) taskManager.getTaskById(subTask.getId());
        assertNull(deletedSubTask, "Подзадача не удалена.");
    }
}