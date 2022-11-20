import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    public HashMap<Integer, Task> taskMap = new HashMap<>();
    public HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    public HashMap<Integer, Epic> epicMap = new HashMap<>();
    public int newId = 1;
    private final String statusNew = "NEW";
    private final String statusDone = "DONE";
    private final String statusInProgress = "IN_PROGRESS";

    public void saveTaskMap(Task task){
        task.setId(newId++);
        taskMap.put(task.getId(), task);
    }

    public void saveSubTask(SubTask subTask){
        subTask.setId(newId++);
        subTaskMap.put(subTask.getId(), subTask);
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
            subTaskMap.get(subTask).setEpicId(epic.getId());
            status = subTaskMap.get(subTask).getStatus();

            if (status.equals(statusNew)){
                checkNew++;
            } else if(status.equals(statusDone)){
                checkDone++;
            }
        }

        if (epic.getSubTasks().size()== checkNew && checkNew != 0){
            status = statusNew;
        } else if (epic.getSubTasks().size() == checkDone && checkDone != 0){
            status = statusDone;
        } else {
            status = statusInProgress;
        }

        epicMap.get(epic.getId()).setStatus(status);
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
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
        ArrayList<SubTask> subTaskId = new ArrayList<>();
        for(int subTask : subTaskMap.keySet()){
            if (subTaskMap.get(subTask).getEpicId() == epicId){
                subTaskId.add(subTaskMap.get(subTask));
            }
        }
        return subTaskId;
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
        syncEpic(epicMap.get(subTask.epicId));
    }

    public void removeTaskById(int id){
        taskMap.remove(id);
    }

    public void removeSubTaskById(int id){
        subTaskMap.remove(id);
        for (int epic : epicMap.keySet()) {
            syncEpic(epicMap.get(epic));
        }
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
        subTaskMap.clear();
    }

    public void clearEpic(){
        epicMap.clear();
    }

    public void clearAll(){
        clearAllTask();
        clearAllSubTask();
        clearEpic();
    }
}
