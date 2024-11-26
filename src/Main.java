import controllers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        // Печать всех задач/эпиков/подзадач
        System.out.println("Список задач: " + taskManager.getAllTasks());
        System.out.println("Список эпиков: " + taskManager.getAllEpics());
        System.out.println("Список подзадач: " + taskManager.getAllSubtasks());

        // Изменение статусов
        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);

        // Печать после изменения статусов
        System.out.println("После изменения статусов:");
        System.out.println("Задача 1: " + taskManager.getTaskById(task1.getId()));
        System.out.println("Задача 2: " + taskManager.getTaskById(task2.getId()));
        System.out.println("Эпик 1: " + taskManager.getEpicById(epic1.getId()));
        System.out.println("Эпик 2: " + taskManager.getEpicById(epic2.getId()));
        System.out.println("Подзадача 1: " + taskManager.getSubtaskById(subtask1.getId()));
        System.out.println("Подзадача 2: " + taskManager.getSubtaskById(subtask2.getId()));
        System.out.println("Подзадача 3: " + taskManager.getSubtaskById(subtask3.getId()));

        // Удаление задачи и эпика
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteTaskById(epic2.getId());

        // Печать после удаления
        System.out.println("После удаления:");
        System.out.println("Список задач: " + taskManager.getAllTasks());
        System.out.println("Список эпиков: " + taskManager.getAllEpics());
        System.out.println("Список подзадач: " + taskManager.getAllSubtasks());
    }
}
