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
    public HashMap<Integer, Task> getTasks() {
        if (!tasks.isEmpty()) {
            return tasks;
        }
        return null;
    }

    public Task getTaskByID(Integer id) {
        if (!tasks.isEmpty()) {
            return tasks.get(id);
        }
        return null;
    }

    //создаём новую задачу
    public void createTask(Task task) {
        if (!task.equals(null)) { //добавила проверку на ноль, так верно будет её задать?
            Integer id = getIdCounter();
            task.setId(id);
            task.setStatus(TaskStatus.NEW);
            tasks.put(id, task);
        }
    }

    public void updateTask(Integer id, Task task) {
        if (!tasks.isEmpty() && tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task); //столкнулась с проблемой, что если не задавать статус при обновлении, он становится нулевым,
            // что делать? Т_Т
        }
    }

    public void clearTaskByID(Integer id) {
        if (!tasks.isEmpty() && tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void clearTaskManager() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    //методы для Epic
    public HashMap<Integer, Epic> getEpics() {
        if (!epics.isEmpty()) {
            return epics;
        }
        return null;
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
        return null;
    }

    public Epic getEpicByID(Integer id) {
        if (!epics.isEmpty()) {
            return epics.get(id);
        }
        return null;
    }

    public void createEpic(Epic epic) {
        Integer id = getIdCounter();
        epic.setId(id);
        epic.setStatus(TaskStatus.NEW);
        epic.setType(epic.getType());
        epics.put(id, epic);
    }

    public void updateEpic(Integer id, Epic epic) {
        if (epics.containsKey(id)) {
            epic.setId(id);
            Epic oldEpic = epics.get(id);
            ArrayList<Integer> epicSubtasks = oldEpic.getSubTasks();
            for (Integer i : epicSubtasks) {
                epic.addSubTask(i);
            }
            epics.put(id, epic);
            updateEpicStatus(id);
        }
    }

    public void deleteEpicByID(Integer epicId) {
        if (epics.get(epicId) != null) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> subtasksToRemove = epic.getSubTasks();
            epics.remove(epicId);
            for (Integer id : subtasksToRemove) {
                subtasks.remove(id);
            }
        }
    }

    public void clearEpicManager() {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                ArrayList<Integer> subtasksToRemove = epic.getSubTasks();
                for (Integer id : subtasksToRemove) {
                    subtasks.remove(id);
                }
            }
            epics.clear();
        }
    }

    //методы для Subtask

    public HashMap<Integer, SubTask> getSubtasks() {
        if (!subtasks.isEmpty()) {
            return subtasks;
        }
        return null;
    }

    public SubTask getSubtaskById(Integer id) {
        if (!subtasks.isEmpty()) {
            return subtasks.get(id);
        }
        return null;
    }

    public void createSubtask(SubTask subtask) {
        if (!epics.isEmpty() && epics.containsKey(subtask.getEpicId())) {
            Integer id = getIdCounter();
            subtask.setId(id);
            subtask.setStatus(TaskStatus.NEW);
            subtask.setType(subtask.getType());
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubTask(id);
            subtasks.put(id, subtask);

        }
    }

    //не стала убирать возможность написать id эпика, чтобы подзадачу можно было перезакрепить к другому эпику, но можно и убрать
    public void updateSubTask(Integer id, SubTask subtask) { //статус эпика меняется при смене статуса подзадач во время обновления поздадачи
        if (!subtasks.isEmpty() && !epics.isEmpty() && subtasks.containsKey(id)) {
            subtask.setId(id);
            SubTask oldSubtask = subtasks.get(id);
            subtask.setEpicId(oldSubtask.getEpicId());
            subtasks.put(id, subtask);
            updateEpicStatus(subtask.getEpicId());

        }
    }

//    на статус ин прогресс, так если хотя бы один раз он есть, значит уже ставим только этот статус
//    не проверяла на статус нью изначально, так как у нас при создании задач этот статус стоит по умолчанию и только если
//    подзадачу сначала апдтейтили в статус ин прогресс и потом снова обновили, но уже в статус нью, тогда да, нужна проверка на нью
//    плюс проверка полезна при обновлении эпика, когда все его задачи в статусе нью, иначе статус заполняется нулем.

    private void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        Integer doneCount = 0;
        Integer newCount = 0;
        ArrayList<Integer> subtaskToCheck = epic.getSubTasks();
        for (Integer i : subtaskToCheck) {
            SubTask subtask = subtasks.get(i);
            if (subtask.getStatus().equals(TaskStatus.DONE)) {
                doneCount++;
                if (doneCount.equals(subtaskToCheck.size())) {
                    epic.setStatus(TaskStatus.DONE);
                    break;
                }
            } else if (subtask.getStatus().equals(TaskStatus.NEW)) {
                newCount++;
                if (newCount.equals(subtaskToCheck.size())) {
                    epic.setStatus(TaskStatus.NEW);
                    break;
                }
            } else if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                break;
            }
        }
    }


    public void clearSubaskManager() { //я не стала добавлять логику если это последняя задача эпика, так как в эпик
        //  должна остаться возможность добавить еще подзадач
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public void clearSubTaskByID(Integer id) {
        if (subtasks.containsKey(id)) {
            SubTask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            ArrayList<Integer> epicSubtasks = epic.getSubTasks();
            epicSubtasks.remove(id);
            subtasks.remove(id);
        }
    }
}