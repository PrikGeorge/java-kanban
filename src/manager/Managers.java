package manager;

import model.Task;

public class Managers {

    private Managers() {
        throw new AssertionError("Невозможно создать экземпляр.");
    }

//    private static TaskManager<Task> defaultTaskManager;
//    private static HistoryManager<Task> defaultHistoryManager;

    //    public static TaskManager<Task> getDefault() {
//        return Objects.requireNonNullElse(defaultTaskManager, defaultTaskManager = new InMemoryTaskManager<>());
//    }
//
//    public static HistoryManager<Task> getDefaultHistory() {
//        return Objects.requireNonNullElse(defaultHistoryManager, defaultHistoryManager = new InMemoryHistoryManager<>());
//    }
    public static <T extends Task> TaskManager<T> getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static <T extends Task> HistoryManager<T> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }
}
