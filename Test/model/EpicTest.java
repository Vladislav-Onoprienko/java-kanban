package model;
import Main.model.Epic;
import Main.model.Subtask;
import Main.model.TaskStatus;

import Main.controllers.Managers;
import Main.controllers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EpicTest {

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
}