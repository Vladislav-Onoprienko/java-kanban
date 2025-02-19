package main.model;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    private String taskName;
    private int id;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String taskName, String description, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return (id == otherTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String startTimeString = (startTime != null) ? startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";
        String endTimeString = (getEndTime() != null) ? getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";
        return "Задача{" +
                "Название='" + taskName + '\'' +
                ", id=" + id +
                ", Описание='" + description + '\'' +
                ", Статус=" + status +
                ", Длительность=" + (duration != null ? duration : "Не задано") +
                ", Время начала=" + startTimeString +
                ", Время окончания=" + endTimeString +
                '}';
    }
}
