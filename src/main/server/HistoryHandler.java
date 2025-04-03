package main.server;

import main.controllers.TaskManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String path = h.getRequestURI().getPath();

            if (!path.equals("/history")) {
                sendBadRequest(h, "Invalid path format");
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                sendData(h, taskManager.getHistory());
            } else {
                sendMethodNotAllowed(h, "GET");
            }
        } catch (IllegalArgumentException e) {
                sendBadRequest(h, "Invalid parameters");
        } catch (Exception e) {
            sendInternalError(h);
        }
    }
}
