
import model.*;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager;
        manager = new Manager();

        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("Вторая задача", "Описание второй задачи", TaskStatus.NEW, TaskType.TASK);
        manager.createTask(task1);
        manager.createTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика", TaskStatus.NEW, TaskType.EPIC);
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика", TaskStatus.NEW, TaskType.EPIC);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, TaskType.SUBTASK, 2);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.NEW, TaskType.SUBTASK, 2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        SubTask subtask3 = new SubTask("Подзадача второго эпика", "Описание подзадачи второго эпика", TaskStatus.NEW, TaskType.SUBTASK, 3);
        manager.createSubtask(subtask3);
        System.out.println("Задачи созданы " + manager);
        System.out.println("Подзадачи эпика " + manager.getSubTaskByEpic(2));
        SubTask subtask3UpDate = new SubTask("2 второго эпика", "Описание 2 подзадачи второго эпика", TaskStatus.IN_PROGRESS, TaskType.SUBTASK, 3);
        manager.updateSubTask(4, subtask3UpDate);
        System.out.println("Обновили подзадачу, проверили статус эпика " + manager);
        manager.clearTaskByID(0);
        System.out.println("Удалили задачу по айди " + manager);
        manager.deleteEpicByID(3);
        System.out.println("Удалили эпик и его подзадачи " + manager);
        manager.clearManager();
        System.out.println("Удалили всё " + manager);
    }
}

