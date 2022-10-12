package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import server.KVTaskClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HTTPTaskManager<T extends Task> extends FileBackedTasksManager<T> {

    private final KVTaskClient client;

    private static final Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        super(url);

        client = new KVTaskClient(url);
        loadFromServer();
    }

    public KVTaskClient getClient() {
        return client;
    }

    @Override
    protected void save() {
        client.put("subtasks", gson.toJson(getTaskList().stream().filter(task -> task instanceof Subtask).collect(Collectors.toList())));
        client.put("epics", gson.toJson(getTaskList().stream().filter(task -> task instanceof Epic).collect(Collectors.toList())));
        client.put("tasks", gson.toJson(getTaskList().stream().filter(Objects::nonNull).collect(Collectors.toList())));
        client.put("history", gson.toJson(history()));
    }

    private void loadFromServer() {

        List<T> tasks = gson.fromJson(getClient().load("tasks"), new TypeToken<List<Task>>() {
        }.getType());
        List<T> subtasks = gson.fromJson(getClient().load("subtasks"), new TypeToken<List<Subtask>>() {
        }.getType());
        List<T> epics = gson.fromJson(getClient().load("epics"), new TypeToken<List<Epic>>() {
        }.getType());
        List<T> history = gson.fromJson(getClient().load("history"), new TypeToken<List<Task>>() {
        }.getType());

        if (Objects.nonNull(tasks) && tasks.size() != 0) {
            tasks.forEach(this::addTask);
        }
        if (Objects.nonNull(subtasks) && subtasks.size() != 0) {
            subtasks.forEach(this::addTask);
        }
        if (Objects.nonNull(epics) && epics.size() != 0) {
            epics.forEach(this::addTask);
        }
        if (Objects.nonNull(history)) {
            history.forEach(historyManager::add);
        }

    }

}
