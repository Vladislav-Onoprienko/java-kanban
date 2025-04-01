package server;

import main.exceptions.TimeConflictException;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeConflictTest extends HttpTaskServerTestBase {
    private LocalDateTime testTime;
    private Epic testEpic;

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        super.setUp();
        testTime = LocalDateTime.of(2025, 3, 31, 10, 0);
        testEpic = new Epic("Эпик", "Описание эпика");
        manager.addEpic(testEpic);
    }

    //Проверяем обнаружение конфликта времени для обычных задач
    @Test
    void shouldDetectTaskTimeConflict() {
        Task task1 = createTestTask("Задача 1", testTime);
        manager.addTask(task1);

        Task conflictingTask = createTestTask("Задача 2", testTime.plusMinutes(30));
        assertThrows(TimeConflictException.class,
                () -> manager.addTask(conflictingTask));
    }

    //Проверяем обнаружение конфликта времени для подзадач
    @Test
    void shouldDetectSubtaskTimeConflict() {
        Subtask sub1 = createTestSubtask("Подзадача 1", testTime);
        manager.addSubtask(sub1);

        Subtask conflictingSub = createTestSubtask("Подзадача 2", testTime.plusMinutes(30));
        assertThrows(TimeConflictException.class,
                () -> manager.addSubtask(conflictingSub));
    }

    //Проверяем возврат статуса 406 при конфликте времени задач через HTTP
    @Test
    void shouldReturn406ForTaskConflictViaHttp() throws IOException, InterruptedException {
        manager.addTask(createTestTask("Задача 1", testTime));

        HttpResponse<String> response = sendConflictRequest(
                createTestTask("Задача 2", testTime.plusMinutes(30)),
                "/tasks");

        assertEquals(406, response.statusCode());
    }

    //Проверяем возврат статуса 406 при конфликте времени подзадач через HTTP
    @Test
    void shouldReturn406ForSubtaskTimeConflictViaHttp() throws IOException, InterruptedException {
        manager.addSubtask(createTestSubtask("Подзадача 1", testTime));

        HttpResponse<String> response = sendConflictRequest(
                createTestSubtask("Подзадача 2", testTime.plusMinutes(30)),
                "/subtasks");

        assertEquals(406, response.statusCode());
    }

    private Task createTestTask(String name, LocalDateTime startTime) {
        return new Task(name, "Описание", TaskStatus.NEW,
                Duration.ofHours(1), startTime);
    }

    private Subtask createTestSubtask(String name, LocalDateTime startTime) {
        return new Subtask(name, "Описание", TaskStatus.NEW,
                Duration.ofHours(1), startTime, testEpic.getId());
    }

    private HttpResponse<String> sendConflictRequest(Task task, String endpoint)
            throws IOException, InterruptedException {

        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}