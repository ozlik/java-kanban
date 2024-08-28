package model;

import java.util.Objects;

public class Task {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskType type = TaskType.TASK; //откорректировала, так действительно лучше

    public Task(String title, String description, TaskStatus status) {

        this.title = title;
        this.description = description;
        this.status=status;
        //здесь проставила статус при создании задачи, как лучше сделать? на уровне конструктора или в методе?
    }

    public Task(String title, String description) {
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

    public void setType(TaskType type) {
        this.type = type;
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
