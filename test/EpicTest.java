import model.Epic;
import model.Status;
import model.SubTask;
import model.TaskTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicCannotAddSelfAsSubTask() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW, TaskTypes.EPIC);

        SubTask subTask = new SubTask(1, "SubTask 1", "Description", epic.getId(), Status.NEW, TaskTypes.SUBTASK);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubTask(subTask);
        });

        assertEquals("Эпик не может добавлять себя как подзадачу", exception.getMessage());
    }
}