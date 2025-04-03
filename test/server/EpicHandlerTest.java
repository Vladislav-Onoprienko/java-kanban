package server;

import main.model.Epic;
import main.model.Subtask;
import main.model.TaskStatus;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest extends HttpTaskServerTestBase {

    //Проверяем создание эпика без подзадач
    @Test
    void shouldCreateEpicWithoutSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Epic createdEpic = manager.getEpicById(1);
        assertNotNull(createdEpic);
        assertTrue(createdEpic.getSubtasksIds().isEmpty());
        assertNull(createdEpic.getStartTime());
    }

    //Проверяем получение списка подзадач эпика
    @Test
    void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 2, 16, 13, 0),
                epic1.getId());
        manager.addSubtask(subtask1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача 1"));
    }

    //Проверяем получение всех эпиков
    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        manager.addEpic(new Epic("Эпик 1", "Описание эпика 2"));
        manager.addEpic(new Epic("Эпик 2", "Описание эпика 2"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Эпик 1"));
        assertTrue(response.body().contains("Эпик 2"));
    }

    //Проверяем получение эпика по ID
    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic1.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Эпик 1"));
    }

    //Проверяем обработку запроса несуществующего эпика
    @Test
    void shouldReturn404ForNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Not Found"));
    }

    //Проверяем обработку некорректного пути
    @Test
    void shouldReturn400ForInvalidPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/123/invalid"))
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
                .uri(URI.create("http://localhost:8080/epics"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertTrue(response.headers().map().get("Allow").contains("GET, POST, DELETE"));
    }

    // Проверяем создание эпика с невалидным телом запроса (пустым JSON)
    @Test
    void shouldReturn400ForInvalidEpicJson() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    //Проверяет удаление эпика через HTTP-запрос
    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic1.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(epic1.getId()));
    }
}
