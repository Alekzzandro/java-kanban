import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testSubTaskEquality() {
        SubTask subTask1 = new SubTask(1, "SubTask 1", "Description", Status.NEW, 1);
        SubTask subTask2 = new SubTask(1, "SubTask 1", "Description", Status.NEW, 1);

        assertEquals(subTask1.getId(), subTask2.getId(), "ID подзадач должны совпадать");
        assertEquals(subTask1.getTitle(), subTask2.getTitle(), "Названия подзадач должны совпадать");
        assertEquals(subTask1.getDescription(), subTask2.getDescription(), "Описания подзадач должны совпадать");
        assertEquals(subTask1.getStatus(), subTask2.getStatus(), "Статусы подзадач должны совпадать");
        assertEquals(subTask1.getEpicId(), subTask2.getEpicId(), "ID эпиков подзадач должны совпадать");

        SubTask subTask3 = new SubTask(2, "SubTask 1", "Description", Status.NEW, 1);
        assertNotEquals(subTask1.getId(), subTask3.getId(), "ID подзадач не должны совпадать");

        SubTask subTask4 = new SubTask(1, "SubTask 1", "Description", Status.NEW, 2);
        assertNotEquals(subTask1.getEpicId(), subTask4.getEpicId(), "ID эпиков подзадач не должны совпадать");
    }
}