package ru.yandex.praktikum.biryukov.kanban.main.dataManage.ramMemory;

import ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage.ramMemory.InMemoryTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}