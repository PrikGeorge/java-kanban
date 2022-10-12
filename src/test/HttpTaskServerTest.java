package test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import type.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskServerTest {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Gson gson = new Gson();

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private HttpTaskServer server;

    private KVServer kvServer;

    @BeforeEach
    public void create() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();

        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        List<Task> historyTasks = List.copyOf(historyManager.getHistory());

        for (Task task : historyTasks) {
            historyManager.remove(task.getId());
        }
    }

    @AfterEach
    public void destroy() {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size());
    }

    @Test
    public void addTaskAndGetTasks() throws IOException, InterruptedException {

        Task nullTimeTask4 = new Task("Таск 3", "Таск 3");
        server.getTaskManager().addTask(nullTimeTask4);

        Task task2 = new Task("Таск 1", "Таск 1", LocalDateTime.parse("21.07.2022 15:00", dateTimeFormatter), 120, TaskStatus.NEW);
        server.getTaskManager().addTask(task2);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        server.getTaskManager().addTask(epic);

        Subtask subtask0 = new Subtask("Сабтаск 1", "Сабтаск 1 эпика 1", LocalDateTime.parse("20.07.2022 12:00", dateTimeFormatter), 60, epic.getId());
        server.getTaskManager().addTask(subtask0);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск 2 эпика 1", LocalDateTime.parse("20.07.2022 15:00", dateTimeFormatter), 60, epic.getId());
        server.getTaskManager().addTask(subtask1);

        Task task3 = new Task("Таск 2", "Таск 2", LocalDateTime.parse("21.07.2022 19:00", dateTimeFormatter), 120, TaskStatus.NEW);
        server.getTaskManager().addTask(task3);


        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Set<Task> responseTasks = gson.fromJson(response.body(), new TypeToken<Set<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        int position = 0;
        for (Task task : responseTasks) {
            if (position == 0) {
                assertEquals(subtask0.getId(), task.getId(), "Задачи не совпадают");
            } else if (position == 1) {
                assertEquals(subtask1.getId(), task.getId(), "Задачи не совпадают");
            } else if (position == 2) {
                assertEquals(task2.getId(), task.getId(), "Задачи не совпадают");
            } else if (position == 3) {
                assertEquals(task3.getId(), task.getId(), "Задачи не совпадают");
            } else {
                assertEquals(nullTimeTask4.getId(), task.getId(), "Задачи не совпадают");
            }
            position++;
        }
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size());
    }

    @Test
    public void addAnyTasksAndGetHistory() throws IOException, InterruptedException {
        HistoryManager<Task> historyManager = Managers.getDefaultHistory();

        Epic epic = new Epic("Epic 1", "Описание эпика");
        historyManager.add(epic);
        historyManager.add(new Task("Task 2", "Описание таска"));
        historyManager.add(new Subtask("Subtask 3", "Описание сабтаска", TaskStatus.NEW, epic.getId()));

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Integer> tasks = gson.fromJson(response.body(), new TypeToken<List<Integer>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(3, tasks.size());
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void addTaskAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Описание таска");
        server.getTaskManager().addTask(task);

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task responseTask = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(task.getId(), responseTask.getId());
    }

    @Test
    public void pushTaskAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Описание таска");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно добавлена.", response.body());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void pushTaskAndUpdateAndDeleteAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Описание таска");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно добавлена.", response.body());

        task.setName("Update task 1");
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно обновлена.", response.body());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно удалена.", response.body());
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Integer> responseTasks = gson.fromJson(response.body(), new TypeToken<List<Integer>>() {
        }.getType());

        assertNull(responseTasks);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void addEpicAndGetEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Описание эпика");
        server.getTaskManager().addTask(task);

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic responseTask = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());

        assertEquals(task.getId(), responseTask.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void pushEpicAndGetEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Описание эпика");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно добавлена.", response.body());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic responseTask = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());

        assertEquals(task.getId(), responseTask.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void pushEpicAndUpdateAndDeleteAndGetEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Описание эпика");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно добавлена.", response.body());

        task.setName("Update task 1");
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно обновлена.", response.body());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно удалена.", response.body());
    }

    //=====
    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Integer> tasks = gson.fromJson(response.body(), new TypeToken<List<Integer>>() {
        }.getType());

        assertNull(tasks);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtasks/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void addSubtaskAndGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Описание эпика");
        Subtask task = new Subtask("Subtask 3", "Описание сабтаска", TaskStatus.NEW, epic.getId());

        server.getTaskManager().addTask(epic);
        server.getTaskManager().addTask(task);

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask responseTasks = gson.fromJson(response.body(), new TypeToken<Subtask>() {
        }.getType());

        assertEquals(task.getId(), responseTasks.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void pushSubtaskAndGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Описание эпика");
        Subtask task = new Subtask("Subtask 3", "Описание сабтаска", TaskStatus.NEW, epic.getId());

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно добавлена.", response.body());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask responseTasks = gson.fromJson(response.body(), new TypeToken<Subtask>() {
        }.getType());

        assertEquals(task.getId(), responseTasks.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена.", response.body());
    }

    @Test
    public void pushSubtaskAndUpdateAndDeleteAndGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Описание эпика");
        Subtask subtask = new Subtask("Subtask 3", "Описание сабтаска", TaskStatus.NEW, epic.getId());

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        URI url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        subtask.setName("Update task 1");
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно обновлена.", response.body());

        url = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/?id=" + subtask.getId());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача успешно удалена.", response.body());
    }
}
