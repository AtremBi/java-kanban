package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.data.TaskStatus;
import ru.yandex.praktikum.biryukov.kanban.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected int newId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public HistoryManager returnHistoryManager(){
        return historyManager;
    }

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

    private void syncEpic(Epic epic){
        TaskStatus status;
        int checkNew = 0;
        int checkDone = 0;

        for (int subTask : epic.getSubTasks()) {
            status = subTaskMap.get(subTask).getStatus();

                if (status.equals(TaskStatus.NEW)) {
                    checkNew++;
                } else if (status.equals(TaskStatus.DONE)) {
                    checkDone++;
                }
            }

        if (epic.getSubTasks().size() == checkNew || epic.getSubTasks().isEmpty()){
            status = TaskStatus.NEW;
        } else if (epic.getSubTasks().size() == checkDone){
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
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
    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    protected HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    protected HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    public Task getTaskById(int id){
        Task task = taskMap.get(id);
        historyManager.add(task);
        return task;
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
        historyManager.remove(id);
        taskMap.remove(id);
    }

    @Override
    public void removeSubTaskById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                if (epicMap.get(epic).getSubTasks().get(i).equals(id)){
                    historyManager.remove(i);
                    epicMap.get(epic).getSubTasks().remove(i);
                }
            }
            syncEpic(epicMap.get(epic));
        }
        historyManager.remove(id);
        subTaskMap.remove(id);
    }

    @Override
    public void removeEpicById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                subTaskMap.remove(epicMap.get(epic).getSubTasks().get(i));
            }
        }
        historyManager.remove(id);
        for (int i = 0; i < epicMap.get(id).getSubTasks().size(); i++){
            historyManager.remove(epicMap.get(id).getSubTasks().get(i));
        }
        epicMap.remove(id);
    }

    @Override
    public void clearAllTask(){
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
        taskMap.clear();
    }

    @Override
    public void clearAllSubTask(){
        for(SubTask subTask : subTaskMap.values()){
            historyManager.remove(subTask.getId());
        }

        for(Epic epic : epicMap.values()){
            historyManager.remove(epic.getId());
        }

        for(Epic epic : epicMap.values()){
            epic.getSubTasks().clear();
        }
        subTaskMap.clear();
    }

    @Override
    public void clearEpic(){
        for(Epic epic : epicMap.values()){
            historyManager.remove(epic.getId());
        }
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
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}
