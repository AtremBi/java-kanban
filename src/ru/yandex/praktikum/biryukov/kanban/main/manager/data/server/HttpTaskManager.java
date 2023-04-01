package ru.yandex.praktikum.biryukov.kanban.main.manager.data.server;

import com.google.gson.*;
import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.manager.data.file.FileBackedTasksManager;
import ru.yandex.praktikum.biryukov.kanban.main.KVclient.KVTaskClient;

import java.net.URI;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

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

    private JsonObject jsonParse(String key) throws JsonSyntaxException{
        JsonElement jsonTask = JsonParser.parseString(kvTaskClient.load(key));
        JsonArray tasksList = jsonTask.getAsJsonArray();
        JsonObject jsonObject = null;
        if (!tasksList.isEmpty()){
            for (JsonElement json : tasksList) {
                jsonObject = json.getAsJsonObject();
            }
        }
        return jsonObject;
    }

    private void loadFromServer(){
        try {
            getTaskMap().put(gson.fromJson(jsonParse("task"), Task.class).getId(),
                    gson.fromJson(jsonParse("task"), Task.class));
            getSubTaskMap().put(gson.fromJson(jsonParse("subtask"), SubTask.class).getId(),
                    gson.fromJson(jsonParse("subtask"), SubTask.class));
            getEpicMap().put(gson.fromJson(jsonParse("epic"), Epic.class).getId(),
                    gson.fromJson(jsonParse("epic"), Epic.class));
            getHistory().add(gson.fromJson(jsonParse("history"), Task.class));
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }
    }
}
