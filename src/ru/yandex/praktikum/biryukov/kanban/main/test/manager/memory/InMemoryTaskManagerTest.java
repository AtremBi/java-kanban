package ru.yandex.praktikum.biryukov.kanban.main.test.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.main.manager.memory.InMemoryTaskManager;
import ru.yandex.praktikum.biryukov.kanban.main.test.manager.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}