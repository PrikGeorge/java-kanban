package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void create() {
        taskManager = new InMemoryTaskManager<>();
        taskManager.removeTaskList();

        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        List<Task> historyTasks = List.copyOf(historyManager.getHistory());

        for (Task task : historyTasks) {
            historyManager.remove(task.getId());
        }

    }

    @AfterEach
    public void clearHistory() {
    }
}
