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
        try {
            String response = gson.toJson(taskManager.getEpics());
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        try {
            handleGetObject(exchange, taskManager::getEpicById);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        try {
            if (idOpt.isEmpty()) {
                handlePostObject(exchange, Epic.class, taskManager::createEpic);
            } else {
                handlePostObjectUpdate(exchange, Epic.class, taskManager::updateEpic);
            }
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteEpics();
            String response = "Все эпики подзадачи удалены";
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            handleDeleteObject(exchange, TaskType.EPIC);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }

    private void handleGetEpicSubtask(HttpExchange exchange) throws IOException {
        try {
            String[] query = exchange.getRequestURI().getQuery().split("/");
            String queryFirst = query[0];
            int id = Integer.parseInt(queryFirst.substring(queryFirst.indexOf("=") + 1));
            String response = gson.toJson(taskManager.getSubTaskByEpic(id));
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }
}

