package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.TaskType;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        String query = exchange.getRequestURI().getQuery();
        switch (endpoint) {
            case GET_ALL: {
                handleGetEpics(exchange);
                break;
            }
            case GET: {
                if (query.endsWith("subtasks")) {
                    handleGetEpicSubtask(exchange);
                    break;
                } else {
                    handleGetEpic(exchange);
                    break;
                }
            }
            case POST: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_ALL: {
                handleDeleteEpics(exchange);
                break;
            }
            case DELETE: {
                handleDeleteEpic(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        handleGetObject(exchange, taskManager::getEpicById);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);

        if (idOpt.isEmpty()) {
            handlePostObject(exchange, Epic.class, taskManager::createEpic);
        } else {
            handlePostObjectUpdate(exchange, Epic.class, taskManager::updateEpic);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        String response = "Все эпики подзадачи удалены";
        sendText(exchange, response);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        handleDeleteObject(exchange, TaskType.EPIC);
    }

    private void handleGetEpicSubtask(HttpExchange exchange) throws IOException {
        String[] query = exchange.getRequestURI().getQuery().split("/");
        String queryFirst = query[0];
        int id = Integer.parseInt(queryFirst.substring(queryFirst.indexOf("=") + 1));
        String response = gson.toJson(taskManager.getSubTaskByEpic(id));
        sendText(exchange, response);
    }
}

