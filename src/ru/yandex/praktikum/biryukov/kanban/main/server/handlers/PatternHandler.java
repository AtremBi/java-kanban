package ru.yandex.praktikum.biryukov.kanban.main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PatternHandler {
    private HttpExchange exchange;
    private TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public TaskManager getTaskManager(){
        return taskManager;
    }

    protected void setExchange(HttpExchange exchange){
        this.exchange = exchange;
    }

    protected String getTaskType(){
        String[] path =  getRequestPath().split("/");
        return path[2];
    }

    public void setTaskManager(TaskManager taskManager){
        this.taskManager = taskManager;
    }

    protected int getRequestLength(){
        return getRequestPath().split("/").length;
    }

    protected String getRequestPath(){
        return exchange.getRequestURI().getPath();
    }

    protected String getRequestMethod(){
        return exchange.getRequestMethod();
    }

    protected String getQuery(){
        return exchange.getRequestURI().getQuery();
    }

    public boolean isId(String query) {
        String[] params = query.split("&");
        String param = null;
        for (String p : params) {
            String[] mas = p.split("=");
            for (int i = 0; i < mas.length; i++) {
                if (mas[i].equals("id")) {
                    param = mas[i + 1];
                }
            }
        }
        return param != null;
    }

    public void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public String getParam(String query) {
        String[] params = query.split("&");
        String param = null;
        for (String p : params) {
            String[] mas = p.split("=");
            for (int i = 0; i < mas.length; i++) {
                if (mas[i].equals("id")) {
                    param = mas[i + 1];
                }
            }
        }
        return param;
    }
}
