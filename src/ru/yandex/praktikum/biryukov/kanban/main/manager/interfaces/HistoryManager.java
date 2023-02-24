package ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces;

import ru.yandex.praktikum.biryukov.kanban.main.data.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
