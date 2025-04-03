package main.server;

import main.controllers.TaskManager;
import main.exceptions.NotFoundException;
import main.exceptions.TimeConflictException;
import main.model.Subtask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                default: sendMethodNotAllowed(h, "GET, POST, DELETE");
            }
        } catch (NotFoundException e) {
            sendNotFound(h);
        } catch (TimeConflictException e) {
            sendHasInteractions(h);
        } catch (Exception e) {
            sendInternalError(h);
        }
    }

    private void handleGet(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        try {
            switch (pathParts.length) {
                case 2:
                    sendData(h, taskManager.getAllSubtasks());
                    break;
                case 3:
                    int id = extractId(pathParts[2]);
                    sendData(h, taskManager.getSubtaskById(id));
                    break;
                default:
                    sendBadRequest(h, "Invalid path format");
            }
        } catch (IllegalArgumentException e) {
            sendBadRequest(h, e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        if (body == null || body.isBlank()) {
            sendBadRequest(h, "Request body is empty");
            return;
        }

        try {
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            if (subtask.getTaskName() == null || subtask.getDescription() == null || subtask.getEpicId() == 0) {
                sendBadRequest(h, "Subtask name, description and epicId are required");
                return;
            }
            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }
            sendCreated(h);
        } catch (Exception e) {
            sendBadRequest(h, "Invalid Subtask data");
        }
    }

    private void handleDelete(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        if (pathParts.length == 3) {
            int id = extractId(pathParts[2]);
            taskManager.deleteTaskById(id);
            sendData(h, "");
        } else {
            sendBadRequest(h, "Invalid path format");
        }
    }
}
