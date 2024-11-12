package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(String title, String description, TaskStatus status, int duration, LocalDateTime startTime, Integer epicId) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, int duration, Integer epicId) {
        super(title, description, status, duration);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, Integer epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", epicId=" + epicId +
                '}';
    }
}
