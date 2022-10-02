package test;

import manager.FileBackedTasksManager;
import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTasksManagerTest extends TaskManagerTest {

    private final String fileName = "test.txt";

    protected FileBackedTasksManager<Task> fileManager;

    @BeforeEach
    public void createManager() {
        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        List<Task> historyTasks = List.copyOf(historyManager.getHistory());

        for (Task task : historyTasks) {
            historyManager.remove(task.getId());
        }

        fileManager = new FileBackedTasksManager<Task>(fileName);
    }

    @AfterEach
    public void removeFile() {
        new File("test.txt").delete();
    }

    @Test
    public void saveEmptyTasksList() {
        Task task = new Task("Task 1", "Description");

        fileManager.addTask(task);
        fileManager.removeTaskById(task.getId());

        assertNotNull(FileBackedTasksManager.loadFromFile(fileName).getTaskList(), "Список задач не найден");
        assertEquals(FileBackedTasksManager.loadFromFile(fileName).getTaskList().size(), 0, "Список задач не пуст");
    }

    @Test
    public void saveWithEpic() {
        Epic epic = new Epic("Epic 1", "Description");
        fileManager.addTask(epic);

        Epic loadedEpic = (Epic) FileBackedTasksManager.loadFromFile(fileName).getTaskById(epic.getId());
        assertNotNull(loadedEpic, "Эпик не найден");
        assertEquals(epic.toString(), loadedEpic.toString(), "Эпик не сохранен");
    }

    @Test
    public void saveEmptyHistoryList() {
        Task task = new Task("Task 1", "Description");

        fileManager.addTask(task);
        fileManager.removeTaskById(task.getId());

        assertNotNull(FileBackedTasksManager.loadFromFile(fileName).history(), "История не найдена");
        assertEquals(FileBackedTasksManager.loadFromFile(fileName).history().size(), 0, "Список задач не пуст");
    }
}
