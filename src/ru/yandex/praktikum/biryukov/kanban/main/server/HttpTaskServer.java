package ru.yandex.praktikum.biryukov.kanban.main.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.praktikum.biryukov.kanban.main.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    public Managers managers = new Managers();
    public TaskManager taskManager = managers.getDefault();

    public void start() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new AllTasksHandler(taskManager));
        server.createContext("/tasks/task", new TaskHandler(taskManager));
        server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        server.createContext("/tasks/epic", new EpicHandler(taskManager));
        server.createContext("/tasks/history", new HistoryHandler(taskManager));
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
}
