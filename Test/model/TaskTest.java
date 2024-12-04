package model;

import Main.model.Task;
import Main.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TaskTest {

    //Проверяем, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void shouldTaskEqualityById() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковыми id должны быть равны.");
    }
}