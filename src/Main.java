
import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Epic epic1 = new Epic("Второй эпик", "Описание второго эпика");
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, 1);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 1);

        taskManager.createTask(task1);
        System.out.println(taskManager.getTasks());
        taskManager.createEpic(epic1);
        System.out.println(taskManager.getEpics());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getTaskByID(0));
        System.out.println(taskManager.getEpicByID(1));
        System.out.println(taskManager.getSubtaskById(2));
        System.out.println(taskManager.getSubTaskByEpic(1));

        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        Epic savedEpic = taskManager.createEpic(epic2);
//        savedEpic.setStatus(TaskStatus.IN_PROGRESS); а если так сделать, то статус у эпика поменяется :)
        savedEpic.setTitle("Обновленный эпик");
        savedEpic.setDescription("Обновленное описание эпика");
        taskManager.updateEpic(savedEpic);

        SubTask subtask3 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 1);
        SubTask savedSubtask = taskManager.createSubtask(subtask3);
        savedSubtask.setStatus(TaskStatus.IN_PROGRESS);
        savedSubtask.setTitle("Новая обновленная задача");
        savedSubtask.setDescription("Обновленное описание");
        taskManager.updateSubTask(savedSubtask);

        Task task2 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task savedTask = taskManager.createTask(task2);
        savedTask.setStatus(TaskStatus.IN_PROGRESS);
        savedTask.setTitle("Новая обновленная задача");
        taskManager.updateTask(savedTask);


        System.out.println("Обновили подзадачу" + taskManager.getSubtaskById(5));
        System.out.println("Эпик подзадачи" + taskManager.getEpicByID(1));

        System.out.println(taskManager.getTaskByID(6));
        System.out.println(taskManager.getEpicByID(4));
        System.out.println();

        System.out.println("История" + taskManager.getHistory());
        taskManager.getEpicByID(1);
        taskManager.getSubtaskById(3);
        taskManager.createTask(new Task("7 Первая задача", "Описание первой задачи", TaskStatus.NEW));
        taskManager.createTask(new Task("8 Первая задача", "Описание первой задачи", TaskStatus.NEW));
        taskManager.createTask(new Task("9 Первая задача", "Описание первой задачи", TaskStatus.NEW));
        taskManager.createTask(new Task("10 Первая задача", "Описание первой задачи", TaskStatus.NEW));
        taskManager.createTask(new Task("11 Первая задача", "Описание первой задачи", TaskStatus.NEW));
        taskManager.getTaskByID(7);
        taskManager.getTaskByID(8);
        taskManager.getTaskByID(9);
        taskManager.getTaskByID(10);
        taskManager.getTaskByID(11);

        System.out.println("История" + taskManager.getHistory());

        taskManager.deleteTaskByID(0);
        System.out.println("Удалили задачу по айди " + taskManager.getTasks());
        taskManager.deleteEpicByID(4);
        System.out.println("Удалили эпик и его подзадачи " + taskManager.getEpics() + taskManager.getSubtasks());
        taskManager.deleteSubtaskByID(5);
        System.out.println("Удалили подзадачу по айди " + taskManager.getSubtasks() + epic1.getSubTasks());


        taskManager.deleteTasks();
        System.out.println("Удалили задачи " + taskManager.getTasks());
        System.out.println(taskManager.getHistory());
        taskManager.deleteSubtasks();
        System.out.println("Удалили сабтаски " + taskManager.getSubtasks() + taskManager.getEpics());
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpics();
        System.out.println("Удалили эпики и связанные сабтаски " + taskManager.getSubtasks() + taskManager.getEpics());
        System.out.println(taskManager.getHistory());

    }
}
