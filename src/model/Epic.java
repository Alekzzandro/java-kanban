package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds = new ArrayList<>();

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status, TaskTypes.EPIC);
    }

    @Override
    public TaskTypes getTaskType() {
        return TaskTypes.EPIC;
    }

    public void addSubTask(int subTaskId) {
        if (this.getId() == subTaskId) {
            throw new IllegalArgumentException("Эпик не может добавлять себя как подзадачу");
        }
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    public void removeSubTask(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
    }

    public void clearSubTasks() {
        subTaskIds.clear();
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    @Override
    public String toString() {
        return "Epic{id=" + getId() + ", title='" + getTitle() + "', description='" + getDescription() + "', status=" + getStatus() + "}";
    }
}
