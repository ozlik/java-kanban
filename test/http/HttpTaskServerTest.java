package http;

import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TaskServer должен:")
class HttpTaskServerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(historyManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void init() {
        manager.deleteAll();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("добавлять задачи")
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, 15);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("добавлять подзадачи")
    public void shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        manager.createEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2",
                TaskStatus.NEW, 15, LocalDateTime.of(2024, 12, 31, 16, 10), 0);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("добавлять эпики")
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        ArrayList<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("отдавать задачи")
    public void shouldGetTasks() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));
        Task task2 = manager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 15, LocalDateTime.of(2024, 12, 2, 16, 10)));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getTasks());
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать эпики")
    public void shouldGetEpics() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic2 = manager.createEpic(new Epic("Тестовая задача 2, заголовок", "Описание тестовой задачи"));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getEpics());
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать подзадачи")
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask = manager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask2 = manager.createSubtask(new SubTask("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getSubtasks());
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать задачу по id")
    public void shouldGetTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTask = gson.toJson(manager.getTaskById(task.getId()));
        String tasks = response.body();

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTask, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать эпик по id")
    public void shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getEpicById(epic.getId()));
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать подзадачу по id")
    public void shouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask = manager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getSubtaskById(subtask.getId()));
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("удалять задачи")
    public void shouldDeleteTasks() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));
        Task task2 = manager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 15, LocalDateTime.of(2024, 12, 2, 16, 10)));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> expectedTasks = manager.getTasks();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("удалять эпики")
    public void shouldDeleteEpics() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic2 = manager.createEpic(new Epic("Тестовая задача 2, заголовок", "Описание тестовой задачи"));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Epic> expectedTasks = manager.getEpics();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("удалять подзадачи")
    public void shouldDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask = manager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask2 = manager.createSubtask(new SubTask("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<SubTask> expectedTasks = manager.getSubtasks();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("удалять задачу по id")
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> expectedTasks = manager.getTasks();

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("отдавать эпик по id")
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Epic> expectedTasks = manager.getEpics();

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("удалять подзадачу по id")
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask = manager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<SubTask> expectedTasks = manager.getSubtasks();

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(0, expectedTasks.size(), "задачи не удалены");
    }

    @Test
    @DisplayName("отдавать историю задач")
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));
        Task task2 = manager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 15, LocalDateTime.of(2024, 12, 2, 16, 10)));
        manager.getTaskById(0);
        manager.getTaskById(1);

        List<Task> historyList = manager.getHistory();
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(historyList);
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать приоритизацию задач")
    public void shouldGetPrioritized() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 11, 2, 15, 10)));
        Task task2 = manager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 15, LocalDateTime.of(2024, 12, 2, 16, 10)));

        List<Task> historyList = manager.getPrioritizedTasks();
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(historyList);
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать подзадачи эпика")
    public void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask = manager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/?id=0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedTasks = gson.toJson(manager.getSubTaskByEpic(epic.getId()));
        String tasks = response.body();
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем задачи
        assertEquals(expectedTasks, tasks, "задачи не совпадают");
    }
}