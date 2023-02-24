package ru.yandex.praktikum.biryukov.kanban.tests;

import ru.yandex.praktikum.biryukov.kanban.main.manager.memory.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}