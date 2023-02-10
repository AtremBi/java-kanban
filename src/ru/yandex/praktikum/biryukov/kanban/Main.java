package ru.yandex.praktikum.biryukov.kanban;

import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;

import static ru.yandex.praktikum.biryukov.kanban.data.TaskStatus.*;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("title", "description", NEW);
        Task task2 = new Task("title", "description", NEW);
        SubTask subTask1 = new SubTask("title", "description", NEW);
        SubTask subTask2 = new SubTask("title123", "description123", DONE);
        SubTask subTask3 = new SubTask("title123", "description123", NEW);
        Epic epic1 = new Epic("title", "description", IN_PROGRESS);
        Epic epic2 = new Epic("title", "description", IN_PROGRESS);
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        System.out.println("Тест истории просмотра задач" + taskManager.getHistory());
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubTaskList());
        System.out.println("123" + taskManager.getEpicList());

        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);

        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic2.getId());

        taskManager.saveTask(task1);
        managers.getDefault().saveTask(task2);

        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveSubTask(subTask3);


        System.out.println("Все эпики - " + taskManager.getEpicList());
        System.out.println("Все таски - " + taskManager.getTaskList());
        System.out.println("Все сабтаски - " + taskManager.getSubTaskList());

        subTask1.setStatus(DONE);
        taskManager.updateSubTask(subTask1);



        System.out.println("Проверили смену статуса - " + taskManager.getEpicList());
        System.out.println("Получение всех сабтасков из эпика - " + taskManager.getAllSubTaskByEpicId(epic1.getId()));

//        taskManager.removeEpicById(epic1.getId());
//        System.out.println("Удаление конеретного эпика - " + taskManager.getEpicList());

        managers.getDefault().removeTaskById(task1.getId());
        System.out.println("Удаление таска - " + taskManager.getTaskList());

        taskManager.getTaskById(3);
        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(1);
        taskManager.getEpicById(1);
        taskManager.getSubTaskById(6);


        taskManager.getSubTaskById(10); // проверка на вызов не сущ таска

        System.out.println("Тест истории просмотра задач" + taskManager.getHistory());
    //    taskManager.removeEpicById(epic2.getId());
//        System.out.println("Тест истории просмотра задач" + taskManager.getHistory());
    }
}
