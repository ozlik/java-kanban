package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private static int idCounter = 0;

    private static int getIdCounter() {
        return idCounter++;
    }

    //методы для Task
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskByID(Integer id) {
        return tasks.get(id);
    }

    //создаём новую задачу
    public Task createTask(Task task) {
        if (task != null) {
            Integer id = getIdCounter();
            task.setId(id);
            tasks.put(id, task);
            return task;
        }
        return null;
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return task;
        }
        return null;
    }

    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    //методы для Epic
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTaskByEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<SubTask> epicSubtask = new ArrayList<>();
            for (Integer i : epic.getSubTasks()) {
                epicSubtask.add(subtasks.get(i));
            }
            return epicSubtask;
        }
        return new ArrayList<>();
    }

    public Epic getEpicByID(Integer id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) { //а здесь получается за счет сетеров автоматом добавляется и статус и тип?
//        и их можно не прописывать дополнительно нигде?
        if (epic != null) {
            Integer id = getIdCounter();
            epic.setId(id);
            epics.put(id, epic);
            return epic;
        }
        return null;
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setTitle(epic.getTitle());
            oldEpic.setDescription(epic.getDescription());
            return oldEpic;
        }
        return null;
    }

    public void deleteEpicByID(Integer epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksToRemove = epic.getSubTasks();
        epics.remove(epicId);
        for (Integer id : subtasksToRemove) {
            subtasks.remove(id);
        }
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

//методы для Subtask

    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public SubTask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public SubTask createSubtask(SubTask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            Integer id = getIdCounter();
            subtask.setId(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubTask(id);
            subtasks.put(id, subtask);
            updateEpicStatus(epic.getId());
            return subtask;
        }
        return null;
    }

    public SubTask updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            SubTask oldSubtask = subtasks.get(subtask.getId());
            subtask.setEpicId(oldSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            return subtask;
        }
        return null;
    }

    private void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        Integer doneCount = 0;
        Integer newCount = 0;
        ArrayList<Integer> subtaskToCheck = epic.getSubTasks();
        if (!subtaskToCheck.isEmpty()) {
            for (Integer i : subtaskToCheck) {
                SubTask subtask = subtasks.get(i);
                if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    break;
                } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    doneCount++;
                } else if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    newCount++;
                }
            }
            if (doneCount.equals(subtaskToCheck.size())) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount.equals(subtaskToCheck.size())) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> epicSubtasks = epic.getSubTasks();
            epicSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void deleteSubtaskByID(Integer id) {
        SubTask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> epicSubtasks = epic.getSubTasks();
        epicSubtasks.remove(id);
        subtasks.remove(id);
        updateEpicStatus(subtask.getEpicId());
    }
}