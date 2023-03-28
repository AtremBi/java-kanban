package ru.yandex.praktikum.biryukov.kanban.main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        server.start();

        System.out.println("\n\n" +
                "               (\\ _ /)\n" +
                "СЕРВЕР ЗАПУЩЕН! (^w^)\n" +
                "на порту 8080＿ノ ヽ ノ＼＿ \n" +
                "           /　`/ ⌒Ｙ⌒ Ｙ  \\\n" +
                "          ( 　(三ヽ人　 /   |\n" +
                "         |　ﾉ⌒＼ ￣￣ヽ　 ノ\n" +
                "          ヽ＿＿＿＞､＿＿／\n" +
                "              ｜( 王 ﾉ〈 \n" +
                "              /ﾐ`ー―彡\\ \n" +
                "             |╰       ╯|\n" +
                "             |    /\\   |\n" +
                "             | /     \\ |");
    }

    public void stop(){
        server.stop(0);
    }

    static class TaskHandler implements HttpHandler {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        public Managers managers = new Managers();
        public TaskManager taskManager = managers.getDefault();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(),
                    exchange.getRequestURI().getQuery());
            switch (endpoint) {
                case GET_ACTUAL_TASKS:
                    getAllTasks(exchange);
                    break;
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
                case GET_SUBTASK:
                    getSubTask(exchange);
                    break;
                case GET_SUBTASK_BY_ID:
                    getSubTaskById(exchange);
                    break;
                case POST_SUBTASK:
                    addSubTask(exchange);
                    break;
                case DELETE_SUBTASK:
                    deleteSubTask(exchange);
                    break;
                case POST_HISTORY:
                    addHistory(exchange);
                    break;
                case GET_HISTORY:
                    getHistory(exchange);
                    break;
                case UNKNOWN:
                    writeResponse(exchange, "Что-то пошло не так", 500);
                    break;

            }
        }

        private void getHistory(HttpExchange exchange) throws IOException{
            writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
        }

        private void addHistory(HttpExchange exchange) throws IOException{
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                Task task = gson.fromJson(body, Task.class);
                taskManager.getHistory().add(task);
                if (taskManager.getHistory().get(task.getId()) != null) {
                    writeResponse(exchange, gson.toJson(taskManager.getHistory().get(task.getId())), 201);
                } else {
                    writeResponse(exchange, "Не пройдена валидация. " +
                            "Время выполнения задачи не должно пересекаться с уже созданной", 400);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void deleteSubTask(HttpExchange exchange) throws IOException {
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
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
                if (!taskManager.getEpicList().isEmpty() && subTask.getEpicId() != 0){
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
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
            if (param != null) {
                int id = Integer.parseInt(param);
                if (taskManager.getSubTaskList().contains(taskManager.getSubTaskById(id))) {
                    writeResponse(exchange, gson.toJson(taskManager.getSubTaskById(id)), 200);
                } else {
                    writeResponse(exchange, "сабтаска не найдена", 404);
                }
            }
        }

        private void getSubTask(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getSubTaskList()), 200);
        }

        private void deleteEpicById(HttpExchange exchange) throws IOException {
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
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
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
            if (param != null) {
                int id = Integer.parseInt(param);
                if (taskManager.getEpicList().contains(taskManager.getEpicById(id))) {
                    writeResponse(exchange, gson.toJson(taskManager.getEpicById(id)), 200);
                } else {
                    writeResponse(exchange, "Эпик не найден", 404);
                }
            }
        }

        private void getEpics(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getEpicList()), 200);
        }

        private void deleteTaskById(HttpExchange exchange) throws IOException {
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
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

        private void getAllTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
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
            String[] params = exchange.getRequestURI().getQuery().split("&");
            String param = null;
            for (String p : params) {
                String[] mas = p.split("=");
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i].equals("id")) {
                        param = mas[i + 1];
                    }
                }
            }
            if (param != null) {
                int id = Integer.parseInt(param);
                if (taskManager.getTaskList().contains(taskManager.getTaskById(id))) {
                    writeResponse(exchange, gson.toJson(taskManager.getTaskById(id)), 200);
                } else {
                    writeResponse(exchange, "Таск не найден", 404);
                }
            }
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

        public Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 3) {
                switch (pathParts[2]) {
                    case "task":
                        if (query == null) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_TASK;
                            }
                            if (requestMethod.equals("POST")) {
                                return Endpoint.POST_TASK;
                            }
                        } else if (isId(query)) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_TASK_BY_ID;
                            }
                            if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_TASK;
                            }
                        }
                        break;
                    case "epic":
                        if (query == null) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_EPICS;
                            }
                            if (requestMethod.equals("POST")) {
                                return Endpoint.POST_EPIC;
                            }
                        } else if (isId(query)) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_EPIC_BY_ID;
                            }
                            if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_EPIC;
                            }
                        }
                    case "subtask":
                        if (query == null) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_SUBTASK;
                            }
                            if (requestMethod.equals("POST")) {
                                return Endpoint.POST_SUBTASK;
                            }
                        } else if (isId(query)) {
                            if (requestMethod.equals("GET")) {
                                return Endpoint.GET_SUBTASK_BY_ID;
                            }
                            if (requestMethod.equals("DELETE")) {
                                return Endpoint.DELETE_SUBTASK;
                            }
                        }
                }
            }

            if (pathParts.length == 2 && pathParts[1].equals("tasks") && query == null && requestMethod.equals("GET")) {
                return Endpoint.GET_ACTUAL_TASKS;
            }

            return Endpoint.UNKNOWN;
        }

        public void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }

    enum Endpoint {
        GET_TASK,
        GET_TASK_BY_ID,
        GET_ACTUAL_TASKS,
        POST_TASK,
        DELETE_TASK,
        GET_SUBTASK,
        GET_SUBTASK_BY_ID,
        POST_SUBTASK,
        DELETE_SUBTASK,
        GET_EPICS,
        GET_EPIC_BY_ID,
        POST_EPIC,
        DELETE_EPIC,
        GET_HISTORY,
        POST_HISTORY,
        UNKNOWN
    }
}
