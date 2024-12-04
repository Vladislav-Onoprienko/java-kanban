package Main.controllers;

import Main.model.Task;
import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public boolean add(Task task) {
        if (task == null) {
            System.out.println("Ошибка: нельзя добавить null задачу в историю.");
            return false;
        }

        history.add(task);

        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        return true;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
