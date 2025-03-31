package controllers;

import main.controllers.FileBackedTaskManager;
import main.exceptions.ManagerSaveException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.*;
import java.io.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest  extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    @Override
    public void setUp() {
        try {
            tempFile = File.createTempFile("task_manager_test", ".csv");
            tempFile.deleteOnExit();
            taskManager = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            fail("Ошибка при создании временного файла: " + e.getMessage());
        }
    }

    @AfterEach
    public void deleteTempFile() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private FileBackedTaskManager loadManagerFromFile() throws ManagerSaveException {
        return FileBackedTaskManager.loadFromFile(tempFile);
    }

    // Проверяем, что при загрузке из пустого файла все коллекции пустые
    @Test
    public void shouldReturnEmptyCollectionsWhenLoadingFromEmptyFile() throws ManagerSaveException {
        FileBackedTaskManager loadedManager = loadManagerFromFile();

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    // Добавляем несколько задач, эпиков и подзадач и проверяем правильность их загрузки
    @Test
    public void shouldLoadMultipleTasksCorrectly() throws ManagerSaveException {
        // Создаём задачи, эпик и подзадачу
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 16, 10, 0));
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ofHours(2),
                LocalDateTime.of(2025, 2, 16, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 2, 16, 13, 0),
                epic1.getId());
        taskManager.addSubtask(subtask1);


        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик 1 должен быть найден.");
        assertNotNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача 1 должна быть найдена.");

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = loadManagerFromFile();

        System.out.println("Задачи из загруженного менеджера:");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики из загруженного менеджера:");
        for (Epic epic : loadedManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("Подзадачи из загруженного менеджера:");
        for (Subtask subtask : loadedManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Проверяем, что количество задач, эпиков и подзадач соответствует ожидаемому
        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        // Проверка первой задачи
        Task loadedTask1 = loadedManager.getTaskById(task1.getId());
        assertNotNull(loadedTask1);
        assertEquals(task1.getTaskName(), loadedTask1.getTaskName());
        assertEquals(task1.getDescription(), loadedTask1.getDescription());
        assertEquals(task1.getStatus(), loadedTask1.getStatus());
        assertEquals(task1.getDuration(), loadedTask1.getDuration());
        assertEquals(task1.getStartTime(), loadedTask1.getStartTime());

        // Проверка второй задачи
        Task loadedTask2 = loadedManager.getTaskById(task2.getId());
        assertNotNull(loadedTask2);
        assertEquals(task2.getTaskName(), loadedTask2.getTaskName());
        assertEquals(task2.getDescription(), loadedTask2.getDescription());
        assertEquals(task2.getStatus(), loadedTask2.getStatus());
        assertEquals(task2.getDuration(), loadedTask2.getDuration());
        assertEquals(task2.getStartTime(), loadedTask2.getStartTime());

        // Проверка эпика
        Epic loadedEpic1 = loadedManager.getEpicById(epic1.getId());
        assertNotNull(loadedEpic1);
        assertEquals(epic1.getTaskName(), loadedEpic1.getTaskName());
        assertEquals(epic1.getDescription(), loadedEpic1.getDescription());
        assertEquals(epic1.getDuration(), loadedEpic1.getDuration());
        assertEquals(epic1.getStartTime(), loadedEpic1.getStartTime());

        // Проверка подзадачи
        Subtask loadedSubtask1 = loadedManager.getSubtaskById(subtask1.getId());
        assertNotNull(loadedSubtask1);
        assertEquals(subtask1.getTaskName(), loadedSubtask1.getTaskName());
        assertEquals(subtask1.getDescription(), loadedSubtask1.getDescription());
        assertEquals(subtask1.getStatus(), loadedSubtask1.getStatus());
        assertEquals(subtask1.getDuration(), loadedSubtask1.getDuration());
        assertEquals(subtask1.getStartTime(), loadedSubtask1.getStartTime());
    }


    // Проверяем, что при загрузке из файла с пустым содержимым все коллекции пустые
    @Test
    public void shouldReturnEmptyCollectionsWhenLoadingFromEmptyContentFile() throws ManagerSaveException {
        FileBackedTaskManager loadedManager = loadManagerFromFile();

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    // Добавляем одну задачу и проверяем, что она загружена правильно
    @Test
    public void shouldLoadSingleTaskCorrectly() throws ManagerSaveException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 16, 10, 0));
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = loadManagerFromFile();

        assertEquals(1, loadedManager.getAllTasks().size());
        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertNotNull(loadedTask);
        assertEquals(task.getTaskName(), loadedTask.getTaskName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    // Проверяем, что выбрасывается исключение при загрузке файла с некорректным форматом
    @Test
    public void shouldThrowExceptionWhenFileHasInvalidFormat() {
        try {
            // Записываем некорректные данные в файл
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("id,type,name,status,description,epic\n");
                writer.write("1,TASK,Task 1,NEW,Description\n"); // Правильная строка
                writer.write("2,TASK,Task 2,IN_PROGRESS\n"); // Недостающие поля
                writer.write("3,EPIC, Epic 1,NEW,Description\n"); // Правильная строка
                writer.write("4,SUBTASK,Subtask 1,NEW,Description,3\n"); // Правильная строка
                writer.write("5,SUBTASK,Subtask 2,NEW\n"); // Недостающие поля
            }

            ManagerSaveException thrownException = null;

            try {
                // Пытаемся загрузить менеджер из файла
                FileBackedTaskManager.loadFromFile(tempFile);
            } catch (ManagerSaveException e) {
                // Ловим исключение и сохраняем его для дальнейшей проверки
                thrownException = e;
            }

            assertNotNull(thrownException, "Ожидалось исключение ManagerSaveException");

            // Проверяем, что ошибка содержит информацию о неверном формате
            String errorMessage = thrownException.getMessage();
            assertTrue(errorMessage.contains("Некорректный формат строки")
                            || errorMessage.contains("Ошибка формата строки"),
                    "Сообщение об ошибке не содержит ожидаемый текст: " + errorMessage);
        } catch (IOException e) {
            fail("Ошибка при создании файла: " + e.getMessage());
        }
    }

    // Проверяем загрузку истории из файла
    @Test
    public void shouldLoadHistoryFromFile() throws ManagerSaveException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 16, 10, 0));
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        FileBackedTaskManager loadedManager = loadManagerFromFile();

        // Проверяем, что история загружена корректно
        assertEquals(1, loadedManager.getHistory().size(), "История должна содержать 1 задачу.");
        assertEquals(task, loadedManager.getHistory().get(0), "Задача в истории должна совпадать с добавленной.");
    }
}
