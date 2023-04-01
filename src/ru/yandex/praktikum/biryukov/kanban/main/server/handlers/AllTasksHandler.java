package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import java.io.IOException;

public class AllTasksHandler extends PatternHandler implements HttpHandler {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public AllTasksHandler(TaskManager taskManager) {
        setTaskManager(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setExchange(exchange);
        String[] path =  getRequestPath().split("/");
        if (getRequestLength() == 2 &&
                path[1].equals("tasks") &&
                getQuery() == null &&
                getRequestMethod().equals("GET")){

            getAllTasks(exchange);

        } else {
                writeResponse(exchange, "Что-то пошло не так", 500);
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(getTaskManager().getPrioritizedTasks()), 200);
    }
}
