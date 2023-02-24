package ru.yandex.praktikum.biryukov.kanban.main.data;

import ru.yandex.praktikum.biryukov.kanban.main.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;
    private LocalDateTime endTime;
    public Epic(String title, String descriptions, TaskStatus status) {
        super( title, descriptions, status);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return type;
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
        return "ru.yandex.praktikum.biryukov.kanban.data.Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", descriptions='" + descriptions + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
