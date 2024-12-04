package Main.controllers;

import Main.model.Epic;
import Main.model.Subtask;
import Main.model.Task;

import java.util.ArrayList;


public interface TaskManager {
    //Получение списка всех задач
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    //Удаление всех задач
    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    //Получение по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //Добавление задач, эпиков и подзадач
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    //Обновление задач, эпиков и подзадач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // Удаление задачи по идентификатору
    void deleteTaskById(int id);

    // Получение списка подзадач эпика
    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    // Получение истории просмотров
    ArrayList<Task> getHistory();
}
