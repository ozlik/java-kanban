package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subtasks;
    private static int idCounter = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    //ок будет если сделаю протектед? так как метод используется в тестах, для проверки его работы или мы его просто не тестируем?
    protected int getIdCounter() {
        return idCounter++;
    }

    //методы для Task
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskByID(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        if (task != null) {
            Integer id = getIdCounter();
            task.setId(id);
            tasks.put(id, task);
            return task;
        }
        return null;
    }

    @Override
    public Task updateTask(Integer id, Task task) {
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);
            oldTask.setTitle(task.getTitle());
            oldTask.setDescription(task.getDescription());
            oldTask.setStatus(task.getStatus());
            return oldTask;
        }
        return null;
    }

    @Override
    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    //методы для Epic
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
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

    @Override
    public Epic getEpicByID(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null) {
            Integer id = getIdCounter();
            epic.setId(id);
            epics.put(id, epic);
            return epic;
        }
        return null;
    }

    @Override
    public Epic updateEpic(Integer id, Epic epic) {
        if (epics.containsKey(id)) {
            Epic oldEpic = epics.get(id);
            oldEpic.setTitle(epic.getTitle());
            oldEpic.setDescription(epic.getDescription());
            return oldEpic;
        }
        return null;
    }

    @Override
    public void deleteEpicByID(Integer epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksToRemove = epic.getSubTasks();
        epics.remove(epicId);
        for (Integer id : subtasksToRemove) {
            subtasks.remove(id);
        }
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public SubTask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
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

    @Override
    public SubTask updateSubTask(Integer id, SubTask subtask) {
        if (subtasks.containsKey(id)) {
            SubTask oldSubtask = subtasks.get(id);
            oldSubtask.setTitle(subtask.getTitle());
            oldSubtask.setDescription(subtask.getDescription());
            oldSubtask.setStatus(subtask.getStatus());
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

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        SubTask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTask(id);
        subtasks.remove(id);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getAll();
    }

}