package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.exceptions.NotFoundException;
import main.server.adapters.DurationTypeAdapter;
import main.server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler {
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

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
            sendText(h, GSON.toJson(data), 200);
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

    protected void sendBadRequest(HttpExchange h, String message) throws IOException {
        sendText(h, "Bad Request: " + message, 400);
    }

    protected void sendMethodNotAllowed(HttpExchange h, String allowedMethods) throws IOException {
        h.getResponseHeaders().set("Allow", allowedMethods);
        sendText(h, "Method Not Allowed", 405);
    }

    protected String readRequestBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected int extractId(String idStr) throws NotFoundException {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    public static Gson getGson() {  // Геттер
        return GSON;
    }
}
