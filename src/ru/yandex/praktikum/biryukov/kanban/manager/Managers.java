package ru.yandex.praktikum.biryukov.kanban.manager;

import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.manager.memory.FileBackedTasksManager;
import ru.yandex.praktikum.biryukov.kanban.manager.memory.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public TaskManager getDefault(){
        File file = new File("tasks.csv");
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
