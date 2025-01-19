package service;

import exception.ManagerLoadFileException;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {

    public static TaskManager getFileBackedTaskManager(Path path) throws IOException, ManagerLoadFileException {
        return new FileBackedTaskManager(path);
    }

    public static TaskManager loadFromFile(Path path) throws ManagerLoadFileException {
        return FileBackedTaskManager.loadFromFile(path);
    }
}
