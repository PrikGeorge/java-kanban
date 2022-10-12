package server;

import com.sun.net.httpserver.HttpServer;
import handler.*;
import manager.Managers;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private final HttpServer server;

    private final TaskManager<Task> taskManager;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/", new TasksHandler(taskManager));
        server.createContext("/tasks/task", new TaskHandler(taskManager));
        server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        server.createContext("/tasks/epic", new EpicHandler(taskManager));
        server.createContext("/tasks/history", new HistoryHandler(taskManager));

        server.start();
    }

    public TaskManager<Task> getTaskManager() {
        return taskManager;
    }

    public void stop() {
        server.stop(0);
    }

}
