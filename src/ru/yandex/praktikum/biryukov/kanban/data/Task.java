package ru.yandex.praktikum.biryukov.kanban.data;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String title;
    protected String descriptions;
    protected TaskStatus status;
    public Types type = Types.TASK;

    public Task(String title, String descriptions, TaskStatus status) {
        this.title = title;
        this.descriptions = descriptions;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(descriptions, task.descriptions) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, descriptions, status);
    }

    @Override
    public String toString() {
        return "ru.yandex.praktikum.biryukov.kanban.data.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", descriptions='" + descriptions + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
