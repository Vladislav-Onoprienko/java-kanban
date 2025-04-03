package server;

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

class TaskHandlerTest extends HttpTaskServerTestBase {

    //Проверяем создание задачи с указанием времени выполнения
    @Test
    void shouldCreateTaskWithTime() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task createdTask = manager.getTaskById(1);
        assertNotNull(createdTask);
        assertEquals("Задача 1", createdTask.getTaskName());
        assertEquals(Duration.ofMinutes(30), createdTask.getDuration());
    }

    //Проверяем обновление статуса задачи через HTTP-запрос
    @Test
    void shouldUpdateTaskStatus() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    //Проверяем получение списка всех задач
    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.addTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"));
        assertTrue(response.body().contains("Задача 2"));
    }

    //Проверяем обработку запроса с некорректным путем
    @Test
    void shouldReturn400ForInvalidPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/123/invalid"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());  // Раньше было 404
        assertTrue(response.body().contains("Invalid path format"));
    }

    //Проверяем обработку запроса несуществующей задачи
    @Test
    void shouldReturn404ForNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1111"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    //Проверяем, что неподдерживаемый метод возвращает ошибку 405
    @Test
    void shouldReturn405ForInvalidMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.noBody())  // Неподдерживаемый метод
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());  // Раньше было 404
        assertTrue(response.headers().map().get("Allow").contains("GET, POST, DELETE"));
    }

    //Проверяем удаление задачи через HTTP-запрос
    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        manager.addTask(task);
        manager.deleteTaskById(task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskById(task.getId()));
    }
}