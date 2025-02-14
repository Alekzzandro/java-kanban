import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicCanAddValidSubTask() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW);

        epic.addSubTask(2);

        assertTrue(epic.getSubTaskIds().contains(2), "Подзадача не была добавлена.");
    }
}