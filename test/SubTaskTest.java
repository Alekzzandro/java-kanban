import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest {
    @Test
    void testSubTaskNotEqual() {
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask1 = new SubTask(1, "SubTask 1", "Description", Status.NEW, 1, Duration.ofMinutes(30), now);
        SubTask subTask2 = new SubTask(2, "SubTask 2", "Different Description", Status.IN_PROGRESS, 2, Duration.ofMinutes(45), now.plusMinutes(30));
        assertNotEquals(subTask1, subTask2, "Подзадачи должны быть неравны");
    }

    @Test
    void testSubTaskToString() {
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask = new SubTask(1, "SubTask 1", "Description", Status.NEW, 1, Duration.ofMinutes(30), now);
        String expectedString = "SubTask{id=1, title='SubTask 1', description='Description', status=NEW, epicId=1, duration=PT30M, startTime=" + now + ", endTime=" + now.plusMinutes(30) + "}";
        assertEquals(expectedString, subTask.toString().replace("null", "Optional.empty"), "Строковое представление подзадачи должно быть корректным");
    }

    @Test
    void testSubTaskGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask = new SubTask(1, "SubTask 1", "Description", Status.NEW, 1, Duration.ofMinutes(30), now);

        assertEquals(1, subTask.getId());
        assertEquals("SubTask 1", subTask.getTitle());
        assertEquals("Description", subTask.getDescription());
        assertEquals(Status.NEW, subTask.getStatus());
        assertEquals(1, subTask.getEpicId());
        assertEquals(Duration.ofMinutes(30), subTask.getDuration());
        assertEquals(now, subTask.getStartTime());
        assertEquals(now.plusMinutes(30), subTask.getEndTime());

        subTask.setId(2);
        subTask.setTitle("Updated Title");
        subTask.setDescription("Updated Description");
        subTask.setStatus(Status.DONE);
        subTask.setEpicId(2);
        subTask.setDuration(Duration.ofMinutes(60));
        LocalDateTime updatedTime = now.plusHours(1);
        subTask.setStartTime(updatedTime);
        subTask.setEndTime(updatedTime.plusMinutes(60));

        assertEquals(2, subTask.getId());
        assertEquals("Updated Title", subTask.getTitle());
        assertEquals("Updated Description", subTask.getDescription());
        assertEquals(Status.DONE, subTask.getStatus());
        assertEquals(2, subTask.getEpicId());
        assertEquals(Duration.ofMinutes(60), subTask.getDuration());
        assertEquals(updatedTime, subTask.getStartTime());
        assertEquals(updatedTime.plusMinutes(60), subTask.getEndTime());
    }
}