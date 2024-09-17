package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    //методы для Task
    ArrayList<Task> getTasks();

    Task getTaskByID(Integer id);

    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTaskByID(Integer id);

    void deleteTasks();

    //методы для Epic
    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTaskByEpic(Integer id);

    Epic getEpicByID(Integer id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpicByID(Integer epicId);

    void deleteEpics();

    //методы для Subtask
    ArrayList<SubTask> getSubtasks();

    SubTask getSubtaskById(Integer id);

    SubTask createSubtask(SubTask subtask);

    SubTask updateSubTask(SubTask subtask);

    void deleteSubtasks();

    void deleteSubtaskByID(Integer id);

    List<Task> getHistory();
}
