package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> checkHistoryViewingTasks = new ArrayList<>();
    @Override
    public ArrayList<Task> getHistory() {
        return checkHistoryViewingTasks;
    }

    @Override
    public void add(Task task){
        if (checkHistoryViewingTasks.size() > 10){
            checkHistoryViewingTasks.remove(0);
        }
        checkHistoryViewingTasks.add(task);
        for (int i = 0; i < checkHistoryViewingTasks.size(); i++){
            if(checkHistoryViewingTasks.get(i) == null){
                checkHistoryViewingTasks.remove(i);
            }
        }
    }
}
