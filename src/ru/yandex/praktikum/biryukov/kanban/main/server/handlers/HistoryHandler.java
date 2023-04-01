package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class HistoryHandler extends PatternHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        setTaskManager(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setExchange(exchange);

        if (getRequestLength() == 3) {
            if (getTaskType().equals("history")) {
                if (getQuery() == null) {
                    if (getRequestMethod().equals("GET")) {
                        getHistory(exchange);
                    }
                    if (getRequestMethod().equals("POST")) {
                        addHistory(exchange);
                    }
                }
            }
        } else {
            writeResponse(exchange, "Что-то пошло не так", 500);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(getTaskManager().getHistory()), 200);
    }

    private void addHistory(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(body, Task.class);
            getTaskManager().getHistory().add(task);
            if (getTaskManager().getHistory().get(task.getId()) != null) {
                writeResponse(exchange, gson.toJson(getTaskManager().getHistory().get(task.getId())), 201);
            } else {
                writeResponse(exchange, "Не пройдена валидация. " +
                        "Время выполнения задачи не должно пересекаться с уже созданной", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }
}
