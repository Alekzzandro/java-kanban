package model;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String title, String description, int epicId, Status status, TaskTypes taskType) {
        super(id, title, description, status, taskType);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }
}