package main.model;

import main.controllers.TaskManager;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Epic extends Task {
    private LocalDateTime endTime;
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW, Duration.ZERO, null);
        this.endTime = null;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateTimeAndDuration(TaskManager manager) {
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : subtasksIds) {
            Subtask subtask = manager.getSubtaskById(subtaskId);
            if (subtask != null) {
                subtasks.add(subtask);
            }
        }

        if (subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        this.startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public String toString() {
        String startTimeString = (startTime != null) ? startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";
        String endTimeString = (endTime != null) ? endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не задано";

        return "Эпик{" +
                "Название='" + getTaskName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус=" + getStatus() +
                ", Подзадачи=" + subtasksIds +
                ", Длительность=" + getDuration() +
                ", Время начала=" + startTimeString +
                ", Время окончания=" + endTimeString +
                ", ID=" + getId() + '}';
    }
}
