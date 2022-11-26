package ru.yandex.praktikum.biryukov.kanban.manager;

import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private int newId = 1;
    private final String statusNew = "NEW";
    private final String statusDone = "DONE";
    private final String statusInProgress = "IN_PROGRESS";

    public void saveTask(Task task){
        task.setId(newId++);
        taskMap.put(task.getId(), task);
    }

    public void saveSubTask(SubTask subTask){
        subTask.setId(newId++);
        subTaskMap.put(subTask.getId(), subTask);
        epicMap.get(subTask.getEpicId()).getSubTasks().add(subTask.getId());
        syncEpic(getEpicById(subTask.getEpicId()));
    }

    public void saveEpic(Epic epic){
        epic.setId(newId++);
        epicMap.put(epic.getId(), epic);
    }

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

    public ArrayList<Task> getTaskList() {
        return new ArrayList<Task>(taskMap.values());
    }

    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<SubTask>(subTaskMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<Epic>(epicMap.values());
    }

    public Task getTaskById(int id){
        return taskMap.get(id);
    }

    public SubTask getSubTaskById(int id){
        return subTaskMap.get(id);
    }

    public Epic getEpicById(int id){
        return epicMap.get(id);
    }

    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId){
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for(int subTask : epicMap.get(epicId).getSubTasks()){
                subTasks.add(subTaskMap.get(subTask));
        }
        return subTasks;
    }

    public void updateTask(Task task){
        taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic){
        epicMap.put(epic.getId(), epic);
        syncEpic(epic);
    }

    public void updateSubTask(SubTask subTask){
        subTaskMap.put(subTask.getId(), subTask);
        syncEpic(epicMap.get(subTask.getEpicId()));
    }

    public void removeTaskById(int id){
        taskMap.remove(id);
    }

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

    public void removeEpicById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                subTaskMap.remove(epicMap.get(epic).getSubTasks().get(i));
            }
        }
        epicMap.remove(id);
    }

    public void clearAllTask(){
        taskMap.clear();
    }

    public void clearAllSubTask(){
        for(Epic epic : epicMap.values()){
            epic.getSubTasks().clear();
        }
        subTaskMap.clear();
    }

    public void clearEpic(){
        epicMap.clear();
        subTaskMap.clear();
    }

    public void clearAll(){
        clearAllTask();
        clearAllSubTask();
        clearEpic();
    }
}
