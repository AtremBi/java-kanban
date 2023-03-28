package ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.server.KVTaskClient;

import java.net.URI;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(URI kvUrl){
        super();
        try {
            loadFromServer();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        kvTaskClient = new KVTaskClient(URI.create(kvUrl.toString()));
    }

    @Override
    public void save(){
        kvTaskClient.put("task", gson.toJson(getTaskList()));
        kvTaskClient.put("subtask", gson.toJson(getSubTaskList()));
        kvTaskClient.put("epic", gson.toJson(getEpicList()));
        kvTaskClient.put("history", gson.toJson(getHistory()));
    }

    public void loadFromServer(){
        try {
            if (!kvTaskClient.load("task").isEmpty()){
                List<Task> tasks = List.of(gson.fromJson(kvTaskClient.load("task"), Task.class));
                for (Task task : tasks) {
                    getTaskMap().put(task.getId(), task);
                }
            }
            if (!kvTaskClient.load("subtask").isEmpty()){
                List<SubTask> subTasks = List.of(gson.fromJson(kvTaskClient.load("subrask"), SubTask.class));
                for (SubTask subTask : subTasks) {
                    getSubTaskMap().put(subTask.getId(), subTask);
                }
            }
            if (!kvTaskClient.load("epic").isEmpty()){
                List<Epic> epics = List.of(gson.fromJson(kvTaskClient.load("epic"), Epic.class));
                for (Epic epic : epics) {
                    getEpicMap().put(epic.getId(), epic);
                }
            }
            if (!kvTaskClient.load("history").isEmpty()){
                List<Task> history = List.of(gson.fromJson(kvTaskClient.load("history"), Task.class));
                for (Task task : history) {
                    getHistory().add(task);
                }
            }
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }
    }
}
