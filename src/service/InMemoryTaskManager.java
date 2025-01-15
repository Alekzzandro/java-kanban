package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private int nextId = 1;

    @Override
    public Task createTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    @Override
    public boolean updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return false;
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
        subTasks.put(subTask.getId(), subTask);
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
        List<SubTask> subTaskList = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epicId) {
                subTaskList.add(subTask);
            }
        }
        return subTaskList;
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
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
    public void deleteSubTask(int subTaskId) {
        SubTask subtask = subTasks.get(subTaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            epics.get(epicId).removeSubTask(subTaskId);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            updateEpicStatus(epics.get(epicId));
        } else {
            System.out.println("Сабтаска с ID " + subTaskId + " не существует");
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
}
