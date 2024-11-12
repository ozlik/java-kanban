import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.TaskHttpHandler;
import model.*;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);


        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW, 100);
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, 100, LocalDateTime.of(2024, 12, 31, 16, 10), 0);
        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 15, LocalDateTime.of(2024, 12, 31, 15, 5), 0);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createTask(task1);
        taskManager.createSubtask(subtask2);


        Gson gson = Managers.getGson();
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/epics", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/history", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new TaskHttpHandler(taskManager, gson));
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");


//        File file1 = new File("File.csv");
//        FileBackedTaskManager taskManager = new FileBackedTaskManager(file1);
//
//        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW, 100);
//        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
//        SubTask subtask1 = new SubTask("Подзадача первого эпика", "Описание подзадачи первого эпика", TaskStatus.NEW, 100, LocalDateTime.of(2024, 12, 31, 16, 10), 0);
//        SubTask subtask2 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 15, LocalDateTime.of(2024, 12, 31, 15, 5), 0);
//
//        taskManager.createEpic(epic1);
//        taskManager.createSubtask(subtask1);
//        taskManager.createTask(task1);
//        taskManager.createSubtask(subtask2);
//
//        System.out.println(taskManager.getSubTaskByEpic(0));
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file1);
//        System.out.println("Бэкап: ");
//        System.out.println(taskManager1.getTasks());
//        System.out.println(taskManager1.getSubtasks());
//        System.out.println(taskManager1.getEpics());
//
//        System.out.println(taskManager.getPrioritizedTasks());

//        System.out.println(taskManager.getTaskByID(0));
//        System.out.println(taskManager.getEpicByID(1));
//        System.out.println(taskManager.getSubtaskById(2));
//        System.out.println(taskManager.getSubTaskByEpic(1));
//
//        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
//        Epic savedEpic = taskManager.createEpic(epic2);
//        savedEpic.setTitle("Обновленный эпик");
//        savedEpic.setDescription("Обновленное описание эпика");
//        taskManager.updateEpic(savedEpic);
//
//        SubTask subtask3 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, 1);
//        SubTask savedSubtask = taskManager.createSubtask(subtask3);
//        savedSubtask.setStatus(TaskStatus.IN_PROGRESS);
//        savedSubtask.setTitle("Новая обновленная задача");
//        savedSubtask.setDescription("Обновленное описание");
//        taskManager.updateSubTask(savedSubtask);
//
//        Task task2 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
//        Task savedTask = taskManager.createTask(task2);
//        savedTask.setStatus(TaskStatus.IN_PROGRESS);
//        savedTask.setTitle("Новая обновленная задача");
//        taskManager.updateTask(savedTask);
//
//
//        System.out.println("Обновили подзадачу" + taskManager.getSubtaskById(5));
//        System.out.println("Эпик подзадачи" + taskManager.getEpicByID(1));
//
//        System.out.println(taskManager.getTaskByID(6));
//        System.out.println(taskManager.getEpicByID(4));
//        System.out.println();
//
//        System.out.println("История" + taskManager.getHistory());
//        taskManager.getEpicByID(1);
//        taskManager.getSubtaskById(3);
//        taskManager.createTask(new Task("7 Первая задача", "Описание первой задачи", TaskStatus.NEW));
//        taskManager.createTask(new Task("8 Первая задача", "Описание первой задачи", TaskStatus.NEW));
//        taskManager.createTask(new Task("9 Первая задача", "Описание первой задачи", TaskStatus.NEW));
//        taskManager.createTask(new Task("10 Первая задача", "Описание первой задачи", TaskStatus.NEW));
//        taskManager.createTask(new Task("11 Первая задача", "Описание первой задачи", TaskStatus.NEW));
//        taskManager.getTaskByID(7);
//        taskManager.getTaskByID(8);
//        taskManager.getTaskByID(9);
//        taskManager.getTaskByID(10);
//        taskManager.getTaskByID(11);
//        taskManager.getTaskByID(10);
//        System.out.println("История" + taskManager.getHistory());
//
//        taskManager.deleteTaskById(0);
//        System.out.println("Удалили задачу по айди " + taskManager.getTasks());
//        taskManager.deleteEpicById(4);
//        System.out.println("Удалили эпик и его подзадачи " + taskManager.getEpics() + taskManager.getSubtasks());
//        taskManager.deleteSubtaskById(5);
//        System.out.println("Удалили подзадачу по айди " + taskManager.getSubtasks() + epic1.getSubTasks());
//
//
//        taskManager.deleteTasks();
//        System.out.println("Удалили задачи " + taskManager.getTasks());
//        System.out.println(taskManager.getHistory());
//        taskManager.deleteSubtasks();
//        System.out.println("Удалили сабтаски " + taskManager.getSubtasks() + taskManager.getEpics());
//        System.out.println(taskManager.getHistory());
//        taskManager.deleteEpics();
//        System.out.println("Удалили эпики и связанные сабтаски " + taskManager.getSubtasks() + taskManager.getEpics());
//        System.out.println(taskManager.getHistory());

    }
}
