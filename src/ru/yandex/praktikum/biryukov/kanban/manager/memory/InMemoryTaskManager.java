package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private int newId = 1;
    private final String statusNew = String.valueOf(TaskStatus.NEW);
    private final String statusDone = String.valueOf(TaskStatus.DONE);
    private final String statusInProgress = String.valueOf(TaskStatus.IN_PROGRESS);
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void saveTask(Task task){
        task.setId(newId++);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void saveSubTask(SubTask subTask){
        subTask.setId(newId++);
        subTaskMap.put(subTask.getId(), subTask);
        epicMap.get(subTask.getEpicId()).getSubTasks().add(subTask.getId());
        syncEpic(epicMap.get(subTask.getEpicId()));
    }

    @Override
    public void saveEpic(Epic epic){
        epic.setId(newId++);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void syncEpic(Epic epic){
        String status;
        int checkNew = 0;
        int checkDone = 0;

        for (int subTask : epic.getSubTasks()) {
            status = subTaskMap.get(subTask).getStatus();

            if (status.equals(statusNew)) {
                checkNew++;
            } else if (status.equals(statusDone)) {
                checkDone++;
            }
        }

        if (epic.getSubTasks().size() == checkNew || epic.getSubTasks().isEmpty()){
            status = statusNew;
        } else if (epic.getSubTasks().size() == checkDone){
            status = statusDone;
        } else {
            status = statusInProgress;
        }

        epicMap.get(epic.getId()).setStatus(status);
    }

    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<Task>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<SubTask>(subTaskMap.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<Epic>(epicMap.values());
    }

    @Override
    public Task getTaskById(int id){
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id){
        historyManager.add(subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public Epic getEpicById(int id){
        historyManager.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId){
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for(int subTask : epicMap.get(epicId).getSubTasks()){
            subTasks.add(subTaskMap.get(subTask));
        }
        return subTasks;
    }

    @Override
    public void updateTask(Task task){
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic){
        epicMap.put(epic.getId(), epic);
        syncEpic(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask){
        subTaskMap.put(subTask.getId(), subTask);
        syncEpic(epicMap.get(subTask.getEpicId()));
    }

    @Override
    public void removeTaskById(int id){
        taskMap.remove(id);
    }

    @Override
    public void removeSubTaskById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                if (epicMap.get(epic).getSubTasks().get(i).equals(id)){
                    epicMap.get(epic).getSubTasks().remove(i);
                }
            }
            syncEpic(epicMap.get(epic));
        }
        subTaskMap.remove(id);
    }

    @Override
    public void removeEpicById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                subTaskMap.remove(epicMap.get(epic).getSubTasks().get(i));
            }
        }
        epicMap.remove(id);
    }

    @Override
    public void clearAllTask(){
        taskMap.clear();
    }

    @Override
    public void clearAllSubTask(){
        for(Epic epic : epicMap.values()){
            epic.getSubTasks().clear();
        }
        subTaskMap.clear();
    }

    @Override
    public void clearEpic(){
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void clearAll(){
        clearAllTask();
        clearAllSubTask();
        clearEpic();
    }

    @Override
    public ArrayList<Task> getHistory(){
        return historyManager.getHistory();
    }
}