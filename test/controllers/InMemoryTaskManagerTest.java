package controllers;

import main.controllers.InMemoryTaskManager;
import main.exceptions.TimeConflictException;
import main.model.Task;
import main.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    //Проверяем, что при пересечении интервалов будет выброшено исключение
    @Test
    public void shouldNotAddTaskWithOverlappingInterval() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 18, 10, 0));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 18, 10, 30));

        assertThrows(TimeConflictException.class, () -> taskManager.addTask(task2),
                "Должно быть выброшено исключение при пересечении интервалов.");
    }

    //Проверяем, что при обновлении задачи выбрасывается исключение, если новый интервал пересекается с существующими
    @Test
    public void shouldNotUpdateTaskWithOverlappingInterval() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 18, 10, 0));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 18, 11, 0));
        taskManager.addTask(task2);

        task2.setStartTime(LocalDateTime.of(2025, 2, 18, 10, 30));

        assertThrows(TimeConflictException.class, () -> taskManager.updateTask(task2),
                "Должно быть выброшено исключение при обновлении задачи с пересекающимся интервалом.");
    }
}