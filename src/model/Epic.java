package model;

import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();
    private TaskManager taskManager;

    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime, TaskManager taskManager) {
        super(id, title, description, status, duration, startTime);
        this.taskManager = taskManager;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
        updateEpicDurationAndTimes();
    }

    public void updateEpicDurationAndTimes() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        for (Integer subTaskId : subTaskIds) {
            SubTask subTask = taskManager.getSubTaskById(subTaskId);
            if (subTask != null) {
                totalDuration = totalDuration.plus(subTask.getDuration());
                if (earliestStartTime == null || subTask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subTask.getStartTime();
                }
                LocalDateTime subTaskEndTime = subTask.getEndTime();
                if (latestEndTime == null || subTaskEndTime.isAfter(latestEndTime)) {
                    latestEndTime = subTaskEndTime;
                }
            }
        }
        this.duration = totalDuration;
        this.startTime = earliestStartTime;
        this.endTime = latestEndTime;
    }

    public void addSubTask(int subTaskId) {
        if (this.getId() == subTaskId) {
            throw new IllegalArgumentException("Эпик не может добавлять себя как подзадачу");
        }
        subTaskIds.add(subTaskId);
        updateEpicDurationAndTimes();
    }

    public void removeSubTask(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
        updateEpicDurationAndTimes();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }
}