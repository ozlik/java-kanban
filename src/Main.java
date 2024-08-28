
import model.*;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager;
        manager = new Manager();

        Task task1 = new Task("Первая задача", "Описание первой задачи");
        Task task2 = new Task("Вторая задача", "Описание второй задачи");
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", 2);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", 2);
        SubTask subtask3 = new SubTask("Подзадача второго эпика", "Описание подзадачи второго эпика", 3);

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

        SubTask subtask3UpDate = new SubTask("2 второго эпика", "Описание 2 подзадачи второго эпика",
                TaskStatus.IN_PROGRESS);
        Task task1UpDate = new Task("Первая обновлённая задача", "Описание первой задачи",
                TaskStatus.IN_PROGRESS); //здесь убрала указание id эпика, так как кейса с переносом подзадачи
        // к другому эпику не было, и чтобы не прописывать проверку на соответстсвие, хотя это не сложно и могу сделать
        //если дальнейшая работа предполагает этот кейс.
        Epic epic1UpDate = new Epic("Первый обновлённый эпик", "Описание первого эпика");
        manager.updateSubTask(4, subtask3UpDate);
        System.out.println("Обновили подзадачу" + manager.getSubtaskById(4));
        System.out.println("Эпик подзадачи" + manager.getEpicByID(2));
        manager.updateTask(1, task1UpDate);
        System.out.println(manager.getTaskByID(1));
        manager.updateEpic(2, epic1UpDate);
        System.out.println(manager.getEpicByID(2));

        manager.clearTaskByID(0);
        System.out.println("Удалили задачу по айди " + manager.getTasks());
        manager.deleteEpicByID(3);
        System.out.println("Удалили эпик и его подзадачи " + manager.getEpics());
        manager.clearSubTaskByID(5);
        System.out.println("Удалили задачу по айди " + manager.getSubtasks());

        manager.clearTaskManager();
        System.out.println("Удалили задачи " + manager.getTasks());
        manager.clearSubaskManager();
        System.out.println("Удалили сабтаски " + manager.getSubtasks());
        manager.clearEpicManager();
        System.out.println("Удалили эпики и связанные сабтаски " + manager.getSubtasks() + manager.getEpics());

    }
}

//немного не поняла про вывод, нужно реализовывать красивый вывод и что-то писать в мейне? или этого хватит, мне для тестов
//хватило такого вывода