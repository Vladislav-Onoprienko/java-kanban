package server;

import main.model.Subtask;
import main.model.Epic;
import main.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest extends HttpTaskServerTestBase {

    // Проверяем получение списка всех подзадач
    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        manager.addSubtask(new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, epic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача 1"));
        assertTrue(response.body().contains("Подзадача 2"));
    }

    // Проверяем получение подзадачи по ID
    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача"));
    }

    // Проверяем обработку запроса несуществующей подзадачи
    @Test
    void shouldReturn404ForNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    // Проверяем обновление статуса подзадачи
    @Test
    void shouldUpdateSubtaskStatus() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(TaskStatus.DONE, manager.getSubtaskById(subtask.getId()).getStatus());
    }

    // Проверяем удаление подзадачи
    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtaskById(subtask.getId()));
    }

    // Проверяем обработку некорректного пути
    @Test
    void shouldReturn400ForInvalidPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/123/invalid"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Invalid path format"));
    }

    // Проверяем, что неподдерживаемый метод возвращает 405
    @Test
    void shouldReturn405ForInvalidMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertTrue(response.headers().map().get("Allow").contains("GET, POST, DELETE"));
    }

    // Проверяем создание подзадачи с невалидным телом запроса (пустым JSON)
    @Test
    void shouldReturn400ForInvalidSubtaskJson() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    // Проверяем обработку неверного формата ID
    @Test
    void shouldReturn400ForInvalidIdFormat() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/abc"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Invalid ID format"));
    }
}