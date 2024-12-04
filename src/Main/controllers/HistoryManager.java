package Main.controllers;
import Main.model.Task;
import java.util.ArrayList;

public interface HistoryManager {
    boolean add(Task task);
    ArrayList<Task> getHistory();
}
