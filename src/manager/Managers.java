package manager;

import model.Task;

public class Managers {

    private Managers() {
        throw new AssertionError("Невозможно создать экземпляр.");
    }

    public static <T extends Task> TaskManager<T> getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static <T extends Task> HistoryManager<T> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }
}
