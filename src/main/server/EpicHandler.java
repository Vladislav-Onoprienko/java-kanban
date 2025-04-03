package main.server;

import main.controllers.TaskManager;
import main.exceptions.NotFoundException;
import main.model.Epic;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    handleGet(h, pathParts);
                    break;
                case "POST":
                    handlePost(h);
                    break;
                case "DELETE":
                    handleDelete(h, pathParts);
                    break;
                default:
                    sendMethodNotAllowed(h, "GET, POST, DELETE");
            }
        } catch (NotFoundException e) {
            sendNotFound(h);
        } catch (Exception e) {
            sendInternalError(h);
        }
    }

    private void handleGet(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        switch (pathParts.length) {
            case 2:
                sendData(h, taskManager.getAllEpics());
                break;
            case 3:
                int id = extractId(pathParts[2]);
                sendData(h, taskManager.getEpicById(id));
                break;
            case 4:
                if ("subtasks".equals(pathParts[3])) {
                    int epicId = extractId(pathParts[2]);
                    if (taskManager.getEpicById(epicId) == null) {
                        sendNotFound(h);
                    } else {
                        sendData(h, taskManager.getSubtasksOfEpic(epicId));
                    }
                } else {
                    sendBadRequest(h, "Invalid path format");
                }
                break;
            default:
                sendBadRequest(h, "Invalid path format");
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        if (body == null || body.isBlank()) {
            sendBadRequest(h, "Request body is empty");
            return;
        }

        try {
            Epic epic = GSON.fromJson(body, Epic.class);
            if (epic.getTaskName() == null || epic.getDescription() == null) {
                sendBadRequest(h, "Epic name and description fields are required");
                return;
            } if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }
            sendCreated(h);
        } catch (Exception e) {
            sendBadRequest(h, "Invalid Epic data");
        }
    }

    private void handleDelete(HttpExchange h, String[] pathParts) throws IOException, NotFoundException {
        if (pathParts.length == 3) {
            int id = extractId(pathParts[2]);
            taskManager.deleteTaskById(id);
            sendData(h, "");
        } else {
            sendNotFound(h);
        }
    }
}