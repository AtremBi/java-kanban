package ru.yandex.praktikum.biryukov.kanban.main.data.ramMemory;

import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.data.ramMemory.InMemoryHistoryManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.data.ramMemory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus.NEW;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    @Test
    public void add_returnAddedTask(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("TEST", "description", NEW);
        inMemoryTaskManager.saveTask(task);
        inMemoryHistoryManager.add(task);

        assertTrue(inMemoryHistoryManager.getHistory().contains(task));
    }
    @Test
    public void getHistory_IsEmpty_returnEmptyList(){
        assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }
    @Test
    public void add_deduplicate(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("TEST", "description", NEW);
        inMemoryTaskManager.saveTask(task);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
    }
    @Test
    public void remove_deletingTaskInEndList_returnTaskListWithoutDeletedTask(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("TEST", "description", NEW);
        Task task2 = new Task("TEST", "description", NEW);
        Task task3 = new Task("TEST", "description", NEW);
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        inMemoryTaskManager.saveTask(task3);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);

        inMemoryHistoryManager.remove(task3.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(task3));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task1));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task2));
    }
    @Test
    public void remove_deletingTaskInMiddleList_returnTaskListWithoutDeletedTask(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("TEST", "description", NEW);
        Task task2 = new Task("TEST", "description", NEW);
        Task task3 = new Task("TEST", "description", NEW);
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        inMemoryTaskManager.saveTask(task3);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);

        inMemoryHistoryManager.remove(task2.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(task2));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task1));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task3));
    }

    @Test
    public void remove_deletingTaskInBeginList_returnTaskListWithoutDeletedTask(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("TEST", "description", NEW);
        Task task2 = new Task("TEST", "description", NEW);
        Task task3 = new Task("TEST", "description", NEW);
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        inMemoryTaskManager.saveTask(task3);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);

        inMemoryHistoryManager.remove(task1.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(task1));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task2));
        assertTrue(inMemoryHistoryManager.getHistory().contains(task3));
    }
}