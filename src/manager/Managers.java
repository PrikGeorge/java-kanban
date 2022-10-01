package manager;

import model.Task;

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

    public static FileBackedTasksManager<Task> getDefaultFile(String filePath) {
        return new FileBackedTasksManager<>(filePath);
    }
}
