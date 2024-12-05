package model;

import java.util.Objects;

public class SubTask {
    private int id;
    private String title;
    private String description;
    private int epicId;
    private Status status;

    public SubTask(int id, String title, String description, int epicId, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getEpicId() {
        return epicId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId &&
                id == subTask.id &&
                Objects.equals(title, subTask.title) &&
                Objects.equals(description, subTask.description) &&
                status == subTask.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, epicId, status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                '}';
    }
}