package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> epicSubtasksId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubTask(Integer id) {
        this.epicSubtasksId.add(id);
    }

    public void deleteSubTask(Integer id) {
        this.epicSubtasksId.remove(id);
    }

    public void deleteSubtasks() {
        this.epicSubtasksId.clear();
    }

    public ArrayList<Integer> getSubTasks() {
        return epicSubtasksId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicSubtasksId, epic.epicSubtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", epicSubtasksId=" + epicSubtasksId +
                '}';
    }
}

