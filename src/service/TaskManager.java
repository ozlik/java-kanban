package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    //методы для Task
    ArrayList<Task> getTasks();

    Optional<Task> getTaskByID(Integer id);

    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTaskById(Integer id);

    void deleteTasks();

    //методы для Epic
    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTaskByEpic(Integer id);

    Optional<Epic> getEpicByID(Integer id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpicById(Integer epicId);

    void deleteEpics();

    //методы для Subtask
    ArrayList<SubTask> getSubtasks();

    Optional<SubTask> getSubtaskById(Integer id);

    SubTask createSubtask(SubTask subtask);

    SubTask updateSubTask(SubTask subtask);

    void deleteSubtasks();

    void deleteSubtaskById(Integer id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
