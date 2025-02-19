package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String description, TaskStatus status, Duration duration,
                   LocalDateTime startTime, int epicId) {
        super(taskName, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String description, TaskStatus status, int epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String startTimeString = (getStartTime() != null) ? getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";
        String endTimeString = (getEndTime() != null) ? getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";

        return "Подзадача{" +
                "Название='" + getTaskName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус=" + getStatus() +
                ", EpicId=" + epicId +
                ", Длительность=" + (getDuration() != null ? getDuration() : "Не задано") +
                ", Время начала=" + startTimeString +
                ", Время окончания=" + endTimeString +
                ", ID подзадачи=" + getId() + '}';
    }
}

