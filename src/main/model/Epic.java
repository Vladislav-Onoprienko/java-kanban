package main.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
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
    public String toString() {
        return "Эпик{" +
                "Название='" + getTaskName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус=" + getStatus() +
                ", Подзадачи=" + subtasksIds +
                ", ID=" + getId() + '}';
    }
}
