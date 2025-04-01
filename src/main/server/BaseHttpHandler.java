package main.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected final Gson gson;

    protected BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected void sendData(HttpExchange h, Object data) throws IOException {
        if (data == null) {
            sendNotFound(h);
        } else {
            sendText(h, gson.toJson(data), 200);
        }
    }

    protected void sendCreated(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201, -1);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendText(h, "Not Found", 404);
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        sendText(h, "Not Acceptable", 406);
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        sendText(h, "Internal Server Error", 500);
    }

    protected String readRequestBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected int extractId(String idStr) throws NotFoundException {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Invalid ID format");
        }
    }
}
