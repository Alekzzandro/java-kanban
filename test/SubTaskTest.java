package test;

import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testSubTaskEquality() {
        SubTask subTask1 = new SubTask(1, "SubTask 1", "Description", 1, Status.NEW);
        SubTask subTask2 = new SubTask(1, "SubTask 1", "Description", 1, Status.NEW);

        assertEquals(subTask1, subTask2, "Подзадачи должны быть равны по содержимому");
    }
}