package service;

import model.Epic;
import model.Task;
import model.SubTask;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, SubTask> subtasks = new HashMap<>();

    Epic epic = new Epic();
    SubTask subtask = new SubTask();

    private static class IdGenerator {
        private static int nextId = 0;

        public static int getNextId() {
            return nextId++;
        }
    }
    //методы для Task

    //создаём новую задачу
    public void createTask(Task task) {
        Integer id = IdGenerator.getNextId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void updateTask(Integer id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    public void clearTaskByID(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    public void clearManager() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    //методы для Epic
    public void createEpic(Epic epic) {
        Integer id = IdGenerator.getNextId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public ArrayList<SubTask> getSubTaskByEpic(Integer id) {
        Epic epic = epics.get(id);
        return epic.getSubTasks();
    }

    public void deleteEpicByID(Integer epicId) {
        if (epics.get(epicId) != null) {
            Epic epic = epics.get(epicId);
            ArrayList<SubTask> subtasksToRemove = epic.getSubTasks();
            epics.remove(epicId);
            for (SubTask sb : subtasksToRemove) {
                subtasks.remove(sb.getId());
            }
        }
    }


    //методы для Subtask
    public void createSubtask(SubTask subtask) {
        Integer id = IdGenerator.getNextId();
        subtask.setId(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTask(subtask);
        subtasks.put(id, subtask);
    }

    public void updateSubTask(Integer id, SubTask subtask) { //статус эпика меняется при смене статуса подзадач во время обновления поздадачи
        if (subtasks.containsKey(id)) {
            SubTask subtaskToRemove = subtasks.get(id);
            subtask.setId(id);
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubTasks().remove(subtaskToRemove);
            epic.addSubTask(subtask);
            ArrayList<SubTask> subtasksToCheck = epic.getSubTasks();
            updateEpicStatus(subtasksToCheck);
        } else {
            System.out.println("Такой подзадачи нет");
        }
    }

    private void updateEpicStatus(ArrayList<SubTask> subtaskToCheck) {
        boolean allDone = true;
        for (SubTask subtask : subtaskToCheck) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
                break;
            }
        }
        for (SubTask subtask : subtaskToCheck) {
            if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                Epic epic = epics.get(subtask.getEpicId());
                epic.setStatus(TaskStatus.IN_PROGRESS);
                break;
            }
        }
        if (allDone) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.setStatus(TaskStatus.DONE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Objects.equals(tasks, manager.tasks) && Objects.equals(epics, manager.epics) && Objects.equals(subtasks, manager.subtasks) && Objects.equals(epic, manager.epic) && Objects.equals(subtask, manager.subtask);
    }


    @Override
    public int hashCode() {
        return Objects.hash(tasks, epics, subtasks, epic, subtask);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}