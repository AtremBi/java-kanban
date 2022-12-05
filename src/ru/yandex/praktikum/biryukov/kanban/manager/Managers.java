package ru.yandex.praktikum.biryukov.kanban.manager;

import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.manager.memory.InMemoryHistoryManager;
import ru.yandex.praktikum.biryukov.kanban.manager.memory.InMemoryTaskManager;

public class Managers {
    public TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
