package main.controllers;

import main.exceptions.TimeConflictException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
    private int nextId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    //Получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    //Удаление всех задач
    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtaskId);
                }
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic);
        }
    }


    //Получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }


    //Добавление задач, эпиков и подзадач
    @Override
    public void addTask(Task task) {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(currentTask -> hasTimeOverlapping(task, currentTask));

        if (isOverlapping) {
            throw new TimeConflictException("Задача пересекается по времени с уже существующей.");
        }

        task.setId(nextId++);
        tasks.put(task.getId(), task);
        historyManager.add(task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        epic.setStatus(TaskStatus.NEW);
        historyManager.add(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(currentTask -> hasTimeOverlapping(subtask, currentTask));

        if (isOverlapping) {
            throw new TimeConflictException("Подзадача пересекается по времени с уже существующей задачей.");
        }

        subtask.setId(nextId++);

        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (!epic.getSubtasksIds().contains(subtask.getId())) {
                epic.getSubtasksIds().add(subtask.getId());
                updateEpicStatus(epic);
                epic.updateTimeAndDuration(this);
            }
        }
        historyManager.add(subtask);
    }


    //Обновление задач, эпиков и подзадач
    @Override
    public void updateTask(Task task) {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(currentTask -> hasTimeOverlapping(task, currentTask) && task.getId() != currentTask.getId());

        if (isOverlapping) {
            throw new TimeConflictException("Задача пересекается по времени с уже существующей.");
        }

        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(oldTask);

            tasks.put(task.getId(), task);
            historyManager.add(task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        historyManager.add(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> hasTimeOverlapping(subtask, existingTask)
                        && subtask.getId() != existingTask.getId());

        if (isOverlapping) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с уже существующей задачей.");
        }

        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                epic.updateTimeAndDuration(this);
            }
            historyManager.add(subtask);
        }
    }


    //Обновление статуса Epic
    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = getSubtasksOfEpic(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        boolean allNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    // Удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.remove(id);
            prioritizedTasks.remove(task);
            historyManager.remove(id);
            return;
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Integer subtaskId : epic.getSubtasksIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasksIds().remove((Integer) id);
                updateEpicStatus(epic);
                epic.updateTimeAndDuration(this);
            }
            historyManager.remove(id);
        }
    }


    // Получение списка подзадач эпика
    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return Optional.ofNullable(epics.get(epicId))
                .map(epic -> epic.getSubtasksIds().stream()
                        .map(subtasks::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }


    //Получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //Получение отсортированного списка задач по приоритету
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }


    //Метод для проверки пересечений двух задач
    private boolean hasTimeOverlapping(Task taskA, Task taskB) {
        LocalDateTime startA = taskA.getStartTime();
        LocalDateTime endA = taskA.getEndTime();
        LocalDateTime startB = taskB.getStartTime();
        LocalDateTime endB = taskB.getEndTime();

        return startA.isBefore(endB) && startB.isBefore(endA);
    }
}
