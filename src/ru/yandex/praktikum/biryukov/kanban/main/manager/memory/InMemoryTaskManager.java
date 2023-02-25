package ru.yandex.praktikum.biryukov.kanban.main.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.main.data.Epic;
import ru.yandex.praktikum.biryukov.kanban.main.data.SubTask;
import ru.yandex.praktikum.biryukov.kanban.main.data.Task;
import ru.yandex.praktikum.biryukov.kanban.main.data.TaskStatus;
import ru.yandex.praktikum.biryukov.kanban.main.manager.Managers;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.HistoryManager;
import ru.yandex.praktikum.biryukov.kanban.main.manager.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected int newId = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> sortedList = new TreeSet<>((Comparator.nullsLast((task1, task2) -> {
        if (task1.getStartTime() != null && task2.getStartTime() != null){
            return task1.getStartTime().compareTo(task2.getStartTime());
        }else if (task1.getStartTime() == null && task2.getStartTime() != null){
            return 1;
        } else if (task1.getStartTime() != null && task2.getStartTime() == null){
            return -1;
        } else {
            return task1.getId() - task2.getId();
        }
    })));

    @Override
    public void saveTask(Task task){
        task.setId(newId++);
        if (checkIntersections(task)){
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void saveSubTask(SubTask subTask){
        subTask.setId(newId++);
        if (checkIntersections(subTask)){
            subTaskMap.put(subTask.getId(), subTask);
            epicMap.get(subTask.getEpicId()).getSubTasks().add(subTask.getId());
            syncEpic(epicMap.get(subTask.getEpicId()));
        }
    }

    @Override
    public void saveEpic(Epic epic){
        epic.setId(newId++);
        epicMap.put(epic.getId(), epic);
    }

    private boolean checkIntersections(Task task){
        boolean valid = true;
        if(task.getStartTime() != null){
            for (Task task1 : getPrioritizedTasks()) {
                if(task1.getStartTime() != null){
                    if(task.getStartTime().isBefore(task1.getEndTime()) &&
                            task.getEndTime().isAfter(task1.getStartTime())){
                        valid = false;
                        System.out.println("Не пройдена валидация. " +
                                "Время выполнения задачи не должно пересекаться с уже созданной");
                    }
                }
            }
        }
        return valid;
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
            status = TaskStatus .DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }

        LocalDateTime minStartTime = null;
        LocalDateTime maxEndTime = null;
        Duration duration = Duration.ofMinutes(0);

        for (SubTask subTask : getAllSubTaskByEpicId(epic.getId())) {
            if (minStartTime == null || subTask.getStartTime().isBefore(Objects.requireNonNull(minStartTime))){
                    minStartTime = subTask.getStartTime();
            }
            if (maxEndTime == null || subTask.getEndTime().isAfter(Objects.requireNonNull(maxEndTime))){
                maxEndTime = subTask.getEndTime();
            }
            duration = duration.plusMinutes(subTask.getDuration().toMinutes());
        }

        getEpicById(epic.getId()).setStatus(status);
        getEpicById(epic.getId()).setStartTime(minStartTime);
        getEpicById(epic.getId()).setDuration(duration);
        getEpicById(epic.getId()).setEndTime(maxEndTime);

    }

    @Override
    public TreeSet<Task> getPrioritizedTasks(){
        sortedList.addAll(taskMap.values());
        sortedList.addAll(subTaskMap.values());

        return sortedList;
    }

    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicMap.values());
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
        sortedList.remove(taskMap.get(id));
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
        sortedList.remove(subTaskMap.get(id));
        historyManager.remove(id);
        subTaskMap.remove(id);
    }

    @Override
    public void removeEpicById(int id){
        for (int epic : epicMap.keySet()) {
            for (int i = 0; i < epicMap.get(epic).getSubTasks().size(); i++) {
                sortedList.remove(subTaskMap.get(epicMap.get(epic).getSubTasks().get(i)));
                subTaskMap.remove(epicMap.get(epic).getSubTasks().get(i));
            }
        }
        historyManager.remove(id);
        for (int i = 0; i < epicMap.get(id).getSubTasks().size(); i++){
            historyManager.remove(epicMap.get(id).getSubTasks().get(i));
        }
        sortedList.remove(epicMap.get(id));
        epicMap.remove(id);
    }

    @Override
    public void clearAllTask(){
        for (Task task : taskMap.values()) {
            sortedList.remove(task);
            historyManager.remove(task.getId());
        }
        taskMap.clear();
    }

    @Override
    public void clearAllSubTask(){
        for(SubTask subTask : subTaskMap.values()){
            sortedList.remove(subTask);
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
            sortedList.remove(epic);
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
