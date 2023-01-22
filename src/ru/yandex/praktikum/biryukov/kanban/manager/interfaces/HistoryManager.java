package ru.yandex.praktikum.biryukov.kanban.manager.interfaces;

import ru.yandex.praktikum.biryukov.kanban.data.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
