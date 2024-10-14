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

    Task addTask(Integer id, Task task);

    void deleteTaskById(Integer id);

    void deleteTasks();

    //методы для Epic
    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTaskByEpic(Integer id);

    Epic getEpicByID(Integer id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic addEpic(Integer id, Epic epic);

    void deleteEpicById(Integer epicId);

    void deleteEpics();

    //методы для Subtask
    ArrayList<SubTask> getSubtasks();

    SubTask getSubtaskById(Integer id);

    SubTask createSubtask(SubTask subtask);

    SubTask updateSubTask(SubTask subtask);

    SubTask addSubtask(Integer id, SubTask subTask);

    void deleteSubtasks();

    void deleteSubtaskById(Integer id);

    List<Task> getHistory();
}
