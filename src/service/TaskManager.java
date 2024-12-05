package service;
import model.Epic;
import model.SubTask;
import model.Task;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);
    Epic createEpic(Epic epic);
    SubTask createSubTask(SubTask subTask);
    boolean updateTask(Task task);
    boolean updateEpic(Epic epic);
    boolean updateSubTask(SubTask subTask);
    Task getTaskById(int taskId);
    Epic getEpicById(int epicId);
    SubTask getSubTaskById(int subTaskId);
    List<Task> getTasks();
    List<Epic> getEpics();
    List<SubTask> getSubTasks();
    List<SubTask> getSubTasksByEpic(int epicId);
    void deleteTask(int taskId);
    void deleteEpic(int epicId);
    void deleteSubTask(int subTaskId);
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubTasks();
    void addTask(Task task);
    void removeTask(int id);
    Task getTask(int id);
    List<Task> getAllTasks();
    List<Task> getHistory();
}