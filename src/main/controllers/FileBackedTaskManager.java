package main.controllers;

import main.model.*;
import main.exceptions.ManagerSaveException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    private String taskToString(Task task) {
        Duration duration = task.getDuration();
        String durationString = (duration != null) ? String.valueOf(duration.toMinutes()) : "0";
        String startTime = (task.getStartTime() != null) ? task.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
        String endTime = (task.getEndTime() != null) ? task.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
        String epicId = task.getType() == TaskType.SUBTASK ? String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getTaskName(),
                task.getStatus().name(),
                task.getDescription(),
                durationString,
                startTime,
                endTime,
                epicId
                );
    }

    public static Task fromString(String value)  {
        String[] fields = value.trim().split(",");
        TaskType type = TaskType.valueOf(fields[1]);

        if (type == TaskType.SUBTASK && fields.length != 9) {
            throw new ManagerSaveException("Некорректный формат строки для Subtask: " + value, null);
        }
        if (type != TaskType.SUBTASK && fields.length != 8) {
            throw new ManagerSaveException("Некорректный формат строки для " + type + ": " + value, null);
        }

        try {
            int id = Integer.parseInt(fields[0]);
            TaskStatus status = TaskStatus.valueOf(fields[3]);
            String description = fields[4].trim();
            Duration duration = fields[5].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[5]));
            LocalDateTime startTime = fields[6].isEmpty() ? null : LocalDateTime.parse(fields[6],
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime = fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7],
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (type == TaskType.TASK) {
                return new Task(fields[2].trim(), description, status, duration, startTime);
            } else if (type == TaskType.EPIC) {
                Epic epic = new Epic(fields[2].trim(), description);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                epic.setEndTime(endTime);
                return epic;
            } else if (type == TaskType.SUBTASK) {
                int epicId = Integer.parseInt(fields[8]); // Только для Subtask
                Subtask subtask = new Subtask(fields[2].trim(), description, status, duration, startTime, epicId);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;
            } else {
                throw new ManagerSaveException("Неизвестный тип задачи: " + type, null);
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка разбора строки: " + value, null, e);
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("\uFEFF"); //Столкнулся с проблемой вывода данных в Exel, поэтому нашел такое решение
            writer.write("id,type,name,status,description,duration,startTime,endTime,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + file.getPath(), file, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<String> lines = reader.lines().toList();

            if (lines.size() <= 1) {
                return manager;
            }

            for (String line : lines.subList(1, lines.size())) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Task task = fromString(line);

                    if (task.getType() == TaskType.EPIC) {
                        manager.addEpic((Epic) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        manager.addSubtask((Subtask) task);
                    } else {
                        manager.addTask(task);
                    }
                } catch (Exception e) {
                    throw new ManagerSaveException("Ошибка формата строки: " + line + " в файле: " + file.getPath(),
                            file, e);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + file.getPath(), file, e);
        }

        return manager;
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW,
                    Duration.ofMinutes(30), LocalDateTime.now());
            Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS,
                    Duration.ofMinutes(40), LocalDateTime.now().plusHours(1));

            manager.addTask(task1);
            manager.addTask(task2);

            Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
            manager.addEpic(epic1);

            Subtask subtask1 = new Subtask("Подзадача 1 для эпика 1", "Описание подзадачи",
                    TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2), epic1.getId());
            manager.addSubtask(subtask1);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

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

        } catch (ManagerSaveException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
