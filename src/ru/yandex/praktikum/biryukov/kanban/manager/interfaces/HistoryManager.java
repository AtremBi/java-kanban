package ru.yandex.praktikum.biryukov.kanban.manager.interfaces;

import ru.yandex.praktikum.biryukov.kanban.data.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}
