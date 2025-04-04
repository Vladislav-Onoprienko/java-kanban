package server;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryAndPriorityTest extends HttpTaskServerTestBase {

    //Проверяет получение истории просмотров задач
    @Test
    void shouldGetTaskHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        manager.addTask(task);
        manager.getTaskById(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача"));
    }

    //Проверяем правильность сортировки задач по приоритету
    @Test
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());

        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().indexOf("Задача 2") < response.body().indexOf("Задача 1"));
    }
}
