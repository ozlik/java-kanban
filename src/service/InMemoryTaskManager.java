package service;

import model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    final HashMap<Integer, Task> tasks;
    final HashMap<Integer, Epic> epics;
    final HashMap<Integer, SubTask> subtasks;
    private static int idCounter = 0;
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int getIdCounter() {
        return idCounter++;
    }

    protected void setIdCounter(int id) {
        idCounter = id;
    }

    //методы для Task
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskByID(Integer id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Task createTask(Task task) {
        if (task != null) {
            Integer id = getIdCounter();
            task.setId(id);
            taskTimeValidation(task);
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(task);
            taskTimeValidation(task);
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
            return task;
        }
        return null;
    }

    protected Task addTask(Integer id, Task task) {
        if (task != null) {
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return task;
        }
        return null;
    }

    @Override
    public void deleteTaskById(Integer id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        for (Integer id : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(id));
            historyManager.remove(id);
        }
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
            // Используем Stream для получения списка SubTask
            return epic.getSubTasks().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toCollection(ArrayList::new)); // собираем результат в ArrayList
        }
        return new ArrayList<>();
    }

    @Override
    public Epic getEpicByID(Integer id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
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
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setTitle(epic.getTitle());
            oldEpic.setDescription(epic.getDescription());
            return oldEpic;
        }
        return null;
    }

    protected Epic addEpic(Integer id, Epic epic) {
        if (epic != null) {
            epics.put(id, epic);
            return epic;
        }
        return null;
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        historyManager.remove(epicId);
        epics.remove(epicId);
        epic.deleteSubtasks();
        epic.getSubTasks().forEach(id -> {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        });
    }

    @Override
    public void deleteEpics() {
        epics.keySet().forEach(id -> {
            historyManager.remove(id);
            epics.get(id).deleteSubtasks();
        });
        subtasks.keySet().forEach(id -> {
            prioritizedTasks.remove(subtasks.get(id));
            historyManager.remove(id);
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public SubTask getSubtaskById(Integer id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public SubTask createSubtask(SubTask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            Integer id = getIdCounter();
            subtask.setId(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubTask(id);
            taskTimeValidation(subtask);
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            updateEpicStatus(epic.getId());
            updateEpicTime(subtask.getEpicId());
            return subtask;
        }
        return null;
    }

    @Override
    public SubTask updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtask);
            taskTimeValidation(subtask);
            prioritizedTasks.add(subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            updateEpicTime(subtask.getEpicId());
            return subtask;
        }
        return null;
    }

    protected SubTask addSubtask(Integer id, SubTask subtask) {
        if (subtask != null) {
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            return subtask;
        }
        return null;
    }

    private void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        List<Integer> subtaskToCheck = epic.getSubTasks();
        if (subtaskToCheck.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            Map<TaskStatus, Long> statusCounts = subtaskToCheck.stream()
                    .map(subtasks::get)
                    .collect(Collectors.groupingBy(SubTask::getStatus, Collectors.counting()));

            long inProgressCount = statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L);
            long doneCount = statusCounts.getOrDefault(TaskStatus.DONE, 0L);
            long newCount = statusCounts.getOrDefault(TaskStatus.NEW, 0L);

            if (inProgressCount > 0) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (doneCount == subtaskToCheck.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount == subtaskToCheck.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS); // Любая другая комбинация означает IN_PROGRESS
            }
        }
    }

    private void updateEpicTime(Integer id) {
        Epic epic = getEpicByID(id);
        epic.setDuration(calculateEpicDuration(id));
        epic.setStartTime(calculateEpicStartTime(id));
        epic.setEndTime(calculateEpicEndTime(id));
    }

    private int calculateEpicDuration(Integer id) {
        Epic epic = getEpicByID(id);

        return epic.getSubTasks().stream()
                .mapToInt(subtaskId -> subtasks.get(subtaskId).getDuration())
                .sum();
    }

    private LocalDateTime calculateEpicStartTime(Integer id) {
        Epic epic = getEpicByID(id);
        return epic.getSubTasks().stream()
                .map(subtasks::get)
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
    }

    private LocalDateTime calculateEpicEndTime(Integer id) {
        Epic epic = getEpicByID(id);
        return epic.getSubTasks().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(epic.getStartTime());
    }

    public void taskTimeValidation(Task task) {
        boolean isTaskOverlap = isTaskOverlap(task);
        if (isTaskOverlap) {
            throw new ManagerValidationException("Время задачи пересекается с существующей");
        }
    }

    private boolean isTaskOverlap(Task task) {
        if (task.getStartTime() != null) {
            for (Task taskToCompare : prioritizedTasks) {
                if (taskToCompare.getStartTime() != null) {
                    if (
                            (taskToCompare.getStartTime().equals(task.getStartTime())
                                    && taskToCompare.getEndTime().equals(task.getEndTime()))
                                    || (taskToCompare.getStartTime().isBefore(task.getStartTime())
                                    && (taskToCompare.getEndTime().isAfter(task.getStartTime()))
                                    || (taskToCompare.getStartTime().isAfter(task.getStartTime()))
                                    && (taskToCompare.getStartTime().isBefore(task.getEndTime())))
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().forEach(id -> {
            prioritizedTasks.remove(subtasks.get(id));
            historyManager.remove(id);
        });

        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.deleteSubtasks();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        SubTask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTask(id);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getAll();
    }

}