package model;

import java.util.Objects;

public class Task {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskType type;

    public Task(String title, String description) {

        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus status, TaskType type) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public Task(String title, String description, TaskStatus status) {

        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task() {
    }

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Task(Integer id, TaskType type, TaskStatus status, String title, String description) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.title = title;
        this.description = description;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {

        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) &&
                status == task.status;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
