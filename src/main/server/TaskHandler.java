package main.server;

import main.controllers.TaskManager;
import main.exceptions.NotFoundException;
import main.exceptions.TimeConflictException;
import main.model.Task;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET": handleGet(h, pathParts); break;
                case "POST": handlePost(h); break;
                case "DELETE": handleDelete(h, pathParts); break;
                default: sendNotFound(h);
            }
        } catch (NotFoundException e) {
            sendNotFound(h);
        }
        catch (TimeConflictException e) {
            sendHasInteractions(h);
        }
        catch (Exception e) {
            sendInternalError(h);
        }
    }

    private void handleGet(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        switch (pathParts.length) {
            case 2:
                sendData(h, taskManager.getAllTasks());
                break;
            case 3:
                int id = extractId(pathParts[2]);
                sendData(h, taskManager.getTaskById(id));
                break;
            default:
                sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        if (body == null || body.isBlank()) {
            sendInternalError(h);
            return;
        }

        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == 0) {
                taskManager.addTask(task);
            } else {
                taskManager.updateTask(task);
            }
            sendCreated(h);
        } catch (Exception e) {
            throw e;
        }
    }

    private void handleDelete(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        if (pathParts.length != 3) {
            sendNotFound(h);
            return;
        }

        int id = extractId(pathParts[2]);
        taskManager.deleteTaskById(id);
        sendData(h, "");
    }
}
