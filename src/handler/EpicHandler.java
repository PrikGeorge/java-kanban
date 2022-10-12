package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class EpicHandler implements HttpHandler {

    private final TaskManager<Task> taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager<Task> taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (OutputStream out = exchange.getResponseBody()) {
            String query = exchange.getRequestURI().getQuery();

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (Objects.isNull(query)) {
                        String tasksGson = gson.toJson(taskManager.getTaskList().stream().filter(task -> task.getClass().equals(Epic.class)));
                        exchange.sendResponseHeaders(200, 0);
                        out.write(tasksGson.getBytes(StandardCharsets.UTF_8));

                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);

                        if (Objects.nonNull(taskManager.getTaskById(id))) {
                            String tasksGson = gson.toJson(taskManager.getTaskById(id));
                            exchange.sendResponseHeaders(200, 0);
                            out.write(tasksGson.getBytes(StandardCharsets.UTF_8));
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                            out.write("Задача не найдена.".getBytes(StandardCharsets.UTF_8));
                        }
                    }
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Epic task = gson.fromJson(body, Epic.class);

                    if (Objects.nonNull(task) && Objects.nonNull(taskManager.getTaskById(task.getId()))) {
                        taskManager.updateTask(task);
                        exchange.sendResponseHeaders(200, 0);
                        out.write("Задача успешно обновлена.".getBytes(StandardCharsets.UTF_8));

                    } else {
                        taskManager.addTask(task);
                        exchange.sendResponseHeaders(200, 0);
                        out.write("Задача успешно добавлена.".getBytes(StandardCharsets.UTF_8));
                    }
                    break;
                case "DELETE":
                    if (Objects.isNull(query)) {
                        taskManager.removeTaskList();
                        out.write("Все таски удалены.".getBytes(StandardCharsets.UTF_8));

                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);

                        if (Objects.nonNull(taskManager.getTaskById(id))) {
                            taskManager.removeTaskById(id);
                            exchange.sendResponseHeaders(200, 0);
                            out.write("Задача успешно удалена.".getBytes(StandardCharsets.UTF_8));

                        } else {
                            exchange.sendResponseHeaders(404, 0);
                            out.write("Задача не найдена.".getBytes(StandardCharsets.UTF_8));
                        }
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    out.write("Метод запроса неверен.".getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
