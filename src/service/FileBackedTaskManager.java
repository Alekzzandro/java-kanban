package service;

import exception.ManagerLoadFileException;
import model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(Path path) throws IOException, ManagerLoadFileException {
        this.path = path;
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        load();
    }

    private void save() throws ManagerLoadFileException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bw.write("id,type,name,status,description,epic");
            bw.newLine();
            for (Task task : getTasks()) {
                bw.write(taskToString(task));
                bw.newLine();
            }
            for (Epic epic : getEpics()) {
                bw.write(taskToString(epic));
                bw.newLine();
            }
            for (SubTask subTask : getSubTasks()) {
                bw.write(taskToString(subTask));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerLoadFileException("Ошибка сохранения задач в файл", e);
        }
    }

    private String taskToString(Task task) {
        String epicId = task.getTaskType() == TaskTypes.SUBTASK ? String.valueOf(((SubTask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTaskType().name(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }

    private Task taskFromString(String value) throws ManagerLoadFileException {
        String[] fields = value.split(",");
        try {
            int id = Integer.parseInt(fields[0]);
            TaskTypes type = TaskTypes.valueOf(fields[1]);
            String title = fields[2];
            Status status = Status.valueOf(fields[3]);
            String description = fields[4];
            switch (type) {
                case TASK:
                    return new Task(id, title, description, status);
                case EPIC:
                    return new Epic(id, title, description, status);
                case SUBTASK:
                    int epicId = Integer.parseInt(fields[5]);
                    return new SubTask(id, title, description, epicId, status);
                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            }
        } catch (Exception e) {
            throw new ManagerLoadFileException("Ошибка при чтении строки задачи: " + value, e);
        }
    }

    private void load() throws ManagerLoadFileException {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                Task task = taskFromString(line);
                if (task instanceof SubTask) {
                    addSubTaskToStorage((SubTask) task);
                } else if (task instanceof Epic) {
                    addEpicToStorage((Epic) task);
                } else {
                    addTaskToStorage(task);
                }
                maxId = Math.max(maxId, task.getId());
            }
            setNextId(maxId + 1);
        } catch (IOException e) {
            throw new ManagerLoadFileException("Ошибка загрузки данных из файла", e);
        }
    }

    @Override
    public Task createTask(Task task) throws ManagerLoadFileException {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerLoadFileException {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws ManagerLoadFileException {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void deleteTask(int taskId) throws ManagerLoadFileException {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) throws ManagerLoadFileException {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubTask(int subTaskId) throws ManagerLoadFileException {
        super.deleteSubTask(subTaskId);
        save();
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws ManagerLoadFileException {
        try {
            return new FileBackedTaskManager(path);
        } catch (IOException | ManagerLoadFileException e) {
            throw new ManagerLoadFileException("Ошибка загрузки данных из файла", e);
        }
    }
}
