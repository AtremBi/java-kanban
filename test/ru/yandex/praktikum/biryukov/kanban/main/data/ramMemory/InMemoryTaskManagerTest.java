package ru.yandex.praktikum.biryukov.kanban.main.data.ramMemory;

import ru.yandex.praktikum.biryukov.kanban.main.manager.data.ramMemory.InMemoryTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}