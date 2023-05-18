package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TaskHandler extends PatternHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setExchange(exchange);

        if (getRequestLength() == 3) {
            if (getTaskType().equals("task")) {
                if (getQuery() == null) {
                    if (getRequestMethod().equals("GET")) {
                        getTaskList(exchange);
                    }
                    if (getRequestMethod().equals("POST")) {
                        addTask(exchange);
                    }
                }
                else if (isId(getQuery())) {
                    if (getRequestMethod().equals("GET")) {
                        getTaskById(exchange);
                    }
                    if (getRequestMethod().equals("DELETE")) {
                        deleteTaskById(exchange);
                    }
                }
            }
        }
            writeResponse(exchange, "ERROR", 500);
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getTaskList().contains(taskManager.getTaskById(id))) {
                taskManager.removeTaskById(id);
                writeResponse(exchange, "Таск удален", 200);
            } else {
                writeResponse(exchange, "Таск не найден", 404);
            }
        }
    }

    private void getTaskList(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getTaskList()), 200);
    }

    private void addTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(body, Task.class);
            taskManager.saveTask(task);
            if (taskManager.getTaskById(task.getId()) != null) {
                writeResponse(exchange, gson.toJson(taskManager.getTaskById(task.getId())), 201);
            } else {
                writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                writeResponse(exchange, gson.toJson(task), 200);
            } else {
                writeResponse(exchange, "Таск не найден", 404);
            }
        }
    }
}