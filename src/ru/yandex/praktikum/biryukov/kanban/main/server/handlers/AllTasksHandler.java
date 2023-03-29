package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.enums.Endpoint;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.HttpTaskServer;

import java.io.IOException;

public class AllTasksHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public AllTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case GET_ACTUAL_TASKS:
                getAllTasks(exchange);
                break;
            case UNKNOWN:
                HttpTaskServer.writeResponse(exchange, "Что-то пошло не так", 500);
                break;

        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks") && query == null && requestMethod.equals("GET")) {
            return Endpoint.GET_ACTUAL_TASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
