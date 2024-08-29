
import model.*;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager;
        manager = new Manager();

        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task task2 = new Task("Вторая задача", "Описание второй задачи", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, 2);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 2);
        SubTask subtask3 = new SubTask("Подзадача второго эпика", "Описание подзадачи второго эпика", TaskStatus.NEW, 3);

        manager.createTask(task1);
        manager.createTask(task2);
        System.out.println(manager.getTasks());
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        System.out.println(manager.getEpics());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getTaskByID(0));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getSubtaskById(5));
        System.out.println(manager.getSubTaskByEpic(2));

        SubTask subtask3UpDate = new SubTask(6, "2 второго эпика", "Описание 2 подзадачи второго эпика",
                TaskStatus.IN_PROGRESS);
        Task task1UpDate = new Task(1, "Первая обновлённая задача", "Описание первой задачи",
                TaskStatus.IN_PROGRESS);
        Epic epic1UpDate = new Epic(2, "Первый обновлённый эпик", "Описание первого эпика");
        manager.updateSubTask(subtask3UpDate);
        System.out.println("Обновили подзадачу" + manager.getSubtaskById(6));
        System.out.println("Эпик подзадачи" + manager.getEpicByID(3));
        manager.updateTask(task1UpDate);
        System.out.println(manager.getTaskByID(1));
        manager.updateEpic(epic1UpDate);
        System.out.println(manager.getEpicByID(2));

        manager.deleteTaskByID(0);
        System.out.println("Удалили задачу по айди " + manager.getTasks());
        manager.deleteEpicByID(3);
        System.out.println("Удалили эпик и его подзадачи " + manager.getEpics());
        manager.deleteSubtaskByID(5);
        System.out.println("Удалили задачу по айди " + manager.getSubtasks());

        manager.clearTasks();
        System.out.println("Удалили задачи " + manager.getTasks());
        manager.clearSubtasks();
        System.out.println("Удалили сабтаски " + manager.getSubtasks());
        manager.clearEpics();
        System.out.println("Удалили эпики и связанные сабтаски " + manager.getSubtasks() + manager.getEpics());

    }
}
