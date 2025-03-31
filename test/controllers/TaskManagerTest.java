package controllers;

import main.controllers.TaskManager;

import main.model.Task;
import main.model.Epic;
import main.model.Subtask;
import main.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public abstract void setUp();

    //Тестируем добавление новой задачи и получение задачи по Id
    @Test
    public void shouldAddNewTask() {
        taskManager.removeAllTasks();

        Task newTask = new Task("Новая Задача", "Описание новой задачи", TaskStatus.NEW);
        taskManager.addTask(newTask);

        assertEquals(1, taskManager.getAllTasks().size(), "Количество задач должно быть 1 после " +
                "добавления новой задачи.");

        // Получаем задачу по Id и проверяем, что она соответствует добавленной
        Task managedTask = taskManager.getTaskById(newTask.getId());
        assertNotNull(managedTask, "Задача должна возвращаться из менеджера.");
        assertEquals(newTask.getTaskName(), managedTask.getTaskName(), "Имена задач должны совпадать.");
        assertEquals(newTask.getDescription(), managedTask.getDescription(), "Описание задач должно совпадать.");
        assertEquals(newTask.getStatus(), managedTask.getStatus(), "Статусы задач должны совпадать.");
    }

    // Проверяем, что задачи с заданным id и сгенерированным id не конфликтуют
    @Test
    public void shouldNoIdConflict() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        task2.setId(task1.getId());

        taskManager.addTask(task2);

        Task taskFromManager = taskManager.getTaskById(task1.getId());
        assertEquals(task1, taskFromManager, "Задача с конфликтующим ID должна быть task1.");
    }

    //Проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void shouldImmutabilityAfterAddition() {
        Task initialTask = new Task("Исходная Задача", "Исходное Описание", TaskStatus.NEW);
        taskManager.addTask(initialTask); // Добавление исходной задачи

        Task managedTask = taskManager.getTaskById(initialTask.getId());

        // Проверяем, что все поля задачи не изменяются
        assertEquals(initialTask.getTaskName(), managedTask.getTaskName());
        assertEquals(initialTask.getDescription(), managedTask.getDescription());
        assertEquals(initialTask.getStatus(), managedTask.getStatus());

        Task changedTask = new Task("Изменённая Задача", "Новое Описание", TaskStatus.IN_PROGRESS);

        assertEquals(initialTask.getTaskName(), taskManager.getTaskById(initialTask.getId()).getTaskName());
        assertEquals(initialTask.getDescription(), taskManager.getTaskById(initialTask.getId()).getDescription());
        assertEquals(initialTask.getStatus(), taskManager.getTaskById(initialTask.getId()).getStatus());

        //Проверяем что измененная задача не влияет на исходную
        assertNotEquals(changedTask.getTaskName(), initialTask.getTaskName(),
                "Имя задачи не должно изменяться при добавлении");
        assertNotEquals(changedTask.getDescription(), initialTask.getDescription(),
                "Описание задачи не должно изменяться при добавлении");
        assertNotEquals(changedTask.getStatus(), initialTask.getStatus(),
                "Статус задачи не должен изменяться при добавлении");
    }

    //Проверяем, TaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    public void shouldAddDifferentTypesOfTasksAndFindTasksById() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskManager.addTask(task1);

        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                epic.getId());
        taskManager.addSubtask(subtask);
        //Проверяем, что задача, эпик и подзадача успешно найдены по ID
        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача 1 должна быть найдена.");
        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпик 1 должен быть найден.");
        assertNotNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача 1 должна быть найдена.");
    }
}
