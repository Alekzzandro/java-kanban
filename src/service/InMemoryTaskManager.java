package service;

import exception.ManagerLoadFileException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
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
        if (epics.containsKey(subTask.getEpicId())) {
            validateTaskOverlap(subTask);
            subTask.setId(nextId++);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            addSubTaskToEpic(epic, subTask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            return subTask;
        }
        return null;
    }

    private void validateTaskOverlap(Task task) {
        for (Task existingTask : getAllTasks()) {
            if (existingTask.getId() == task.getId()) continue;
            if (this.isOverlap(task, existingTask)) {
                throw new IllegalArgumentException("Task overlaps with existing tasks");
            }
        }
    }

    private List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
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
        for (int subtaskId : getSubTaskIds(epic.getId())) {
            SubTask subtask = subTasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
            }
        }
        if (countNew == getSubTaskIds(epic.getId()).size()) {
            epic.setStatus(Status.NEW);
        } else if (countDone == getSubTaskIds(epic.getId()).size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTime(Epic epic) {
        List<SubTask> subTasksList = getSubTasksByEpic(epic.getId());

        if (!subTasksList.isEmpty()) {
            Duration totalDuration = Duration.ZERO;
            LocalDateTime earliestStartTime = null;
            LocalDateTime latestEndTime = null;

            for (SubTask subTask : subTasksList) {
                if (subTask.getDuration() != null) {
                    totalDuration = totalDuration.plus(subTask.getDuration());
                }
                if (subTask.getStartTime() != null) {
                    earliestStartTime = (earliestStartTime == null || subTask.getStartTime().isBefore(earliestStartTime))
                            ? subTask.getStartTime() : earliestStartTime;
                    latestEndTime = (latestEndTime == null || subTask.getEndTime().isAfter(latestEndTime))
                            ? subTask.getEndTime() : latestEndTime;
                }
            }

            epic.setDuration(totalDuration);
            epic.setStartTime(earliestStartTime);
            epic.setEndTime(latestEndTime);
        } else {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
        }
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setTitle(epic.getTitle());
            existingEpic.setDescription(epic.getDescription());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId()) || subTask.getEpicId() != subTasks.get(subTask.getId()).getEpicId())
            return false;
        validateTaskOverlap(subTask);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicTime(epic);
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
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
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
            List<Integer> subTaskIds = getSubTaskIds(epicId);
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
            removeSubTaskFromEpic(epicId, subTaskId);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            updateEpicStatus(epics.get(epicId));
            updateEpicTime(epics.get(epicId));
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
            addSubTaskToEpic(epic, subTask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
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

    public List<Integer> getSubTaskIds(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return epic.getSubTaskIds();
    }

    protected boolean isOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();
        if (end1 == null || end2 == null) return false;
        return !(end1.isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(end2));
    }

    private void addSubTaskToEpic(Epic epic, int subTaskId) {
        if (epic != null) {
            epic.addSubTask(subTaskId);
        }
    }

    private void removeSubTaskFromEpic(int epicId, int subTaskId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.removeSubTask(subTaskId);
        }
    }
}