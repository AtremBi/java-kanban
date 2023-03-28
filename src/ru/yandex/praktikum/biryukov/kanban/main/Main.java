package ru.yandex.praktikum.biryukov.kanban.main;

import ru.yandex.praktikum.biryukov.kanban.main.server.HttpTaskServer;
import ru.yandex.praktikum.biryukov.kanban.main.server.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
