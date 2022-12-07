package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> viewedTasks = new ArrayList<>();
    @Override
    public ArrayList<Task> getHistory() {
        return viewedTasks;
    }

    @Override
    public void add(Task task){
        if (viewedTasks.size() > 10){
            viewedTasks.remove(0);
        }
        if (task != null){
            viewedTasks.add(task);
        }
    }
}
