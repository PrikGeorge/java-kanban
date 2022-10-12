package test;

import manager.HTTPTaskManager;
import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HTTPTaskManagerTest extends TaskManagerTest {

    private HTTPTaskManager<Task> manager;

    @BeforeEach
    public void create() {
        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        List<Task> historyTasks = List.copyOf(historyManager.getHistory());
        manager = (HTTPTaskManager<Task>) Managers.getDefault();
        manager.removeTaskList();
        for (Task task : historyTasks) {
            historyManager.remove(task.getId());
        }
    }

    @Test
    public void saveEmptyTasksList() {
        Task task = new Task("Task", "Description");
        manager.addTask(task);
        manager.removeTaskById(task.getId());

        HTTPTaskManager<Task> loadManager = new HTTPTaskManager<>("localhost");
        assertNotNull(loadManager.getTaskList(), "Список задач не найден");
        assertEquals(loadManager.getTaskList().size(), 0, "Список задач не пуст");
    }

    @Test
    public void saveWithEpic() {
        Epic epic = new Epic("Epic", "Description");
        manager.addTask(epic);

        HTTPTaskManager<Task> loadManager = (HTTPTaskManager<Task>) Managers.getDefault();
        Epic loadedEpic = (Epic) loadManager.getTaskById(epic.getId());

        assertNotNull(loadedEpic, "Эпик не найден");
        assertEquals(epic.toString(), loadedEpic.toString(), "Эпик не сохранен");
    }

    @Test
    public void saveEmptyHistoryList() {
        Task task = new Task("Task", "Description");
        manager.addTask(task);
        manager.removeTaskById(task.getId());

        HTTPTaskManager<Task> loadManager = new HTTPTaskManager<>("localhost");
        assertNotNull(loadManager.history(), "История не найдена");
        assertEquals(loadManager.history().size(), 0, "Список задач не пуст");
    }
}
