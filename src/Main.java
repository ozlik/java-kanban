
import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {

     TaskManager taskManager = Managers.getDefault();
        

        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task task2 = new Task("Вторая задача", "Описание второй задачи", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, 2);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 2);
        SubTask subtask3 = new SubTask("Подзадача второго эпика", "Описание подзадачи второго эпика", TaskStatus.NEW, 3);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println(taskManager.getTasks());
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        System.out.println(taskManager.getEpics());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getTaskByID(0));
        System.out.println(taskManager.getEpicByID(2));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getSubTaskByEpic(2));

        SubTask subtask3UpDate = new SubTask("2 второго эпика", "Описание 2 подзадачи обновленной второго эпика",
                TaskStatus.DONE, 3);
        Task task1UpDate = new Task("Первая обновлённая задача", "Описание первой задачи",
                TaskStatus.IN_PROGRESS);
        Epic epic1UpDate = new Epic( "Первый обновлённый эпик", "Описание первого эпика");
        taskManager.updateSubTask(6, subtask3UpDate);
        System.out.println("Обновили подзадачу" + taskManager.getSubtaskById(6));
        System.out.println("Эпик подзадачи" + taskManager.getEpicByID(3));
        taskManager.updateTask(1, task1UpDate);
        System.out.println(taskManager.getTaskByID(1));
        taskManager.updateEpic(2, epic1UpDate);
        System.out.println(taskManager.getEpicByID(2));
                System.out.println();

       System.out.println(taskManager.getHistory());
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);
       taskManager.getEpicByID(2);

       System.out.println(taskManager.getHistory());

        taskManager.deleteTaskByID(0);
        System.out.println("Удалили задачу по айди " + taskManager.getTasks());
        taskManager.deleteEpicByID(3);
        System.out.println("Удалили эпик и его подзадачи " + taskManager.getEpics() + taskManager.getSubtasks());
        taskManager.deleteSubtaskByID(5);
        System.out.println("Удалили подзадачу по айди " + taskManager.getSubtasks() + epic1.getSubTasks());



        taskManager.deleteTasks();
        System.out.println("Удалили задачи " + taskManager.getTasks());
        taskManager.deleteSubtasks();
        System.out.println("Удалили сабтаски " + taskManager.getSubtasks()+taskManager.getEpics());
        taskManager.deleteEpics();
        System.out.println("Удалили эпики и связанные сабтаски " + taskManager.getSubtasks() + taskManager.getEpics());

    }
}
