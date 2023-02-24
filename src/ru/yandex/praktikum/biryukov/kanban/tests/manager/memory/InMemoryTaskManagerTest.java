package ru.yandex.praktikum.biryukov.kanban.tests.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.main.manager.memory.InMemoryTaskManager;
import ru.yandex.praktikum.biryukov.kanban.tests.manager.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}