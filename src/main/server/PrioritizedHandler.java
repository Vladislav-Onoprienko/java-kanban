package main.server;

import main.controllers.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String path = h.getRequestURI().getPath();

            if (!path.equals("/prioritized")) {
                sendBadRequest(h, "Invalid path format");
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                sendData(h, taskManager.getPrioritizedTasks());
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
