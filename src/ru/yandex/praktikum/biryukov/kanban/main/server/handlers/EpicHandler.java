package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class EpicHandler extends PatternHandler implements HttpHandler {
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
        setExchange(exchange);

        if (getRequestLength() == 3) {
            if (getTaskType().equals("epic")) {
                if (getQuery() == null) {
                    if (getRequestMethod().equals("GET")) {
                        getEpics(exchange);
                    }
                    if (getRequestMethod().equals("POST")) {
                        addEpic(exchange);
                    }
                } else if (isId(getQuery())) {
                    if (getRequestMethod().equals("GET")) {
                        getEpicById(exchange);
                    }
                    if (getRequestMethod().equals("DELETE")) {
                        deleteEpicById(exchange);
                    }
                }
            }
        } else {
            writeResponse(exchange, "Что-то пошло не так", 500);
        }
    }

    private void deleteEpicById(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getEpicList().contains(taskManager.getEpicById(id))) {
                taskManager.removeEpicById(id);
                writeResponse(exchange, "Epic удален", 200);
            } else {
                writeResponse(exchange, "Epic не найден", 404);
            }
        }
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.saveEpic(epic);
            if (taskManager.getEpicById(epic.getId()) != null) {
                writeResponse(exchange, gson.toJson(taskManager.getEpicById(epic.getId())), 201);
            } else {
                writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                writeResponse(exchange, gson.toJson(epic), 200);
            } else {
                writeResponse(exchange, "Эпик не найден", 404);
            }
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getEpicList()), 200);
    }
}
