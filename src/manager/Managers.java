package manager;

import model.Task;

import java.util.Objects;

public class Managers {
    private static HistoryManager<Task> defaultHistory;

    private static FileBackedTasksManager<Task> defaultFile;

    private static TaskManager<Task> defaultManager;

    private Managers() {
        throw new AssertionError("Невозможно создать экземпляр.");
    }

    public static TaskManager<Task> getDefault() {
        return Objects.requireNonNullElseGet(defaultManager, ()
                -> defaultManager = new HTTPTaskManager<Task>("localhost"));
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return Objects.requireNonNullElseGet(defaultHistory, ()
                -> defaultHistory = new InMemoryHistoryManager<>());
    }

    public static FileBackedTasksManager<Task> getDefaultFile(String filePath) {
        return Objects.requireNonNullElseGet(defaultFile, ()
                -> defaultFile = new FileBackedTasksManager<>(filePath));
    }
}
