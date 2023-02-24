package ru.yandex.praktikum.biryukov.kanban.main.manager;

import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.fileMange.FileBackedTasksManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.memory.InMemoryHistoryManager;

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
