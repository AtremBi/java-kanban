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

public class TaskHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    public TaskHandler(TaskManager taskManager){
        this.taskManager = taskManager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case GET_TASK:
                getTaskList(exchange);
                break;
            case GET_TASK_BY_ID:
                getTaskById(exchange);
                break;
            case POST_TASK:
                addTask(exchange);
                break;
            case DELETE_TASK:
                deleteTaskById(exchange);
                break;
            case UNKNOWN:
                HttpTaskServer.writeResponse(exchange, "Что-то пошло не так", 500);
                break;
        }
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        String param = HttpTaskServer.getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getTaskList().contains(taskManager.getTaskById(id))) {
                taskManager.removeTaskById(id);
                HttpTaskServer.writeResponse(exchange, "Таск удален", 200);
            } else {
                HttpTaskServer.writeResponse(exchange, "Таск не найден", 404);
            }
        }
    }

    private void getTaskList(HttpExchange exchange) throws IOException {
        HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getTaskList()), 200);
    }

    private void addTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(body, Task.class);
            taskManager.saveTask(task);
            if (taskManager.getTaskById(task.getId()) != null) {
                HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getTaskById(task.getId())), 201);
            } else {
                HttpTaskServer.writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            HttpTaskServer.writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        String param = HttpTaskServer.getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getTaskList().contains(taskManager.getTaskById(id))) {
                HttpTaskServer.writeResponse(exchange, gson.toJson(taskManager.getTaskById(id)), 200);
            } else {
                HttpTaskServer.writeResponse(exchange, "Таск не найден", 404);
            }
        }
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            if (pathParts[2].equals("task")) {
                if (query == null) {
                    if (requestMethod.equals("GET")) {
                        return Endpoint.GET_TASK;
                    }
                    if (requestMethod.equals("POST")) {
                        return Endpoint.POST_TASK;
                    }
                } else if (HttpTaskServer.isId(query)) {
                    if (requestMethod.equals("GET")) {
                        return Endpoint.GET_TASK_BY_ID;
                    }
                    if (requestMethod.equals("DELETE")) {
                        return Endpoint.DELETE_TASK;
                    }
                }
            }
        }
        return Endpoint.UNKNOWN;
    }
}