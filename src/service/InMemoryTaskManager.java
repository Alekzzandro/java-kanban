package service;

import exception.ManagerLoadFileException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager extends TaskManagerImplement implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    protected int nextId = 1;

    @Override
    public Task createTask(Task task) throws ManagerLoadFileException {
        validateTaskOverlap(task);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerLoadFileException {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws ManagerLoadFileException {
        validateTaskOverlap(subTask);
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask.getId());
            updateEpicStatus(epic);
        }
        return subTask;
    }

    private void validateTaskOverlap(Task task) {
        for (Task existingTask : getAllTasks()) {
            if (existingTask == task) continue; // Не проверяем пересечение с самой собой
            if (task instanceof SubTask && existingTask instanceof Epic) {
                SubTask subTask = (SubTask) task;
                Epic epic = (Epic) existingTask;
                if (subTask.getEpicId() == epic.getId()) continue; // Не проверяем пересечение с родительским эпиком
            }
            System.out.println("Checking overlap between: " + task + " and " + existingTask);
            if (Task.isOverlap(task, existingTask)) {
                throw new IllegalArgumentException("Task overlaps with existing tasks");
            }
        }
    }

    private List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    @Override
    public boolean updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return false;
        validateTaskOverlap(task);
        tasks.put(task.getId(), task);
        return true;
    }

    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        for (int subtaskId : epic.getSubTaskIds()) {
            SubTask subtask = subTasks.get(subtaskId);
            if (subtask.getStatus() == Status.NEW) {
                countNew++;
            } else if (subtask.getStatus() == Status.DONE) {
                countDone++;
            }
        }
        if (countNew == epic.getSubTaskIds().size()) epic.setStatus(Status.NEW);
        else if (countDone == epic.getSubTaskIds().size()) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return false;
        epics.put(epic.getId(), epic);
        return true;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) return false;
        validateTaskOverlap(subTask);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return true;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(int taskId) throws ManagerLoadFileException {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) throws ManagerLoadFileException {
        if (epics.containsKey(epicId)) {
            List<Integer> subTaskIds = epics.get(epicId).getSubTaskIds();
            for (Integer subtaskId : subTaskIds) {
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        } else {
            System.out.println("Epic with ID " + epicId + " not found");
        }
    }

    @Override
    public void deleteSubTask(int subTaskId) throws ManagerLoadFileException {
        SubTask subtask = subTasks.get(subTaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            epics.get(epicId).removeSubTask(subTaskId);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            updateEpicStatus(epics.get(epicId));
        } else {
            System.out.println("SubTask with ID " + subTaskId + " not found");
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer taskId : subTasks.keySet()) {
            historyManager.remove(taskId);
        }
        subTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void addTaskToStorage(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addEpicToStorage(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void addSubTaskToStorage(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask.getId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Epic with ID " + subTask.getEpicId() + " not found for SubTask with ID " + subTask.getId());
        }
    }

    protected void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public List<Task> getPrioritizedTasks() {
        TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        sortedTasks.addAll(getAllTasks());
        return new ArrayList<>(sortedTasks);
    }
}