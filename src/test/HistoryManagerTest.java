package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import type.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {


    private HistoryManager<Task> historyManager;

    @BeforeEach
    public void createManager() {
        historyManager = new InMemoryHistoryManager<Task>();
    }

    @Test
    void getEmptyHistory() {
        assertEquals(historyManager.getHistory().size(), 0, "История не пустая.");
    }

    @Test
    void add() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Task task = new Task("Task 2", "Description 2");
        Subtask subtask = new Subtask("Subtask 3", "Description 3", TaskStatus.NEW, epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        assertEquals(historyManager.getHistory().get(0), epic, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(1), task, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(2), subtask, "Задача не найдена");
    }

    @Test
    void getHistory() {
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(historyManager.getHistory().size(), 0, "История не пуста");

        Epic epic = new Epic("Epic 1", "Description");
        Task task = new Task("Task 2", "Description");
        Subtask subtask = new Subtask("Subtask 3", "Description", TaskStatus.NEW, epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        assertEquals(historyManager.getHistory().get(0), epic, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(1), task, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(2), subtask, "Задача не найдена");

        historyManager.add(task);
        assertNotEquals(historyManager.getHistory().get(1), task, "Задача должна быть перемещена");
        assertEquals(historyManager.getHistory().get(2), task, "Задача должна быть перемещена");

        historyManager.add(task);
    }

    @Test
    void remove() {
        Epic epic = new Epic("Epic 1", "Description");
        Task task = new Task("Task 2", "Description");
        Subtask subtask = new Subtask("Subtask 3", "Description", TaskStatus.NEW, epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        historyManager.remove(subtask.getId());
        assertFalse(historyManager.getHistory().contains(subtask), "Задача не удалена");

        historyManager.remove(epic.getId());
        assertFalse(historyManager.getHistory().contains(epic), "Задача не удалена");
    }


    @Test
    void getDuplicateTasksHistory() {
        Epic epic = new Epic("Epic 1", "Description");
        Task task = new Task("Task 2", "Description");
        Subtask subtask = new Subtask("Subtask 3", "Description", TaskStatus.NEW, epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(subtask);

        assertEquals(historyManager.getHistory().size(), 3, "Некорректное кол-во элементов.");

        assertEquals(historyManager.getHistory().get(0), epic, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(1), task, "Задача не найдена");
        assertEquals(historyManager.getHistory().get(2), subtask, "Задача не найдена");
    }

}
