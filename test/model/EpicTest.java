package model;
import main.model.Epic;
import main.model.Subtask;
import main.model.TaskStatus;

import main.controllers.Managers;
import main.controllers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }

    //Проверяем, что эпики равны друг другу, если равен их id
    @Test
    void shouldEpicEqualityById() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики должны быть равны, если их id совпадают");
    }

    //Проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    public void shouldEpicCannotBeAddedAsSubtask() {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Некорректная подзадача", "Описание", TaskStatus.NEW,
                epic.getId());
        manager.addSubtask(subtask);

        assertFalse(epic.getSubtasksIds().contains(epic.getId()), "Эпик не может быть подзадачей самого себя.");
    }

    //Тест для расчёта статуса Epic при условии, что все подзадачи со статусом NEW
    @Test
    public void shouldEpicStatusNewWhenAllSubtasksNew() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW.");
    }

    //Тест для расчёта статуса Epic при условии, что все подзадачи со статусом DONE
    @Test
    public void shouldEpicStatusDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.DONE, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE.");
    }

    //Тест для расчёта статуса Epic при условии, что подзадачи со статусами NEW и DONE
    @Test
    public void shouldEpicStatusInProgressWhenSubtasksNewAndDone() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.DONE, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если есть подзадачи NEW и DONE.");
    }

    //Тест для расчёта статуса Epic при условии, что подзадачи со статусом IN_PROGRESS
    @Test
    public void shouldEpicStatusInProgressWhenSubtasksInProgress() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если все подзадачи IN_PROGRESS.");
    }
}