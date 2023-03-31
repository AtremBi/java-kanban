package ru.yandex.praktikum.biryukov.kanban.main.dataManage.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.main.TaskManagerTest;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage.server.HttpTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.server.KVServer;

import java.io.IOException;
import java.net.URI;

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
        SubTask subTask = new SubTask("title", "description", NEW);
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.saveTask(task);
        taskManager.saveSubTask(subTask);
        taskManager.getTaskById(task.getId());

        HttpTaskManager manager = new HttpTaskManager(URI.create("http://localhost:8078"));

        assertEquals(manager.getTaskById(task.getId()), task);
        assertEquals(manager.getSubTaskById(subTask.getId()), subTask);
        assertEquals(manager.getEpicById(epic.getId()), epic);
        assertTrue(manager.getHistory().contains(task));
    }
}
