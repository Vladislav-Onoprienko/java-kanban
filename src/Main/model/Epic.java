package Main.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);

    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
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
