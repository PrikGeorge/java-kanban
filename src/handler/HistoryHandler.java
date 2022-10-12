package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HistoryHandler implements HttpHandler {

    private final TaskManager<Task> taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager<Task> taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {

            try (OutputStream out = exchange.getResponseBody()) {
                String historyGson = gson.toJson(taskManager.history());
                exchange.sendResponseHeaders(200, 0);
                out.write(historyGson.getBytes(StandardCharsets.UTF_8));
            }

        } else {
            try (OutputStream out = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(405, 0);
                out.write("Метод запроса неверен.".getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
