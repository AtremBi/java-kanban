package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.*;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public void save(){
        addHeader();
        try (Writer writer = new FileWriter("tasks.csv", true)){
            for (Task task : getTaskList()) {
                writer.write(createTaskString(task) + "\n");
            }
            for (SubTask subTask : getSubTaskList()) {
                writer.write(createSubTaskString(subTask) + "\n");
            }
            for (Epic epic : getEpicList()) {
                writer.write(createEpicString(epic) + "\n");
            }
            writer.write("\n" + createHistoryString(returnHistoryManager()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  ManagerSaveException(e.getMessage());
        }
    }

    public String readFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            List<String> lines = new LinkedList<>();
            while (reader.ready()){
                lines.add(reader.readLine());
            }
            lines.remove(0);
            lines.remove(lines.size() - 2);
            for (String line : lines) {
                if (fileBackedTasksManager.taskFromString(line) != null){
                    Task task = fileBackedTasksManager.taskFromString(line);
                    fileBackedTasksManager.getTaskMap().put(task.getId(), task);
                }
                if (fileBackedTasksManager.subTaskFromString(line) != null){
                    SubTask sub = fileBackedTasksManager.subTaskFromString(line);
                    fileBackedTasksManager.getSubTaskMap().put(sub.getId(), sub);
                }
                if (fileBackedTasksManager.epicFromString(line) != null){
                    Epic epic = fileBackedTasksManager.epicFromString(line);
                    fileBackedTasksManager.getEpicMap().put(epic.getId(), epic);
                    for (SubTask subTask : fileBackedTasksManager.getSubTaskMap().values()) {
                        if (subTask.getEpicId() == epic.getId()){
                            epic.getSubTasks().add(subTask.getId());

                        }
                    }
                }
            }
            String line = lines.get(lines.size() - 1);
                for (Integer ids : FileBackedTasksManager.historyFromString(line)) {
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getTaskMap().get(ids));
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getSubTaskMap().get(ids));
                    fileBackedTasksManager.returnHistoryManager().add(fileBackedTasksManager.getEpicMap().get(ids));
                }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }

    public void addHeader(){
        try (Writer writer = new FileWriter("tasks.csv")) {
            writer.write(getHeader() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHeader(){
        StringBuilder columns = new StringBuilder();
        StringBuilder correcting = new StringBuilder();
        ArrayList<String> namesColumns = new ArrayList<>();
        namesColumns.add(Arrays.toString(ColumnNames.values()));
        for (String column: namesColumns) {
            columns.append(column).append(", ");
        }
        String result = columns.deleteCharAt(columns.length() - 1).toString();
        correcting.append(result);
        return correcting.deleteCharAt(0).delete(result.length() - 3, result.length() - 1).toString();
    }

    public String createTaskString(Task task){
        return String.join(", ",
                String.valueOf(task.getId()),
                String.valueOf(task.type),
                task.getTitle(),
                String.valueOf(task.getStatus()),
                task.getDescriptions());
    }

    public String createSubTaskString(SubTask subTask){
        return String.join(", ",
                String.valueOf(subTask.getId()),
                String.valueOf(subTask.type),
                subTask.getTitle(),
                String.valueOf(subTask.getStatus()),
                subTask.getDescriptions(),
                String.valueOf(subTask.getEpicId()));
    }

    public String createEpicString(Epic epic){
        return String.join(", ",
                String.valueOf(epic.getId()),
                String.valueOf(epic.type),
                epic.getTitle(),
                String.valueOf(epic.getStatus()),
                epic.getDescriptions());
    }


    public static String createHistoryString(HistoryManager manager){
        List<String> values = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            values.add(String.valueOf(task.getId()));
        }
        return String.join(", ", values);
    }

    public Task taskFromString(String value){
        String[] line = value.split(", ");
        Task task = null;
        if(line.length > 1){
            if (!line[1].isEmpty() && line[1].equals("TASK")){
                task = new Task(line[2], line[4], TaskStatus.valueOf(line[3]));
                task.setId(Integer.parseInt(line[0]));
            }
        }
        return task;
    }

    public SubTask subTaskFromString(String value){
        String[] line = value.split(", ");
        SubTask subTask = null;
        if(line.length > 1){
            if (!line[1].isEmpty() && line[1].equals("SUBTASK")){
                subTask = new SubTask(line[2], line[4], TaskStatus.valueOf(line[3]));
                subTask.setId(Integer.parseInt(line[0]));
                subTask.setEpicId(Integer.parseInt(line[5]));
            }
        }
        return subTask;
    }

    public Epic epicFromString(String value){
        String[] line = value.split(", ");
        Epic epic = null;
        if(line.length > 1){
            if (!line[1].isEmpty() && line[1].equals("EPIC")){
                epic = new Epic(line[2], line[4], TaskStatus.valueOf(line[3]));
                epic.setId(Integer.parseInt(line[0]));
            }
        }
        return epic;
    }

    public static List<Integer> historyFromString(String value){
        String[] line = value.split(", ");
        List<Integer> history = new ArrayList<>();
        if(line.length > 1){
            if (!line[1].isEmpty()
                    && !line[1].equals(String.valueOf(Types.EPIC))
                    && !line[1].equals(String.valueOf(Types.TASK))
                    && !line[1].equals(String.valueOf(Types.SUBTASK))) {
                for (String str : line) {
                    history.add(Integer.parseInt(str));
                }
            } else if (line[1].isEmpty()){
                for (String str : line) {
                    history.add(Integer.parseInt(str));
                }
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
    public ArrayList<SubTask> getAllSubTaskByEpicId(int epicId){
        ArrayList<SubTask> subTask = super.getAllSubTaskByEpicId(epicId);
        save();
        return subTask;
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
