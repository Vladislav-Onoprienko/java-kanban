package Main;

import Main.controllers.TaskManager;
import Main.controllers.Managers;
import Main.model.Epic;
import Main.model.Subtask;
import Main.model.Task;
import Main.model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Эпик 1", "Эпик без подзадач");
        Epic epic2 = new Epic("Эпик 2", "Эпик с подзадачами");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic2.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic2.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Запрос задач и вывод истории
        printAllTasks(manager);
        printHistory(manager);

        // Запрос задач в разном порядке
        System.out.println("Запрос задачи 1:");
        manager.getTaskById(task1.getId());
        printHistory(manager);

        System.out.println("Запрос задачи 2:");
        manager.getTaskById(task2.getId());
        printHistory(manager);

        System.out.println("Запрос эпика 1:");
        manager.getEpicById(epic1.getId());
        printHistory(manager);

        System.out.println("Запрос подзадачи 1:");
        manager.getSubtaskById(subtask1.getId());
        printHistory(manager);

        // Удаление задачи и проверка истории
        System.out.println("Удаление задачи 1:");
        manager.deleteTaskById(task1.getId());
        printHistory(manager);

        // Удаление эпика с подзадачами и проверка истории
        System.out.println("Удаление эпика 2 с подзадачами:");
        manager.deleteTaskById(epic2.getId());
        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики и их подзадачи:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Integer subtaskId : epic.getSubtasksIds()) {
                Subtask subtask = manager.getSubtaskById(subtaskId);
                if (subtask != null) {
                    System.out.println("--> " + subtask);
                }
            }
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printHistory(TaskManager manager){
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------");
    }
}

