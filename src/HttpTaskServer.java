import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.TaskHttpHandler;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    HistoryManager historyManager;
    TaskManager taskManager;
    Gson gson;
    HttpServer httpServer;

    public HttpTaskServer() throws IOException {
    historyManager = new InMemoryHistoryManager();
    taskManager = new InMemoryTaskManager(historyManager);

    gson = Managers.getGson();
    httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/epics", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/history", new TaskHttpHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new TaskHttpHandler(taskManager, gson)); }

    public void start() {
               httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }
    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
