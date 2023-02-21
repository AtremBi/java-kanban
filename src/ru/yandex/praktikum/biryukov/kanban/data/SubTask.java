package ru.yandex.praktikum.biryukov.kanban.data;

import ru.yandex.praktikum.biryukov.kanban.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public SubTask(String title, String descriptions, TaskStatus status) {
        super(title, descriptions, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "ru.yandex.praktikum.biryukov.kanban.data.SubTask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", descriptions='" + descriptions + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }
}
