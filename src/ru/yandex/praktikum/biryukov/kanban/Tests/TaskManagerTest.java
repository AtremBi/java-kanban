package ru.yandex.praktikum.biryukov.kanban.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.data.TaskStatus.DONE;
import static ru.yandex.praktikum.biryukov.kanban.data.TaskStatus.NEW;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    protected void updateTakManager() {
        taskManager = createTaskManager();
    }

    @Test
    public void testGetTaskList(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);

        assertTrue(taskManager.getTaskList().contains(task));
    }

    @Test
    public void testGetTaskListIsEmpty(){
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    public void testGetSubTaskList(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);

        assertTrue(taskManager.getSubTaskList().contains(subTask));
    }

    @Test
    public void testGetSubTaskListIsEmpty(){
        assertTrue(taskManager.getSubTaskList().isEmpty());
    }

    @Test
    public void testGetEpicList(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertTrue(taskManager.getEpicList().contains(epic));
    }

    @Test
    public void testGetEpicListIsEmpty(){
        assertTrue(taskManager.getEpicList().isEmpty());
    }

    @Test
    public void testGetTaskById(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testGetTaskByInvalidId(){
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void testGetSubTaskById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void testGetSubTaskByInvalidId(){
        assertNull(taskManager.getSubTaskById(1));
    }

    @Test
    public void testGetEpicById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testGetEpicByInvalidId(){
        assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void testGetAllSubTaskByEpicId(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask1 = new SubTask("title", "description", NEW);
        subTask1.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);

        SubTask subTask2 = new SubTask("title", "description", NEW);
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask2);

        System.out.println(taskManager.getAllSubTaskByEpicId(epic.getId()));

        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).contains(subTask1));
        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).contains(subTask2));
    }

    @Test
    public void testGetAllSubTaskByInvalidEpicId(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).isEmpty());
    }

    @Test
    public void testUpdateTask(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);
        task.setStatus(DONE);
        taskManager.updateTask(task);

        assertEquals(DONE, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void testUpdateTaskIsEmptyTaskList(){
        Task task = new Task("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateTask(task));
    }

    @Test
    public void testUpdateSubTask(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);
        subTask.setStatus(DONE);
        taskManager.updateSubTask(subTask);

        assertEquals(DONE, taskManager.getSubTaskById(subTask.getId()).getStatus());
    }

    @Test
    public void testUpdateSubTaskIsEmpty(){
        SubTask subTask = new SubTask("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(subTask));
    }

    @Test
    public void testUpdateEpic(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        epic.setStatus(DONE);

        assertEquals(DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void testUpdateEpicIsEmpty(){
        Epic epic = new Epic("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateEpic(epic));
    }

    @Test
    public void testRemoveTaskById(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testRemoveSubTaskById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);
        taskManager.removeSubTaskById(subTask.getId());

        assertNull(taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void testRemoveEpicById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        taskManager.removeEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testClearAllTasks(){
        Task task1 = new Task("title", "description", NEW);
        Task task2 = new Task("title", "description", NEW);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.clearAllTask();

        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    public void testClearAllSubTasks(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title", "description", NEW);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.clearAllSubTask();

        assertTrue(taskManager.getSubTaskList().isEmpty());
    }

    @Test
    public void testClearAllEpic(){
        Epic epic1 = new Epic("title", "description", NEW);
        Epic epic2 = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic2);
        taskManager.saveEpic(epic1);
        taskManager.clearEpic();

        assertTrue(taskManager.getEpicList().isEmpty());
    }

    @Test
    public void testClearAll(){
        Epic epic1 = new Epic("title", "description", NEW);
        Epic epic2 = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic2);
        taskManager.saveEpic(epic1);

        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title", "description", NEW);
        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic2.getId());

        Task task1 = new Task("title", "description", NEW);
        Task task2 = new Task("title", "description", NEW);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);

        taskManager.clearAll();

        assertTrue(taskManager.getSubTaskList().isEmpty());
        assertTrue(taskManager.getSubTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
    }

    @Test
    public void testGetHistory(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);

        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);

        taskManager.getTaskById(task.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());

        assertFalse(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testGetHistoryIsEmpty(){
        assertTrue(taskManager.getHistory().isEmpty());
    }

}
