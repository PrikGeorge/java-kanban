package manager;

import exception.FileSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import type.TaskStatus;
import type.TaskType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager<T extends Task> extends InMemoryTaskManager<T> {

    private final String filePath;

    private static final String csvHeaderText = "id,type,name,status,description,startTime,duration,epic_id\n";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void addTask(T task) {
        super.addTask(task);
        save();
    }

    @Override
    public void removeTaskList() {
        super.removeTaskList();
        save();
    }

    @Override
    public T getTaskById(int id) {
        T task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(T task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        List<Subtask> subtasks = super.getSubtaskByEpicId(epicId);
        save();
        return subtasks;
    }

    protected void save() {

        try (Writer fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8)) {

            fileWriter.append(csvHeaderText);

            for (T task : getTaskList()) {
                fileWriter.append(toString(task)).append("\n");
            }

            fileWriter.append("\n");

            List<Task> taskList = historyManager.getHistory();
            String[] ids = new String[taskList.size()];

            for (int i = 0; i < taskList.size(); i++) {
                ids[i] = String.valueOf(taskList.get(i).getId());
            }

            fileWriter.append(String.join(",", ids));

        } catch (IOException e) {
            throw new FileSaveException("Ошибка при сохранении данных");
        }

    }

    private Task fromString(String value) {

        String[] values = value.split(",");

        switch (TaskType.valueOf(values[1])) {
            case EPIC:
                return new Epic(Integer.parseInt(values[0]), values[2], values[4]);
            case TASK:
                return new Task(Integer.parseInt(values[0]), values[2], values[4],
                        values[5].isEmpty() ? null : LocalDateTime.parse(values[4], formatter),
                        values[6].isEmpty() ? null : Integer.valueOf(values[5]),
                        TaskStatus.valueOf(values[3]));
            case SUBTASK:
                return new Subtask(Integer.parseInt(values[0]), values[2], values[4],
                        values[5].isEmpty() ? null : LocalDateTime.parse(values[4], formatter),
                        values[6].isEmpty() ? null : Integer.valueOf(values[5]),
                        TaskStatus.valueOf(values[3]),
                        Integer.parseInt(values[7]));
            default:
                return null;
        }
    }

    private String toString(Task task) {

        String startTime = Objects.nonNull(task.getStartTime()) ? formatter.format(task.getStartTime()) : "";
        String duration = Objects.nonNull(task.getDuration()) ? task.getDuration().toString() : "";

        String result = task.getId() + "," + task.getType() + "," +
                task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," +
                startTime + "," + duration;

        if (task instanceof Subtask) {
            result += "," + ((Subtask) task).getParentId();
        }

        return result;
    }

    public static FileBackedTasksManager<Task> loadFromFile(String filePath) {
        final FileBackedTasksManager<Task> taskManager = new FileBackedTasksManager<>(filePath);

        try {
            String fileContent = Files.readString(Path.of(filePath));
            String[] lines = fileContent.split("\n");

            for (int i = 1; i < lines.length; i++) {

                if (!lines[i].isBlank()) {

                    if (taskManager.fromString(lines[i]) != null) {

                        taskManager.addTask(taskManager.fromString(lines[i]));
                    }
                } else {
                    String historyLine = lines[i + 1];
                    String[] ids = historyLine.split(",");

                    for (String id : ids) {
                        int taskId = Integer.parseInt(id);
                        taskManager.historyManager.add(taskManager.getTaskById(taskId));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new FileSaveException("Ошибка при загрузке данных из файла");
        }
        return taskManager;
    }

}
