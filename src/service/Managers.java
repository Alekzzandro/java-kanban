package service;

import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskTypes;
import exception.ManagerLoadFileException;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTaskManager(Path path) throws IOException, ManagerLoadFileException {
        return new FileBackedTaskManager(path);
    }

    public static TaskManager loadFromFile(Path path) throws ManagerLoadFileException {
        return FileBackedTaskManager.loadFromFile(path);
    }
}