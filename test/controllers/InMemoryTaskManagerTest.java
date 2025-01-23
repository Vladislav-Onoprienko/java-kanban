package controllers;

import main.model.Task;
import main.model.Epic;
import main.model.Subtask;
import main.model.TaskStatus;

import main.controllers.Managers;
import main.controllers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {

    private TaskManager manager;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
    }

    //Тестируем добавление новой задачи и получение задачи по Id
    @Test
    public void shouldAddNewTask() {
        manager.removeAllTasks();

        Task newTask = new Task("Новая Задача", "Описание новой задачи", TaskStatus.NEW);
        manager.addTask(newTask);

        assertEquals(1, manager.getAllTasks().size(), "Количество задач должно быть 1 после " +
                "добавления новой задачи.");

        // Получаем задачу по Id и проверяем, что она соответствует добавленной
        Task managedTask = manager.getTaskById(newTask.getId());
        assertNotNull(managedTask, "Задача должна возвращаться из менеджера.");
        assertEquals(newTask.getTaskName(), managedTask.getTaskName(), "Имена задач должны совпадать.");
        assertEquals(newTask.getDescription(), managedTask.getDescription(), "Описание задач должно совпадать.");
        assertEquals(newTask.getStatus(), managedTask.getStatus(), "Статусы задач должны совпадать.");
    }

    //Проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    public void shouldAddDifferentTypesOfTasksAndFindTasksById() {
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                epic.getId());
        manager.addSubtask(subtask);
        //Проверяем, что задача, эпик и подзадача успешно найдены по ID
        assertNotNull(manager.getTaskById(task1.getId()), "Задача 1 должна быть найдена.");
        assertNotNull(manager.getEpicById(epic.getId()), "Эпик 1 должен быть найден.");
        assertNotNull(manager.getSubtaskById(subtask.getId()), "Подзадача 1 должна быть найдена.");
    }

    // Проверяем, что задачи с заданным id и сгенерированным id не конфликтуют
    @Test
    public void shouldNoIdConflict() {
        Task task3 = new Task("Задача 3", "Описание 3", TaskStatus.NEW);
        int taskId = task1.getId();
        task3.setId(taskId);

        manager.addTask(task3);
        Task taskById = manager.getTaskById(taskId);
        assertEquals(task1, taskById, "Задача с конфликтующим ID должна быть task1.");
    }

    //Проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void shouldImmutabilityAfterAddition() {
        Task initialTask = new Task("Исходная Задача", "Исходное Описание", TaskStatus.NEW);
        manager.addTask(initialTask); // Добавление исходной задачи

        Task managedTask = manager.getTaskById(initialTask.getId());

        // Проверяем, что все поля задачи не изменяются
        assertEquals(initialTask.getTaskName(), managedTask.getTaskName());
        assertEquals(initialTask.getDescription(), managedTask.getDescription());
        assertEquals(initialTask.getStatus(), managedTask.getStatus());

        Task changedTask = new Task("Изменённая Задача", "Новое Описание", TaskStatus.IN_PROGRESS);

        assertEquals(initialTask.getTaskName(), manager.getTaskById(initialTask.getId()).getTaskName());
        assertEquals(initialTask.getDescription(), manager.getTaskById(initialTask.getId()).getDescription());
        assertEquals(initialTask.getStatus(), manager.getTaskById(initialTask.getId()).getStatus());

        //Проверяем что измененная задача не влияет на исходную
        assertNotEquals(changedTask.getTaskName(), initialTask.getTaskName(),
                "Имя задачи не должно изменяться при добавлении");
        assertNotEquals(changedTask.getDescription(), initialTask.getDescription(),
                "Описание задачи не должно изменяться при добавлении");
        assertNotEquals(changedTask.getStatus(), initialTask.getStatus(),
                "Статус задачи не должен изменяться при добавлении");
    }
}