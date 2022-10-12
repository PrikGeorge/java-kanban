package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.KVServer;

import java.io.IOException;
import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void createManager() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = new InMemoryTaskManager<Task>();

        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        List<Task> historyTasks = List.copyOf(historyManager.getHistory());
        taskManager.removeTaskList();
        for (Task task : historyTasks) {
            historyManager.remove(task.getId());
        }

    }

    @AfterEach
    public void clearHistory() {
    }
}
