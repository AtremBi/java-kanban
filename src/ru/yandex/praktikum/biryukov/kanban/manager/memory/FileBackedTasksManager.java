package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.*;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;

import java.io.*;
import java.util.*;

import static ru.yandex.praktikum.biryukov.kanban.data.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTasksManager(File file){
        this.file = file;
    }

    private void save(){
        addHeader();
        try (Writer writer = new FileWriter(file, true)){
            for (Task task : getTaskList()) {
                writer.write(createTask(task) + "\n");
            }
            for (SubTask subTask : getSubTaskList()) {
                writer.write(createTask(subTask) + "\n");
            }
            for (Epic epic : getEpicList()) {
                writer.write(createTask(epic) + "\n");
            }
            writer.write("\n" + createHistoryString(returnHistoryManager()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        //что бы с начала восстановить все саб таски, а потом уже добавить их в эпики
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains(String.valueOf(SUBTASK))) {
                    fileBackedTasksManager.saveTasksFromString(line);
                }
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            List<Integer> ids = new ArrayList<>();
            while (reader.ready()){
                String line = reader.readLine();
                if(line.contains(String.valueOf(TASK)) || line.contains(String.valueOf(EPIC))){
                    fileBackedTasksManager.saveTasksFromString(line);
                } else if (!line.contains("type")){
                    for (Integer id : FileBackedTasksManager.historyFromString(line)) {
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getTaskMap().get(id));
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getSubTaskMap().get(id));
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getEpicMap().get(id));
                    }
                }
                if(!line.contains("id") && !line.isEmpty()){
                    String[] mas = line.split(", ");
                    ids.add(Integer.parseInt(mas[0]));
                }
            }
            fileBackedTasksManager.newId = Collections.max(ids);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }

    private void saveTasksFromString(String value){
            String[] type = value.split(", ");
            switch (TaskType.valueOf(type[1])){
                case TASK:
                    getTaskMap().put(taskFromString(value).getId(), taskFromString(value));
                    break;
                case SUBTASK:
                    getSubTaskMap().put(subTaskFromString(value).getId(), subTaskFromString(value));
                    break;
                case EPIC:
                    getEpicMap().put(epicFromString(value).getId(), epicFromString(value));
                    for (SubTask subTask : getSubTaskMap().values()) {
                        if (subTask.getEpicId() == epicFromString(value).getId()) {
                            getEpicMap().get(epicFromString(value).getId()).getSubTasks().add(subTask.getId());
                        }
                    }
                    break;
            }
    }

    private void addHeader(){
        try (Writer writer = new FileWriter(file)) {
            writer.write(getHeader() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHeader(){
        StringBuilder columns = new StringBuilder();
        for (ColumnNames column: ColumnNames.values()) {
            columns.append(column).append(", ");
        }
        return columns.deleteCharAt(columns.length() - 2).toString();
    }

    private String createTask(Task task){
        return String.join(", ",
                String.valueOf(task.getId()),
                String.valueOf(task.getType()),
                task.getTitle(),
                String.valueOf(task.getStatus()),
                task.getDescriptions());
    }

    private String createTask(SubTask subTask){
        return String.join(", ",
                String.valueOf(subTask.getId()),
                String.valueOf(subTask.getType()),
                subTask.getTitle(),
                String.valueOf(subTask.getStatus()),
                subTask.getDescriptions(),
                String.valueOf(subTask.getEpicId()));
    }

    private String createTask(Epic epic){
        return String.join(", ",
                String.valueOf(epic.getId()),
                String.valueOf(epic.getType()),
                epic.getTitle(),
                String.valueOf(epic.getStatus()),
                epic.getDescriptions());
    }


    private static String createHistoryString(HistoryManager manager){
        List<String> values = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            values.add(String.valueOf(task.getId()));
        }
        return String.join(", ", values);
    }

    private Task taskFromString(String value){
        String[] line = value.split(", ");
        Task task = new Task(line[2], line[4], TaskStatus.valueOf(line[3]));
        task.setId(Integer.parseInt(line[0]));
        return task;
    }

    private SubTask subTaskFromString(String value){
        String[] line = value.split(", ");
        SubTask subTask = new SubTask(line[2], line[4], TaskStatus.valueOf(line[3]));
                subTask.setId(Integer.parseInt(line[0]));
                subTask.setEpicId(Integer.parseInt(line[5]));
        return subTask;
    }

    private Epic epicFromString(String value){
        String[] line = value.split(", ");
        Epic epic = new Epic(line[2], line[4], TaskStatus.valueOf(line[3]));
        epic.setId(Integer.parseInt(line[0]));
        return epic;
    }

    private static List<Integer> historyFromString(String value){
        String[] line = value.split(", ");
        List<Integer> history = new ArrayList<>();
        if(!value.equals("")){
            for (String str : line) {
                history.add(Integer.parseInt(str));
            }
        }
        return history;
    }

    @Override
    public Task getTaskById(int id){
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id){
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id){
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void saveTask(Task task){
        super.saveTask(task);
        save();
    }

    @Override
    public void saveSubTask(SubTask subTask){
        super.saveSubTask(subTask);
        save();
    }

    @Override
    public void saveEpic(Epic epic){
        super.saveEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id){
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id){
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id){
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearAllTask(){
        super.clearAllTask();
        save();
    }

    @Override
    public void clearAllSubTask(){
        super.clearAllSubTask();
        save();
    }

    @Override
    public void clearEpic(){
        super.clearEpic();
        save();
    }

    @Override
    public void clearAll(){
        super.clearAll();
        save();
    }
}
