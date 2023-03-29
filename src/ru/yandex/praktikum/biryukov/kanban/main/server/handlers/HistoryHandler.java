package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.enums.Endpoint;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.HttpTaskServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class HistoryHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case GET_HISTORY:
                getHistory(exchange);
                break;
            case POST_HISTORY:
                addHistory(exchange);
                break;
            case UNKNOWN:
                HttpTaskServer.writeResponse(exchange, "Что-то пошло не так", 500);
                break;

        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
    }

    private void addHistory(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(body, Task.class);
            taskManager.getHistory().add(task);
            if (taskManager.getHistory().get(task.getId()) != null) {
                HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getHistory().get(task.getId())), 201);
            } else {
                HttpTaskServer.writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            HttpTaskServer.writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            if (pathParts[2].equals("history")) {
                if (query == null) {
                    if (requestMethod.equals("GET")) {
                        return Endpoint.GET_HISTORY;
                    }
                    if (requestMethod.equals("POST")) {
                        return Endpoint.POST_HISTORY;
                    }
                }
            }
        }

        if (pathParts.length == 2 && pathParts[1].equals("tasks") && query == null && requestMethod.equals("GET")) {
            return Endpoint.GET_ACTUAL_TASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
