package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    public ArrayList<SubTask> epicSubTask = new ArrayList<>();

    public Epic(String title, String description, TaskStatus status, TaskType type) {
        super(title, description, status, type);
    }

    public Epic() {
    }

    public void addSubTask(SubTask subtask) {
        this.epicSubTask.add(subtask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return epicSubTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicSubTask, epic.epicSubTask);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(epicSubTask);
    }
}

