package model;

import Main.model.Subtask;
import Main.model.TaskStatus;
import Main.model.Epic;

import Main.controllers.Managers;
import Main.controllers.TaskManager;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    //Проверяем, что подзадачи равны друг другу, если равен их id
    @Test
    public void shouldSubtasksAreEqualIfIdsMatch() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS, 1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны, если их id совпадают.");
    }

    //Проверяем, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void shouldSubtaskCannotBeItsOwnEpic() {
        TaskManager manager = Managers.getDefault(); // Получаем менеджер задач

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        subtask.setEpicId(subtask.getId());
        manager.addSubtask(subtask);

        assertFalse(epic.getSubtasksIds().contains(subtask.getId()), "Подзадача не может быть своим собственным эпиком.");
    }

}