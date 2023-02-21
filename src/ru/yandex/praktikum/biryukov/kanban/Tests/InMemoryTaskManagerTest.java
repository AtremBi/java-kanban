package ru.yandex.praktikum.biryukov.kanban.Tests;

import ru.yandex.praktikum.biryukov.kanban.manager.memory.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}