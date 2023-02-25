package ru.yandex.praktikum.biryukov.kanban.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus.DONE;
import static ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus.NEW;

public abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
     void updateTakManager() {
        taskManager = createTaskManager();
    }

    @Test
    public void addingTaskEqualsFirstTask_secondSubTaskNotAdding(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title123", "description123", DONE);
        subTask1.setStartTime(LocalDateTime.of(2010, 10, 10, 10, 10));
        subTask1.setDuration(Duration.ofMinutes(9999));

        subTask2.setStartTime(LocalDateTime.of(2010, 10, 10, 10, 10));
        subTask2.setDuration(Duration.ofMinutes(9999));

        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        taskManager.updateEpic(epic);

        assertFalse(epic.getSubTasks().contains(subTask2.getId()));
    }

    @Test
    public void addingTaskInsideIntervalFirstTask_secondSubTaskNotAdding(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title123", "description123", DONE);
        subTask1.setStartTime(LocalDateTime.of(2010, 10, 10, 10, 10));
        subTask1.setDuration(Duration.ofMinutes(9999));

        subTask2.setStartTime(LocalDateTime.of(2010, 10, 11, 10, 10));
        subTask2.setDuration(Duration.ofMinutes(999));

        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        taskManager.updateEpic(epic);

        assertFalse(epic.getSubTasks().contains(subTask2.getId()));
    }

    @Test
    public void addingTaskAfterIntervalFirstTask_secondTaskAdding(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title123", "description123", DONE);
        subTask1.setStartTime(LocalDateTime.of(2010, 10, 10, 10, 10));
        subTask1.setDuration(Duration.ofMinutes(9999));

        subTask2.setStartTime(LocalDateTime.of(2010, 10, 17, 10, 10));
        subTask2.setDuration(Duration.ofMinutes(999));

        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        taskManager.updateEpic(epic);

        assertTrue(epic.getSubTasks().contains(subTask2.getId()));
    }

    @Test
    public void addingTaskBeforeIntervalFirstTask_secondTaskAdding(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title123", "description123", DONE);
        subTask1.setStartTime(LocalDateTime.of(2010, 10, 10, 10, 10));
        subTask1.setDuration(Duration.ofMinutes(9999));

        subTask2.setStartTime(LocalDateTime.of(2010, 10, 9, 12,59));
        subTask2.setDuration(Duration.ofMinutes(999));

        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        taskManager.updateEpic(epic);

        assertTrue(epic.getSubTasks().contains(subTask2.getId()));
    }

    @Test
    public void getTaskList_notEmpty(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);

        assertTrue(taskManager.getTaskList().contains(task));
    }

    @Test
    public void getTaskList_IsEmpty(){
        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    public void getSubTaskList_notEmpty(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);

        assertTrue(taskManager.getSubTaskList().contains(subTask));
    }

    @Test
    public void getSubTaskList_IsEmpty(){
        assertTrue(taskManager.getSubTaskList().isEmpty());
    }

    @Test
    public void getEpicList_notEmpty(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertTrue(taskManager.getEpicList().contains(epic));
    }

    @Test
    public void getEpicList_IsEmpty(){
        assertTrue(taskManager.getEpicList().isEmpty());
    }

    @Test
    public void getTaskById_returnTaskById(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void getTask_ByInvalidId_returnNull(){
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void getSubTask_returnSubtaskById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void getSubTask_ByInvalidId_returnNull(){
        assertNull(taskManager.getSubTaskById(1));
    }

    @Test
    public void getEpic_returnEpicById(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void getEpic_ByInvalidId_returnNull(){
        assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void getAllSubTask_returnAllSubtasksByEpicId(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        SubTask subTask1 = new SubTask("title", "description", NEW);
        subTask1.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);

        SubTask subTask2 = new SubTask("title", "description", NEW);
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask2);

        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).contains(subTask1));
        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).contains(subTask2));
    }

    @Test
    public void getAllSubTask_ByInvalidEpicId_returnEmptyList(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);

        assertTrue(taskManager.getAllSubTaskByEpicId(epic.getId()).isEmpty());
    }

    @Test
    public void updateTask(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);
        task.setStatus(DONE);
        taskManager.updateTask(task);

        assertEquals(DONE, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void updateTask_IsEmptyTaskList_returnThrowNullPointerException(){
        Task task = new Task("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateTask(task));
    }

    @Test
    public void updateSubTask(){
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
    public void updateSubTask_IsEmptySubtaskList_returnThrowNullPointerException(){
        SubTask subTask = new SubTask("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(subTask));
    }

    @Test
    public void updateEpic(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        epic.setStatus(DONE);

        assertEquals(DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void updateEpic_IsEmptyEpicList_ThrowNullPointerException(){
        Epic epic = new Epic("title", "description", NEW);

        assertThrows(NullPointerException.class, () -> taskManager.updateEpic(epic));
    }

    @Test
    public void removeTaskById_returnNull(){
        Task task = new Task("title", "description", NEW);
        taskManager.saveTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void removeSubTaskById_returnNull(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        SubTask subTask = new SubTask("title", "description", NEW);
        subTask.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask);
        taskManager.removeSubTaskById(subTask.getId());

        assertNull(taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void removeEpicById_returnNull(){
        Epic epic = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic);
        taskManager.removeEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void clearAllTasks_returnEmptyTaskList(){
        Task task1 = new Task("title", "description", NEW);
        Task task2 = new Task("title", "description", NEW);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.clearAllTask();

        assertTrue(taskManager.getTaskList().isEmpty());
    }

    @Test
    public void clearAllSubTasks_returnEmptySubTaskList(){
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
    public void clearAllEpic_returnEmptyEpicList(){
        Epic epic1 = new Epic("title", "description", NEW);
        Epic epic2 = new Epic("title", "description", NEW);
        taskManager.saveEpic(epic2);
        taskManager.saveEpic(epic1);
        taskManager.clearEpic();

        assertTrue(taskManager.getEpicList().isEmpty());
    }

    @Test
    public void testClearAll_returnTaskSubTaskEpicEmptyList(){
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
    public void getHistory_returnNotEmptyList(){
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
    public void getHistory_IsEmpty_returnEmptyList(){
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void getEmptyListTasksInEpic(){
        Epic epic = new Epic("title", "decs", TaskStatus.NEW);
        assertTrue(epic.getSubTasks().isEmpty());

        assertTrue(epic.getSubTasks().isEmpty());
    }

    @Test
    public void allSubTasksInEpicWith_NewStatus(){
        Epic epic = new Epic("title", "decs", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("title", "desc", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("title", "desc", TaskStatus.NEW);
        taskManager.saveEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void allSubTasksInEpicWith_DoneStatus(){
        Epic epic = new Epic("title", "decs", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("title", "desc", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("title", "desc", TaskStatus.NEW);
        taskManager.saveEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        epic.setStatus(TaskStatus.IN_PROGRESS);
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void subTasksInEpicWith_NewAndDoneStatuses(){
        Epic epic = new Epic("title", "decs", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("title", "desc", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("title", "desc", TaskStatus.NEW);
        taskManager.saveEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void allSubTasksInEpicWith_InProgressStatus(){
        Epic epic = new Epic("title", "decs", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("title", "desc", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("title", "desc", TaskStatus.NEW);
        taskManager.saveEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);

        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

}
