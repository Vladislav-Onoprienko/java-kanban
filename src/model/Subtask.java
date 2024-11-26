package model;

public class Subtask extends Task {
    private int epicId;

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
    public String toString() {
        return "Подзадача{" +
                "Название='" + getTaskName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус=" + getStatus() +
                ", EpicId=" + epicId + '}';
    }
}

