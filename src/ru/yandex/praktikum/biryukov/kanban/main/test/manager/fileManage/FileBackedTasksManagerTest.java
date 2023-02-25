package ru.yandex.praktikum.biryukov.kanban.main.test.manager.fileManage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.fileMange.FileBackedTasksManager;
import ru.yandex.praktikum.biryukov.kanban.main.test.manager.TaskManagerTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus.NEW;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file = new File("tasks.csv");

    @AfterEach
    public void tearDown() {
        file.delete();
    }

    @Override
    protected FileBackedTasksManager createTaskManager() {
        return FileBackedTasksManager.loadFromFile(file);
    }

    @Test
    public void loadFromFile_returnNotEmptyFile() {
        Task task = new Task("TEST", "description", NEW);
        taskManager.saveTask(task);

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals("TEST", fileBackedTasksManager.getTaskById(1).getTitle());
    }

    @Test
    public void loadFromFile_EpicWithoutSubtasks_notReturnError() {
        Epic epic = new Epic("TEST", "description", NEW);
        taskManager.saveEpic(epic);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals("TEST", fileBackedTasksManager.getEpicById(epic.getId()).getTitle());
    }
}