package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(int id, String title, String description, Status status, TaskTypes epic) {
        super(id, title, description, status, TaskTypes.EPIC);
    }

    @Override
    public TaskTypes getTaskType() {
        return TaskTypes.EPIC;
    }

    public void addSubTask(SubTask subTask) {
        if (this.getId() == subTask.getId()) {
            throw new IllegalArgumentException("Эпик не может добавлять себя как подзадачу");
        }
        subTasks.add(subTask);
        subTaskIds.add(subTask.getId());
    }

    public void removeSubTask(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
        subTasks.removeIf(subTask -> subTask.getId() == subTaskId);
    }

    public void clearSubTasks() {
        subTaskIds.clear();
        subTasks.clear();
    }

    public List<Integer> getSubTasks() {
        return subTaskIds; // Возвращаем список подзадач
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    @Override
    public String toString() {
        return "Epic{id=" + getId() + ", title='" + getTitle() + "', description='" + getDescription() + "', status=" + getStatus() + "}";
    }
}
