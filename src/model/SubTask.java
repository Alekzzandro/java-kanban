package model;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int id, String title, String description, int epicId, Status status) {
        super(id, title, description, status, TaskTypes.SUBTASK);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId &&
                getId() == subTask.getId() &&
                Objects.equals(getTitle(), subTask.getTitle()) &&
                Objects.equals(getDescription(), subTask.getDescription()) &&
                getStatus() == subTask.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), epicId, getStatus());
    }

    @Override
    public String toString() {
        return "SubTask{id=" + getId() + ", title='" + getTitle() + "', description='" + getDescription() + "', epicId=" + epicId + ", status=" + getStatus() + '}';
    }
}
