package main.model;

import java.util.Objects;

public class Task {

    private String taskName;
    private int id;
    private String description;
    private TaskStatus status;
    private final TaskType type;

    public Task(String taskName, String description, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(String taskName, String description, TaskStatus status, TaskType type) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.type = type;
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
        return type;
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
        return "Задача{" +
                "Название='" + taskName + '\'' +
                ", id=" + id +
                ", Описание='" + description + '\'' +
                ", Статус=" + status +
                '}';
    }
}
