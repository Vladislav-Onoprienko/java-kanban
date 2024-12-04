package Main;

import Main.controllers.TaskManager;
import Main.model.Epic;
import Main.model.Subtask;
import Main.model.Task;
import Main.model.TaskStatus;
import Main.controllers.Managers;

public class Main {

    public static void main(String[] args) {
        // Получаем менеджер задач
        TaskManager manager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
        subtask1.setEpicId(epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId());
        subtask2.setEpicId(epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epic2.getId());
        subtask3.setEpicId(epic2.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Печать всех задач
        System.out.println("Начальное состояние:");
        printAllTasks(manager);

        // Изменение статусов
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task2);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);

        // Печать после изменения статусов
        System.out.println("\nПосле изменения статусов:");
        printAllTasks(manager);

        // Удаление задачи, эпика и подзадачи
        manager.deleteTaskById(task2.getId());
        manager.deleteTaskById(epic2.getId());
        manager.deleteTaskById(subtask2.getId());

        // Печать после удаления
        System.out.println("\nПосле удаления:");
        printAllTasks(manager);
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

        System.out.println("\nИстория последних 10 просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
