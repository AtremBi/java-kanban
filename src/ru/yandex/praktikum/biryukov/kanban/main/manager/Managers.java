package ru.yandex.praktikum.biryukov.kanban.main.manager;

import ru.yandex.praktikum.biryukov.kanban.main.manager.data.server.HttpTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.data.ramMemory.InMemoryHistoryManager;

import java.net.URI;

public class Managers {
    public TaskManager getDefault(){
        return new HttpTaskManager(URI.create("http://localhost:8078"));
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
