package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        if (endpoint == Endpoint.GET_ALL) {
            handleGetHistory(exchange);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response);
        } catch (Exception exception) {
            handleException(exchange, exception);
        }
    }
}
