package ru.yandex.praktikum.biryukov.kanban.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.TaskStatus;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;
import ru.yandex.praktikum.biryukov.kanban.manager.memory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;
    TaskManager taskManager;

    @BeforeEach
    public void setUp(){
        epic = new Epic("title", "decs", TaskStatus.NEW);
        taskManager = new InMemoryTaskManager();
        subTask1 = new SubTask("title", "desc", TaskStatus.NEW);
        subTask2 = new SubTask("title", "desc", TaskStatus.NEW);
        taskManager.saveEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
    }

    @Test
    public void testEmptyListTasks(){
        taskManager.clearAllSubTask();
        assertTrue(epic.getSubTasks().isEmpty());
    }

    @Test
    public void allSubTasksNew(){
        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void allSubTasksDone(){
        epic.setStatus(TaskStatus.IN_PROGRESS);
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void subTasksNewAndDone(){
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void allSubTasksInProgress(){
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

}