package controllers;

import main.model.Task;
import main.model.Epic;
import main.model.Subtask;
import main.model.TaskStatus;

import main.controllers.Managers;
import main.controllers.TaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }

    //Проверяем добавление задач в историю и их удаление из начала/середины/конца списка
    @Test
    public void shouldAddAndRemoveTasksFromHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        Task task3 = new Task("Задача 3", "Описание 3", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        assertEquals(3, taskManager.getHistory().size());
        assertEquals(task1, taskManager.getHistory().get(0));
        assertEquals(task3, taskManager.getHistory().get(2));
        System.out.println("Начальное состояние истории: " + taskManager.getHistory());

        // Удаляем задачу из середины
        taskManager.deleteTaskById(task2.getId());
        assertEquals(2, taskManager.getHistory().size());
        assertFalse(taskManager.getHistory().contains(task2));

        // Удаляем первую задачу
        taskManager.deleteTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task3, taskManager.getHistory().get(0));

        // Удаляем последнюю задачу
        taskManager.deleteTaskById(task3.getId());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    //Проверяем, что при удалении эпика удаляются его подзадачи из менеджера задач и истории
    @Test
    public void shouldRemoveEpicAndItsSubtasksCorrectly() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertTrue(taskManager.getAllEpics().contains(epic), "Эпик не добавлен в менеджер задач.");
        assertTrue(taskManager.getAllSubtasks().contains(subtask1), "Первая подзадача не добавлена в менеджер задач.");
        assertTrue(taskManager.getAllSubtasks().contains(subtask2), "Вторая подзадача не добавлена в менеджер задач.");

        System.out.println("Начальное состояние эпиков: " + taskManager.getAllEpics());
        System.out.println("Начальное состояние подзадач: " + taskManager.getAllSubtasks());
        System.out.println("Начальное состояние истории: " + taskManager.getHistory());

        taskManager.deleteTaskById(epic.getId());

        System.out.println("Состояние эпиков после удаления: " + taskManager.getAllEpics());
        System.out.println("Состояние подзадач после удаления: " + taskManager.getAllSubtasks());
        System.out.println("Состояние истории после удаления: " + taskManager.getHistory());

        assertFalse(taskManager.getAllEpics().contains(epic), "Эпик не был удалён из менеджера задач.");
        assertFalse(taskManager.getAllSubtasks().contains(subtask1), "Первая подзадача не была удалена из менеджера задач.");
        assertFalse(taskManager.getAllSubtasks().contains(subtask2), "Вторая подзадача не была удалена из менеджера задач.");

        assertFalse(taskManager.getHistory().contains(epic), "Эпик остался в истории.");
        assertFalse(taskManager.getHistory().contains(subtask1), "Первая подзадача осталась в истории.");
        assertFalse(taskManager.getHistory().contains(subtask2), "Вторая подзадача осталась в истории.");
    }

    // Проверяем, что удалённые подзадачи не хранят старые id
    @Test
    public void shouldNotContainDeletedSubtaskId() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.deleteTaskById(subtask.getId());

        assertFalse(taskManager.getAllSubtasks().contains(subtask), "Подзадача не была удалена из менеджера задач.");
        assertFalse(taskManager.getHistory().contains(subtask), "Подзадача осталась в истории.");

        assertFalse(epic.getSubtasksIds().contains(subtask.getId()), "Эпик содержит ссылку на удалённую подзадачу.");
    }

    // Проверяем, что эпики не содержат неактуальные id подзадач
    @Test
    public void shouldRemoveSubtaskIdFromEpicWhenSubtaskDeleted() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertTrue(epic.getSubtasksIds().contains(subtask1.getId()), "Эпик не содержит подзадачу 1.");
        assertTrue(epic.getSubtasksIds().contains(subtask2.getId()), "Эпик не содержит подзадачу 2.");

        taskManager.deleteTaskById(subtask1.getId());

        assertFalse(epic.getSubtasksIds().contains(subtask1.getId()), "Эпик содержит неактуальную ссылку на подзадачу 1.");
        assertTrue(epic.getSubtasksIds().contains(subtask2.getId()), "Эпик не содержит подзадачу 2.");
    }

    //Проверяем влияние изменения полей задачи через сеттеры на данные в менеджере
    @Test
    public void shouldUpdateTaskFieldsInManagerWhenModified() {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.addTask(task);

        assertEquals("Задача 1", task.getTaskName(), "Название задачи не совпадает.");
        assertEquals("Описание задачи 1", task.getDescription(), "Описание задачи не совпадает.");

        task.setTaskName("Обновленное название задачи");
        task.setDescription("Обновленное описание задачи");

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Обновленное название задачи", updatedTask.getTaskName(), "Название задачи не обновилось.");
        assertEquals("Обновленное описание задачи", updatedTask.getDescription(), "Описание задачи не обновилось.");

        // Проверяем, что задача обновилась в истории
        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(updatedTask), "История не содержит обновленную задачу.");
    }
}
