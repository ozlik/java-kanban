package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            case GET_PRIORITIZED: {
                handleGetPrioritized(exchange);
                break;
            }
            case GET_TASK: {
                handleGetTask(exchange);
                break;
            }
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case GET_SUBTASK: {
                handleGetSubtask(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case POST_SUBTASK: {
                handlePostSubtask(exchange);
                break;
            }
            case DELETE_TASKS: {
                handleDeleteTasks(exchange);
                break;
            }
            case DELETE_EPICS: {
                handleDeleteEpics(exchange);
                break;
            }
            case DELETE_SUBTASKS: {
                handleDeleteSubtasks(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubtask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, response);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getHistory());
        sendText(exchange, response);
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        handleGetObject(exchange, taskManager::getTaskById);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        handleGetObject(exchange, taskManager::getEpicById);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        handleGetObject(exchange, taskManager::getSubtaskById);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);

        if (idOpt.isEmpty()) {
            handlePostObject(exchange, Task.class, taskManager::createTask);
        } else {
            handlePostObjectUpdate(exchange, Task.class, taskManager::updateTask);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);

        if (idOpt.isEmpty()) {
            handlePostObject(exchange, Epic.class, taskManager::createEpic);
        } else {
            handlePostObjectUpdate(exchange, Epic.class, taskManager::updateEpic);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);

        if (idOpt.isEmpty()) {
            handlePostObject(exchange, SubTask.class, taskManager::createSubtask);
        } else {
            handlePostObjectUpdate(exchange, SubTask.class, taskManager::updateSubTask);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        String response = "Все задачи удалены";
        sendText(exchange, response);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        String response = "Все эпики подзадачи удалены";
        sendText(exchange, response);
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        String response = "Все подзадачи удалены";
        sendText(exchange, response);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        handleDeleteObject(exchange, TaskType.TASK);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        handleDeleteObject(exchange, TaskType.EPIC);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        handleDeleteObject(exchange, TaskType.SUBTASK);
    }

    private void handleDeleteObject(HttpExchange exchange, TaskType type) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный формат id", 400);
            return;
        }
        boolean success = false;
        String response = "";

        switch (type) {
            case TASK:
                if (taskManager.getTaskById(id.get()) != null) {
                    taskManager.deleteTaskById(id.get());
                    response = "Задача " + id.get() + " успешно удалена";
                    success = true;
                }
                break;
            case EPIC:
                if (taskManager.getEpicById(id.get()) != null) {
                    taskManager.deleteEpicById(id.get());
                    response = "Эпик и его подзадачи" + id.get() + " успешно удалены";
                    success = true;
                }
                break;
            case SUBTASK:
                if (taskManager.getSubtaskById(id.get()) != null) {
                    taskManager.deleteSubtaskById(id.get());
                    response = "Задача " + id.get() + " успешно удалена";
                    success = true;
                }
                break;
        }
        if (success) {
            sendText(exchange, response);
            return;
        }
        writeResponse(exchange, "Задача с идентификатором " + id.get() + " не найдена", 404);
    }

    private <T> void handlePostObject(HttpExchange exchange, Class<T> c, Consumer<T> creator) throws IOException {
        InputStream bodyInputStream = exchange.getRequestBody();
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            writeResponse(exchange, "Тело задачи не может быть пустым", 400);
            return;
        }

        T object = gson.fromJson(body, c);

        if (object != null) {
            creator.accept(object);
            String response = "Задача создана";
            writeResponse(exchange, response, 201);
            return;
        }

        writeResponse(exchange, "Задача не создана", 404);
    }

    private <T> void handlePostObjectUpdate(HttpExchange exchange, Class<T> c, Consumer<T> uppdate) throws IOException {
        InputStream bodyInputStream = exchange.getRequestBody();
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            writeResponse(exchange, "Тело задачи не может быть пустым", 400);
            return;
        }

        T object = gson.fromJson(body, c);

        if (object != null) {
            uppdate.accept(object);
            String response = "Задача обновлена";
            writeResponse(exchange, response, 201);
            return;
        }

        writeResponse(exchange, "Задача не обновлена", 404);
    }

    private void handleGetObject(HttpExchange exchange, Function<Integer, ? extends Task> getter) throws
            IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный формат id", 400);
            return;
        }

        Task objectById = getter.apply(id.get());
        if (objectById != null) {
            String response = gson.toJson(objectById);
            sendText(exchange, response);
            return;
        }

        writeResponse(exchange, "Задача с идентификатором " + id.get() + " не найдена", 404);
    }

    private Optional<Integer> getId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            return Optional.of(Integer.parseInt(query.substring(query.indexOf("?id=") + 4)));
        } else {
            return Optional.empty();
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        String query = exchange.getRequestURI().getQuery();

        if (requestMethod.equals("GET") && query == null) {
            switch (pathParts[1]) {
                case "tasks": {
                    return Endpoint.GET_TASKS;
                }
                case "subtasks": {
                    return Endpoint.GET_SUBTASKS;
                }
                case "epics": {
                    return Endpoint.GET_EPICS;
                }
                case "history": {
                    return Endpoint.GET_HISTORY;
                }
                case "prioritized": {
                    return Endpoint.GET_PRIORITIZED;
                }

            }
        }
        if (requestMethod.equals("DELETE") && query == null) {
            switch (pathParts[1]) {
                case "tasks": {
                    return Endpoint.DELETE_TASKS;
                }
                case "subtasks": {
                    return Endpoint.DELETE_SUBTASKS;
                }
                case "epics": {
                    return Endpoint.DELETE_EPICS;
                }
            }
        }
        if (requestMethod.equals("GET") && query != null) {
            switch (pathParts[1]) {
                case "tasks": {
                    return Endpoint.GET_TASK;
                }
                case "subtasks": {
                    return Endpoint.GET_SUBTASK;
                }
                case "epics": {
                    return Endpoint.GET_EPIC;
                }
            }
        }
        if (requestMethod.equals("POST")) {
            switch (pathParts[1]) {
                case "tasks": {
                    return Endpoint.POST_TASK;
                }
                case "subtasks": {
                    return Endpoint.POST_SUBTASK;
                }
                case "epics": {
                    return Endpoint.POST_EPIC;
                }
            }
        }
        if (requestMethod.equals("DELETE") && query != null) {
            switch (pathParts[1]) {
                case "tasks": {
                    return Endpoint.DELETE_TASK;
                }
                case "subtasks": {
                    return Endpoint.DELETE_SUBTASK;
                }
                case "epics": {
                    return Endpoint.DELETE_EPIC;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

}

