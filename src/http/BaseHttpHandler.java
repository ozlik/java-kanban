package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import model.TaskType;
import service.ManagerInterruptedException;
import service.ManagerNotFoundException;
import service.ManagerValidationException;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        System.out.println("Обрабатывается запрос " + exchange.getRequestURI().toString() + " с методом " + exchange.getRequestMethod());
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }


    protected void writeResponse(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, response.length);
            os.write(response);
        }
    }

    public void handleException(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();
        switch (e) {
            case ManagerValidationException ignored -> sendHasValidationException(exchange, e.getMessage());
            case ManagerInterruptedException ignored -> sendHasInterruptedException(exchange, e.getMessage());
            case ManagerNotFoundException ignored -> sendNotFound(exchange, e.getMessage());
            case JsonSyntaxException ignored -> sendHasJsonException(exchange, e.getMessage());
            default -> {
                // Обработка других типов исключений или общая обработка
                System.err.println("Неизвестная ошибка: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        }
    }

    private void sendHasValidationException(HttpExchange exchange, String text) throws ManagerValidationException, IOException {
        writeResponse(exchange, text, 400);
    }

    private void sendHasInterruptedException(HttpExchange exchange, String text) throws ManagerInterruptedException, IOException {
        writeResponse(exchange, text, 406);
    }

    private void sendNotFound(HttpExchange exchange, String text) throws ManagerNotFoundException, IOException {
        writeResponse(exchange, text, 404);
    }

    private void sendHasJsonException(HttpExchange exchange, String text) throws JsonSyntaxException, IOException {
        writeResponse(exchange, text, 400);
    }

    protected void handleDeleteObject(HttpExchange exchange, TaskType type) throws IOException {
        try (exchange) {
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
                writeResponse(exchange, response, 204);
                return;
            }
            writeResponse(exchange, "Задача с идентификатором " + id.get() + " не найдена", 404);
        }
    }

    protected <T> void handlePostObject(HttpExchange exchange, Class<T> c, Consumer<T> creator) throws IOException {
        try (exchange) {
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
    }

    protected <T> void handlePostObjectUpdate(HttpExchange exchange, Class<T> c, Consumer<T> uppdate) throws IOException {
        try (exchange) {
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
                writeResponse(exchange, response, 200);
                return;
            }

            writeResponse(exchange, "Задача не обновлена", 404);
        }
    }

    protected void handleGetObject(HttpExchange exchange, Function<Integer, ? extends Task> getter)
            throws IOException {
        try (exchange) {
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
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        try (exchange) {
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                return Optional.of(Integer.parseInt(query.substring(query.indexOf("?id=") + 4)));
            } else {
                return Optional.empty();
            }
        }
    }

    protected Endpoint getEndpoint(HttpExchange exchange) {
        try (exchange) {
            String requestMethod = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            Endpoint result;

            switch (requestMethod) {
                case "GET":
                    if (query == null) {
                        result = Endpoint.GET_ALL;
                    } else {
                        result = Endpoint.GET;
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        result = Endpoint.DELETE_ALL;
                    } else {
                        result = Endpoint.DELETE;
                    }
                    break;
                case "POST":
                    result = Endpoint.POST;
                    break;
                default:
                    result = Endpoint.UNKNOWN;
                    break;
            }
            return result;
        }
    }
}
