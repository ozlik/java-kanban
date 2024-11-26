package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import model.TaskType;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_ALL: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET: {
                handleGetSubtask(exchange);
                break;
            }
            case POST: {
                handlePostSubtask(exchange);
                break;
            }
            case DELETE_ALL: {
                handleDeleteSubtasks(exchange);
                break;
            }
            case DELETE: {
                handleDeleteSubtask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getSubtasks());
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        try {
            handleGetObject(exchange, taskManager::getSubtaskById);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        try {
            if (idOpt.isEmpty()) {
                handlePostObject(exchange, SubTask.class, taskManager::createSubtask);
            } else {
                handlePostObjectUpdate(exchange, SubTask.class, taskManager::updateSubTask);
            }
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteSubtasks();
            String response = "Все подзадачи удалены";
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        try {
            handleDeleteObject(exchange, TaskType.SUBTASK);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }


}

