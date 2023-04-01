package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SubtaskHandler extends PatternHandler implements HttpHandler {
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setExchange(exchange);

        if (getRequestLength() == 3) {
            if (getTaskType().equals("subtask")) {
                    if (getQuery() == null) {
                        if (getRequestMethod().equals("GET")) {
                            getSubTask(exchange);
                        }
                        if (getRequestMethod().equals("POST")) {
                            addSubTask(exchange);
                        }
                    } else if (isId(getQuery())) {
                        if (getRequestMethod().equals("GET")) {
                            getSubTaskById(exchange);
                        }
                        if (getRequestMethod().equals("DELETE")) {
                            deleteSubTask(exchange);
                        }
                    }
            }
        } else {
            writeResponse(exchange, "Что-то пошло не так", 500);
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            if (taskManager.getSubTaskList().contains(taskManager.getSubTaskById(id))) {
                taskManager.removeSubTaskById(id);
                writeResponse(exchange, "SubTask удален", 200);
            } else {
                writeResponse(exchange, "SubTask не найден", 404);
            }
        }
    }

    private void addSubTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (!taskManager.getEpicList().isEmpty() && subTask.getEpicId() != 0) {
                taskManager.saveSubTask(subTask);
                if (taskManager.getSubTaskById(subTask.getId()) != null) {
                    writeResponse(exchange, gson.toJson(taskManager.getSubTaskById(subTask.getId())), 201);
                } else {
                    writeResponse(exchange, "Не пройдена валидация. " +
                            "Время выполнения задачи не должно пересекаться с уже созданной", 400);
                }
            } else {
                writeResponse(exchange, "Задача не была добавлена т.к не был передан epicId или " +
                        "список эпиков пуст", 400);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        String param = getParam(exchange.getRequestURI().getQuery());
        if (param != null) {
            int id = Integer.parseInt(param);
            SubTask subTask = taskManager.getSubTaskById(id);
            if (subTask != null) {
                writeResponse(exchange, gson.toJson(subTask), 200);
            } else {
                writeResponse(exchange, "сабтаска не найдена", 404);
            }
        }
    }

    private void getSubTask(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getSubTaskList()), 200);
    }
}
