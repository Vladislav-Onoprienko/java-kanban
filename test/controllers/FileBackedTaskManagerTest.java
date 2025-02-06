package controllers;

import main.controllers.FileBackedTaskManager;
import main.exceptions.ManagerSaveException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager_test", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
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
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic1.getId());

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        FileBackedTaskManager loadedManager = loadManagerFromFile();

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        Task loadedTask1 = loadedManager.getTaskById(task1.getId());
        assertNotNull(loadedTask1);
        assertEquals(task1.getTaskName(), loadedTask1.getTaskName());
        assertEquals(task1.getDescription(), loadedTask1.getDescription());

        Epic loadedEpic1 = loadedManager.getEpicById(epic1.getId());
        assertNotNull(loadedEpic1);
        assertEquals(epic1.getTaskName(), loadedEpic1.getTaskName());
        assertEquals(epic1.getDescription(), loadedEpic1.getDescription());

        Subtask loadedSubtask1 = loadedManager.getSubtaskById(subtask1.getId());
        assertNotNull(loadedSubtask1);
        assertEquals(subtask1.getTaskName(), loadedSubtask1.getTaskName());
        assertEquals(subtask1.getDescription(), loadedSubtask1.getDescription());
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
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        manager.addTask(task);

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
            assertTrue(errorMessage.contains("Некорректный формат строки") || errorMessage.contains("Ошибка формата строки"),
                    "Сообщение об ошибке не содержит ожидаемый текст: " + errorMessage);
        } catch (IOException e) {
            fail("Ошибка при создании файла: " + e.getMessage());
        }
    }
}
