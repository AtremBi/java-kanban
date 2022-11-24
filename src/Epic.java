import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTasks = new ArrayList<>();
    public Epic(String title, String descriptions, String status) {
        super( title, descriptions, status);
    }

    public void addNewTask(int subTask){
        subTasks.add(subTask);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", descriptions='" + descriptions + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
