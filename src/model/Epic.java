package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    public ArrayList<Integer> epicSubtasksId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

     public void addSubTask(Integer id) { //обновление статуса вынесено в обновление подзадачи,
        //все эпики задачи и подзадачи при создании имеют статус нью, для подзадач и задач можем заменить при обновлении
//для эпика меняем при обновлении подзадачи, в методе менеджера происходит поправка на статус
        this.epicSubtasksId.add(id);
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

