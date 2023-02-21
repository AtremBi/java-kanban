package ru.yandex.praktikum.biryukov.kanban.Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.fileMange.FileBackedTasksManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.data.TaskStatus.NEW;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file = new File("tasks.csv");

    @AfterEach
    public void deleteFile() {
        file.delete();
    }

    @Override
    FileBackedTasksManager createTaskManager() {
        return FileBackedTasksManager.loadFromFile(file);
    }

    @Test
    public void testingFile() {
        Task task = new Task("TEST", "description", NEW);
        taskManager.saveTask(task);

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals("TEST", fileBackedTasksManager.getTaskById(1).getTitle());
    }

    @Test
    public void testingFileEpicWithoutSubtasks() {
        Epic epic = new Epic("TEST", "description", NEW);
        taskManager.saveEpic(epic);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals("TEST", fileBackedTasksManager.getEpicById(epic.getId()).getTitle());
    }
}