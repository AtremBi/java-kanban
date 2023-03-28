package ru.yandex.praktikum.biryukov.kanban.main.dataManage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.main.TaskManagerTest;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage.HttpTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus.NEW;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;

    @Override
    protected HttpTaskManager createTaskManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        return new HttpTaskManager(URI.create("http://localhost:8078"));
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void loadFromServer() {
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);
        String token = null;
        String body = null;

        URI uri = URI.create("http://localhost:8078");
        HttpClient client1 = HttpClient.newHttpClient();
        URI urlReg = URI.create(uri + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlReg)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();
        try {
            HttpResponse<String> response = client1.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                token = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        HttpClient client2  = HttpClient.newHttpClient();
        URI urlPut = URI.create(uri + "/load/" + "task" + "?API_TOKEN=" + token);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlPut)
                .header("Accept", "text/html")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client2.send(request1, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                body = response.body();
            }
            else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");

        }

        assertTrue(body.contains(task.getTitle()));
    }
}
