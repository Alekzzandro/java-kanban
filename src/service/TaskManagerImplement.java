package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TaskManagerImplement implements TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<Epic> epics = new ArrayList<>();
    private List<SubTask> subTasks = new ArrayList<>();
    private List<Task> history = new ArrayList<>();

    private boolean isTimeOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) return false;
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean isTaskOverlapping(Task newTask) {
        List<Task> allTasks = new ArrayList<>(tasks);
        allTasks.addAll(subTasks);
        allTasks.addAll(epics);
        LocalDateTime newTaskStart = newTask.getStartTime();
        LocalDateTime newTaskEnd = newTask.getEndTime();
        return allTasks.stream().anyMatch(task -> {
            if (task == newTask) return false;
            LocalDateTime taskStart = task.getStartTime();
            LocalDateTime taskEnd = task.getEndTime();
            return isTimeOverlapping(taskStart, taskEnd, newTaskStart, newTaskEnd);
        });
    }

    @Override
    public Task createTask(Task task) {
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("The task overlaps with an existing task.");
        }
        tasks.add(task);
        addToHistory(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (isTaskOverlapping(epic)) {
            throw new IllegalArgumentException("The epic overlaps with an existing task.");
        }
        epics.add(epic);
        addToHistory(epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (isTaskOverlapping(subTask)) {
            throw new IllegalArgumentException("The subtask overlaps with an existing task.");
        }
        subTasks.add(subTask);
        updateEpicStatus(subTask.getEpicId());
        addToHistory(subTask);
        return subTask;
    }

    @Override
    public boolean updateTask(Task task) {
        Task existingTask = getTaskById(task.getId());
        if (existingTask != null) {
            if (isTaskOverlapping(task)) {
                throw new IllegalArgumentException("The updated task overlaps with an existing task.");
            }
            tasks.remove(existingTask);
            tasks.add(task);
            addToHistory(task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        Epic existingEpic = getEpicById(epic.getId());
        if (existingEpic != null) {
            if (isTaskOverlapping(epic)) {
                throw new IllegalArgumentException("The updated epic overlaps with an existing task.");
            }
            epics.remove(existingEpic);
            epics.add(epic);
            addToHistory(epic);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        SubTask existingSubTask = getSubTaskById(subTask.getId());
        if (existingSubTask != null) {
            if (isTaskOverlapping(subTask)) {
                throw new IllegalArgumentException("The updated subtask overlaps with an existing task.");
            }
            subTasks.remove(existingSubTask);
            subTasks.add(subTask);
            updateEpicStatus(subTask.getEpicId());
            addToHistory(subTask);
            return true;
        }
        return false;
    }

    @Override
    public Task getTaskById(int taskId) {
        return tasks.stream().filter(task -> task.getId() == taskId).findFirst().orElse(null);
    }

    @Override
    public Epic getEpicById(int epicId) {
        return epics.stream().filter(epic -> epic.getId() == epicId).findFirst().orElse(null);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.stream().filter(subTask -> subTask.getId() == subTaskId).findFirst().orElse(null);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics);
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            return epic.getSubTaskIds().stream()
                    .map(this::getSubTaskById)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.removeIf(task -> task.getId() == taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        epics.removeIf(epic -> epic.getId() == epicId);
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        subTasks.removeIf(subTask -> subTask.getId() == subTaskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    public void addToHistory(Task task) {
        if (!history.contains(task)) {
            history.add(task);
        }
    }

    public List<Task> getPrioritizedTasks() {
        TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        sortedTasks.addAll(tasks);
        sortedTasks.addAll(epics);
        sortedTasks.addAll(subTasks);
        return new ArrayList<>(sortedTasks);
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            int countNew = 0;
            int countDone = 0;
            for (int subtaskId : epic.getSubTaskIds()) {
                SubTask subtask = getSubTaskById(subtaskId);
                if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
            }
            if (countNew == epic.getSubTaskIds().size()) {
                epic.setStatus(Status.NEW);
            } else if (countDone == epic.getSubTaskIds().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}