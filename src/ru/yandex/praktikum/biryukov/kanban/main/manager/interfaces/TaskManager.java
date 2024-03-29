package ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces;

import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    void saveTask(Task task);

    void saveSubTask(SubTask subTask);

    void saveEpic(Epic epic);

    ArrayList<Task> getTaskList();

    ArrayList<SubTask> getSubTaskList();

    ArrayList<Epic> getEpicList();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    ArrayList<SubTask> getAllSubTaskByEpicId(int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    void clearAllTask();

    void clearAllSubTask();

    void clearEpic();

    void clearAll();

    List<Task> getHistory();
}
