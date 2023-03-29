package ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.dataManage.file.FileBackedTasksManager;
import ru.yandex.praktikum.biryukov.kanban.main.KVclient.KVTaskClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(URI kvUrl){
        super();
        kvTaskClient = new KVTaskClient(URI.create(kvUrl.toString()));
        try {
            loadFromServer();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void save(){
        kvTaskClient.put("task", gson.toJson(getTaskList()));
        kvTaskClient.put("subtask", gson.toJson(getSubTaskList()));
        kvTaskClient.put("epic", gson.toJson(getEpicList()));
        kvTaskClient.put("history", gson.toJson(getHistory()));
    }

    private void loadFromServer(){
        ArrayList<Task> tasksList = new ArrayList((List.of(kvTaskClient.load("task"))));
        try {
            if (!tasksList.isEmpty()){
                List<Task> tasks = List.of(gson.fromJson(kvTaskClient.load("task"), Task.class));
                for (Task task : tasks) {
                    getTaskMap().put(task.getId(), task);
                }
            }
            ArrayList<SubTask> subTasksList = new ArrayList(List.of(kvTaskClient.load("subtask")));
            if (!subTasksList.isEmpty()){
                List<SubTask> subTasks = List.of(gson.fromJson(kvTaskClient.load("subtask"), SubTask.class));
                for (SubTask subTask : subTasks) {
                    getSubTaskMap().put(subTask.getId(), subTask);
                }
            }
            ArrayList<Epic> epicList = new ArrayList(List.of(kvTaskClient.load("epic")));
            if (!epicList.isEmpty()){
                List<Epic> epics = List.of(gson.fromJson(kvTaskClient.load("epic"), Epic.class));
                for (Epic epic : epics) {
                    getEpicMap().put(epic.getId(), epic);
                }
            }
            ArrayList<Task> historyList = new ArrayList(List.of(kvTaskClient.load("history")));
            if (!historyList.isEmpty()){
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
