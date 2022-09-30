package manager;

import model.Task;

import java.io.File;

public class Managers {

    private Managers() {
        throw new AssertionError("Невозможно создать экземпляр.");
    }

    public static TaskManager<Task> getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }

    public static FileBackedTasksManager<Task> getDefaultLoadFile(File file) {
        return new FileBackedTasksManager<>(file);
    }
}
