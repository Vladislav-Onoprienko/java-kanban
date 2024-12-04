package controllers;

import Main.model.Task;
import Main.model.Epic;
import Main.model.Subtask;
import Main.model.TaskStatus;

import Main.controllers.Managers;
import Main.controllers.HistoryManager;
import Main.controllers.TaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }

    // //Проверяем добавление задач в историю и их корректное отображение
    @Test
    public void shouldAddTasksAndHistory() {

        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        taskManager.addTask(task2);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic);
        historyManager.add(subtask1);

        assertEquals(4, historyManager.getHistory().size());
        System.out.println("История действий: ");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

    // Тестирование лимита истории
    @Test
    public void shouldFollowHistoryLimit() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Задача " + i, "Описание " + i, TaskStatus.NEW);
            taskManager.addTask(task);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size());
        assertEquals("Задача 11", historyManager.getHistory().get(historyManager.getHistory().size() - 1).getTaskName());
        assertEquals("Задача 2", historyManager.getHistory().get(0).getTaskName());

        System.out.println("История действий: ");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}