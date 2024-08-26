package model;

import java.util.Objects;

public class SubTask extends Task {

    Integer epicId;

    public SubTask(String title, String description, TaskStatus status, TaskType type, Integer epicId) {
        super(title, description, status, type);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status) {
        super(title, description, status);
    }

    public SubTask() {
        super();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
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
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {

        return "SubTask{" +
                super.toString() +
                "epicId=" + epicId +
                '}';
    }
}
