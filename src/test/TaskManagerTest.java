package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static type.TaskStatus.*;

abstract class TaskManagerTest {

    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    protected TaskManager<Task> taskManager;

    KVServer server;

    @BeforeEach
    public void createManager() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void destroy() {
        server.stop();
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Покупки в магазине", "Пойти в магазин до 18:00 и сделать покупки");
        taskManager.addTask(epic);

        final Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewTask() {
        Task task = new Task("Покупки в магазине", "Пойти в магазин до 18:00 и сделать покупки", NEW);
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Subtask wrongSubtask = new Subtask("WrongSubtask", "Description", NEW, 200);
        assertNull(taskManager.getTaskById(wrongSubtask.getParentId()), "Эпик не найден");

        Epic epic = new Epic(1, "Epic 1", "Description 1");

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", NEW, 1);

        taskManager.addTask(epic);

        assertNotNull(taskManager.getTaskById(subtask1.getParentId()), "Эпик не найден");
        assertNotNull(taskManager.getTaskById(subtask1.getParentId()), "Эпик не найден");

        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);

        List<Integer> subtasks = taskManager.getTaskList().stream()
                .filter(task -> task instanceof Subtask)
                .map(Task::getId)
                .collect(Collectors.toList());

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");

        assertTrue(subtasks.contains(subtask1.getId()), "Задача не найдена");
        assertTrue(subtasks.contains(subtask2.getId()), "Задача не найдена");

        Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());
        assertTrue(savedEpic.getSubtasksIds().contains(subtask1.getId()), "Подзадача 1 не найдена в эпике");
        assertTrue(savedEpic.getSubtasksIds().contains(subtask2.getId()), "Подзадача 2 не найдена в эпике");
    }

    @Test
    void checkEmptyEpicStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(epic);
        assertEquals(taskManager.getTaskById(epic.getId()).getStatus(), NEW, "Неверный статус эпика");
    }

    @Test
    void checkEpicWithSubtasksAndStatusNewStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", NEW, epic.getId());

        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);

        assertEquals(taskManager.getTaskById(epic.getId()).getStatus(), NEW, "Неверный статус эпика");
    }

    @Test
    void checkEpicWithSubtasksAndStatusDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(epic);

        Subtask subtaskDone1 = new Subtask("Subtask 1", "Description 1", DONE, epic.getId());
        Subtask subtaskDone2 = new Subtask("Subtask 2", "Description 2", DONE, epic.getId());

        taskManager.addTask(subtaskDone1);
        taskManager.addTask(subtaskDone2);

        assertEquals(taskManager.getTaskById(epic.getId()).getStatus(), DONE, "Неверный статус эпика");
    }

    @Test
    void checkEpicWithSubtasksAndStatusDoneAndNew() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addTask(epic);

        Subtask subtaskNewDone1 = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());
        Subtask subtaskNewDone2 = new Subtask("Subtask 2", "Description 2", DONE, epic.getId());

        taskManager.addTask(subtaskNewDone1);
        taskManager.addTask(subtaskNewDone2);

        assertEquals(taskManager.getTaskById(epic.getId()).getStatus(), IN_PROGRESS, "Неверный статус эпика");
    }

    @Test
    void checkEpicWithSubtasksAndStatusInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(epic);

        Subtask subtaskInProgress1 = new Subtask("Subtask 1", "Description 1", IN_PROGRESS, epic.getId());
        Subtask subtaskInProgress2 = new Subtask("Subtask 2", "Description 2", IN_PROGRESS, epic.getId());

        taskManager.addTask(subtaskInProgress1);
        taskManager.addTask(subtaskInProgress2);

        assertEquals(taskManager.getTaskById(epic.getId()).getStatus(), IN_PROGRESS, "Неверный статус эпика");
    }

    @Test
    void getEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        taskManager.addTask(epic1);
        taskManager.addTask(epic2);

        List<Integer> epics = taskManager.getTaskList().stream()
                .filter(task -> task instanceof Epic)
                .map(Task::getId)
                .collect(Collectors.toList());

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");

        assertTrue(epics.contains(epic1.getId()), "Эпик не найден");
        assertTrue(epics.contains(epic2.getId()), "Эпик не найден");
    }

    @Test
    void getTasks() {
        Task task1 = new Task("Task 1", "Description 1", NEW);
        Task task2 = new Task("Task 2", "Description 2", IN_PROGRESS);
        Task task3 = new Task("Task 3", "Description 3", DONE);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        List<Integer> tasks = taskManager.getTaskList().stream()
                .filter(Objects::nonNull)
                .map(Task::getId)
                .collect(Collectors.toList());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");

        assertTrue(tasks.contains(task1.getId()), "Таск не найден");
        assertTrue(tasks.contains(task2.getId()), "Таск не найден");
        assertTrue(tasks.contains(task3.getId()), "Таск не найден");
    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", NEW, epic.getId());

        taskManager.addTask(epic);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);

        List<Integer> subtasks = taskManager.getTaskList().stream()
                .filter(task -> task instanceof Subtask)
                .map(Task::getId)
                .collect(Collectors.toList());

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");

        assertTrue(subtasks.contains(subtask1.getId()), "Задача не найдена");
        assertTrue(subtasks.contains(subtask2.getId()), "Задача не найдена");
    }

    @Test
    void getTaskById() {
        assertNull(taskManager.getTaskById(-1), "Задача не должна существовать");
        Task task = new Task("Task 1", "Description 1", NEW);

        taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(savedTask, task, "Задачи не совпадают");
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Subtask subtask = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());

        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        Task savedSubTask = taskManager.getTaskById(subtask.getId());

        assertNotNull(savedSubTask, "Задача не найдена");
        assertEquals(savedSubTask, savedSubTask, "Задачи не совпадают");

        assertNull(taskManager.getTaskById(666), "Задача не существует");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(epic);

        Task savedEpic = taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(savedEpic, epic, "Задачи не совпадают");
    }

    @Test
    void getSubtasksByEpicId() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Subtask subtask = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());

        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        List<Subtask> savedSubTasks = taskManager.getSubtaskByEpicId(epic.getId());
        assertTrue(savedSubTasks.contains(subtask), "Задача не найдена");
    }

    @Test
    void clearAllTasks() {
        taskManager.addTask(new Task("Task 1", "Description 1", NEW));
        taskManager.addTask(new Task("Task 2", "Description 2", DONE));

        taskManager.removeTaskList();
        assertNotNull(taskManager.getTaskList(), "Список null");
        assertEquals(taskManager.getTaskList().size(), 0, "Список не пустой");
    }

    @Test
    void clearAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 1", NEW, epic.getId());

        taskManager.addTask(epic);

        assertNotNull(taskManager.getTaskById(subtask1.getParentId()), "Эпик не найден");
        assertNotNull(taskManager.getTaskById(subtask2.getParentId()), "Эпик не найден");

        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);

        taskManager.removeTaskList();

        assertNotNull(taskManager.getTaskList().stream().filter(task -> task instanceof Subtask).collect(Collectors.toList()), "Список null");
        assertEquals(taskManager.getTaskList().stream().filter(task -> task instanceof Subtask).count(), 0, "Список не пустой");
    }

    @Test
    void clearAllEpics() {
        taskManager.addTask(new Epic("Epic 1", "Description 1"));
        taskManager.addTask(new Epic("Epic 2", "Description 2"));

        taskManager.removeTaskList();

        assertNotNull(taskManager.getTaskList(), "Список null");
        assertEquals(taskManager.getTaskList().size(), 0, "Список не пустой");
    }

    @Test
    void deleteAnyTaskById() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Task task = new Task("Task 1", "Description 1");
        Subtask subtask = new Subtask("Subtask 3", "Description 1", NEW, epic.getId());

        taskManager.addTask(epic);
        taskManager.addTask(task);
        taskManager.addTask(subtask);

        taskManager.removeTaskById(subtask.getId());
        assertEquals(taskManager.getTaskList().stream().filter(t -> t instanceof Subtask).count(), 0, "Список не пустой");

        taskManager.removeTaskById(epic.getId());
        assertEquals(taskManager.getTaskList().stream().filter(t -> t instanceof Epic).count(), 0, "Список не пустой");

        taskManager.removeTaskById(task.getId());
        assertEquals(taskManager.getTaskList().stream().filter(Objects::nonNull).count(), 0, "Список не пустой");
    }

    @Test
    void updateTask() {
        Task task = new Task("Task 1", "Description 1", NEW);
        Task updatedTask = new Task(task.getId(), "Task 1", "New Description 1", NEW);
        Task updatedTaskWrong = new Task(-1, "Task 1", "New Description 2", NEW);

        taskManager.addTask(task);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertEquals(updatedTask, savedTask, "Задачи не совпадают");
        assertNotEquals(updatedTaskWrong, savedTask, "Задачи не совпадают");
    }


    @Test
    void updateEpic() {
        Epic epic = new Epic("Epic 1", "Description");
        Epic updatedEpic = new Epic(epic.getId(), "Epic 1", "New Description 1");
        Epic updatedEpicWrong = new Epic(-1, "Epic 1", "New Description 2");

        taskManager.addTask(epic);
        taskManager.addTask(updatedEpic);

        Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());

        assertEquals(updatedEpic, savedEpic, "Задачи не совпадают");
        assertNotEquals(updatedEpicWrong, savedEpic, "Задачи не совпадают");
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("Epic 1", "Description 1");
        Task task = new Task("Task 2", "Description 1");
        Subtask subtask = new Subtask("Subtask 3", "Description 1", NEW, epic.getId());

        Task task2 = new Task(4, "Task 2", "Description 1");

        taskManager.addTask(epic);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(subtask.getId());
        taskManager.getTaskById(epic.getId());

        assertTrue(taskManager.history().contains(task.getId()), "Задача не найдена");
        assertTrue(taskManager.history().contains(subtask.getId()), "Задача не найдена");
        assertTrue(taskManager.history().contains(epic.getId()), "Задача не найдена");

        assertTrue(taskManager.history().contains(epic.getId()), "Задача не найдена");
        assertFalse(taskManager.history().contains(task2.getId()), "Задача не должна быть в истории");
    }

    @Test
    void getPrioritizedTasks() {
        Task nullTimeTask4 = new Task("Таск 3", "Таск 3");
        taskManager.addTask(nullTimeTask4);

        Task task2 = new Task("Таск 1", "Таск 1", LocalDateTime.parse("21.07.2022 15:00", dateTimeFormatter), 120, NEW);
        taskManager.addTask(task2);

        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addTask(epic);

        Subtask subtask0 = new Subtask("Сабтаск 1", "Сабтаск 1 эпика 1", LocalDateTime.parse("20.07.2022 12:00", dateTimeFormatter), 60, epic.getId());
        taskManager.addTask(subtask0);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск 2 эпика 1", LocalDateTime.parse("20.07.2022 15:00", dateTimeFormatter), 60, epic.getId());
        taskManager.addTask(subtask1);

        Task task3 = new Task("Таск 2", "Таск 2", LocalDateTime.parse("21.07.2022 19:00", dateTimeFormatter), 120, NEW);
        taskManager.addTask(task3);

        int position = 0;
        for (Task task : taskManager.getPrioritizedTasks()) {
            if (position == 0) {
                assertEquals(subtask0, task, "Задачи не совпадают");
            } else if (position == 1) {
                assertEquals(subtask1, task, "Задачи не совпадают");
            } else if (position == 2) {
                assertEquals(task2, task, "Задачи не совпадают");
            } else if (position == 3) {
                assertEquals(task3, task, "Задачи не совпадают");
            } else {
                assertEquals(nullTimeTask4, task, "Задачи не совпадают");
            }
            position++;
        }
    }

}
