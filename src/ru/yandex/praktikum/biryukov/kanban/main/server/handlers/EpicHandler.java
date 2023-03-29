package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.enums.Endpoint;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.HttpTaskServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class EpicHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case GET_EPICS:
                getEpics(exchange);
            case GET_EPIC_BY_ID:
                getEpicById(exchange);
                break;
            case POST_EPIC:
                addEpic(exchange);
                break;
            case DELETE_EPIC:
                deleteEpicById(exchange);
                break;
            case UNKNOWN:
                HttpTaskServer.writeResponse(exchange, "Что-то пошло не так", 500);
                break;
        }
    }

    private void deleteEpicById(HttpExchange exchange) throws IOException {
        String param = HttpTaskServer.getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getEpicList().contains(taskManager.getEpicById(id))) {
                taskManager.removeEpicById(id);
                HttpTaskServer.writeResponse(exchange, "Epic удален", 200);
            } else {
                HttpTaskServer.writeResponse(exchange, "Epic не найден", 404);
            }
        }
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.saveEpic(epic);
            if (taskManager.getEpicById(epic.getId()) != null) {
                HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getEpicById(epic.getId())), 201);
            } else {
                HttpTaskServer.writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            HttpTaskServer.writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        String param = HttpTaskServer.getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getEpicList().contains(taskManager.getEpicById(id))) {
                HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getEpicById(id)), 200);
            } else {
                HttpTaskServer.writeResponse(exchange, "Эпик не найден", 404);
            }
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getEpicList()), 200);
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            if (pathParts[2].equals("epic")) {
                if (query == null) {
                    if (requestMethod.equals("GET")) {
                        return Endpoint.GET_EPICS;
                    }
                    if (requestMethod.equals("POST")) {
                        return Endpoint.POST_EPIC;
                    }
                } else if (HttpTaskServer.isId(query)) {
                    if (requestMethod.equals("GET")) {
                        return Endpoint.GET_EPIC_BY_ID;
                    }
                    if (requestMethod.equals("DELETE")) {
                        return Endpoint.DELETE_EPIC;
                    }
                }
            }
        }
        return Endpoint.UNKNOWN;
    }
}
