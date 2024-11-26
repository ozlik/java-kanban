package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import model.TaskType;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_ALL: {
                handleGetTasks(exchange);
                break;
            }
            case GET: {
                handleGetTask(exchange);
                break;
            }
            case POST: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_ALL: {
                handleDeleteTasks(exchange);
                break;
            }
            case DELETE: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        try {
            handleGetObject(exchange, taskManager::getTaskById);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        try {
            if (idOpt.isEmpty()) {
                handlePostObject(exchange, Task.class, taskManager::createTask);
            } else {
                handlePostObjectUpdate(exchange, Task.class, taskManager::updateTask);
            }
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteTasks();
            String response = "Все задачи удалены";
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            handleDeleteObject(exchange, TaskType.TASK);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }
}

