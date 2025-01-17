package service;

import exception.ManagerLoadFileException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    Task createTask(Task task) throws ManagerLoadFileException;

    Epic createEpic(Epic epic) throws ManagerLoadFileException;

    SubTask createSubTask(SubTask subTask) throws ManagerLoadFileException;

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

    void deleteTask(int taskId) throws ManagerLoadFileException;

    void deleteEpic(int epicId) throws ManagerLoadFileException;

    void deleteSubTask(int subTaskId) throws ManagerLoadFileException;

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    List<Task> getHistory();
}
