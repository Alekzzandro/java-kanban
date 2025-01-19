package service;

import exception.ManagerLoadFileException;
import model.*;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public Task createTask(Task task) throws ManagerLoadFileException {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerLoadFileException {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws ManagerLoadFileException {
        super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public boolean updateTask(Task task) throws ManagerLoadFileException {
        boolean updated = super.updateTask(task);
        if (updated) {
            save();
        }
        return updated;
    }

    @Override
    public boolean updateEpic(Epic epic) throws ManagerLoadFileException {
        boolean updated = super.updateEpic(epic);
        if (updated) {
            save();
        }
        return updated;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) throws ManagerLoadFileException {
        boolean updated = super.updateSubTask(subTask);
        if (updated) {
            save();
        }
        return updated;
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

    @Override
    public void deleteAllTasks() throws ManagerLoadFileException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerLoadFileException {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() throws ManagerLoadFileException {
        super.deleteAllSubTasks();
        save();
    }

    public void save() throws ManagerLoadFileException {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            for (Task task : getTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subTask : getSubTasks()) {
                writer.write(taskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerLoadFileException("Error saving tasks to file", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws ManagerLoadFileException {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromString(line, manager);
                manager.addTaskToStorage(task);
            }
        } catch (IOException e) {
            throw new ManagerLoadFileException("Error loading tasks from file", e);
        }
        return manager;
    }

    private String taskToString(Task task) {
        String epicId = task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : "";
        TaskTypes type = TaskTypes.TASK;
        if (task instanceof Epic) {
            type = TaskTypes.EPIC;
        } else if (task instanceof SubTask) {
            type = TaskTypes.SUBTASK;
        }
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s",
                task.getId(),
                type,
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                task.getDuration().toMinutes(),
                task.getStartTime() != null ? task.getStartTime().toString() : "");
    }

    private static Task taskFromString(String value, TaskManager taskManager) throws ManagerLoadFileException {
        String[] fields = value.split(",");
        try {
            int id = Integer.parseInt(fields[0]);
            TaskTypes type = TaskTypes.valueOf(fields[1]);
            String title = fields[2];
            Status status = Status.valueOf(fields[3]);
            String description = fields[4];
            long durationMinutes = Long.parseLong(fields[6]);
            LocalDateTime startTime = fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7]);
            switch (type) {
                case TASK:
                    return new Task(id, title, description, status, Duration.ofMinutes(durationMinutes), startTime);
                case EPIC:
                    return new Epic(id, title, description, status, Duration.ofMinutes(durationMinutes), startTime, taskManager);
                case SUBTASK:
                    int epicId = Integer.parseInt(fields[5]);
                    return new SubTask(id, title, description, status, epicId, Duration.ofMinutes(durationMinutes), startTime);
                default:
                    throw new IllegalArgumentException("Unknown task type: " + type);
            }
        } catch (Exception e) {
            throw new ManagerLoadFileException("Error reading task from file", e);
        }
    }

    protected void addTaskToStorage(Task task) {
        if (task instanceof Epic) {
            createEpic((Epic) task);
        } else if (task instanceof SubTask) {
            createSubTask((SubTask) task);
        } else {
            createTask(task);
        }
    }
}