package model;

import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private TaskTypes taskType;

    public Task(int id, String title, String description, Status status, TaskTypes taskType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskTypes getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                title.equals(task.title) &&
                description.equals(task.description) &&
                status == task.status &&
                taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, taskType);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }
}