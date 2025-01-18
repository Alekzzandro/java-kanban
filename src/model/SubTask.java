package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String title, String description, int epicId, Status status, TaskTypes type) {
        super(id, title, description, status, type);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskTypes getTaskType() {
        return TaskTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{id=" + getId() + ", title='" + getTitle() + "', description='" + getDescription() + "', epicId=" + epicId + ", status=" + getStatus() + "}";
    }
}
