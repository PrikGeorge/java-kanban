package manager;

import exception.FileSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import type.TaskStatus;
import type.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTasksManager<T extends Task> extends InMemoryTaskManager<T> {

    private final File file;

    private static final String csvHeaderText = "id,type,name,status,description,epic_id\n";

    public FileBackedTasksManager(File file) {
        this.file = file;
        this.load();
    }

    public static void main(String[] args) {

        String filePath = System.getProperty("user.dir") + "/local_storage.csv";

        TaskManager<Task> firstManager = Managers.getDefaultLoadFile(new File(filePath));

        /** создаем эпик "магазин" */
        Epic epicShop = new Epic("Покупки в магазине", "Пойти в магазин до 18:00 и сделать покупки");

        /** создаем подзадачи для эпика "магазин" */
        Subtask subtaskBuyMilk = new Subtask("Купить молоко", "Простоквашино", TaskStatus.NEW, epicShop.getId());
        Subtask subtaskBuyBred = new Subtask("Купить хлеб", "Нарезной", TaskStatus.NEW, epicShop.getId());

        /** создаем эпик "работа" */
        Epic epicWork = new Epic("Закончить работу пораньше", "Выйти с работы до 19:00");

        /** создаем подзадачи для эпика "работа"*/
        Subtask subtaskEndProject = new Subtask("Закончить проект", "Реализовать все классы и методы", TaskStatus.NEW, epicWork.getId());

        firstManager.addTask(epicShop);
        firstManager.addTask(epicWork);
        firstManager.addTask(subtaskBuyMilk);
        firstManager.addTask(subtaskBuyBred);
        firstManager.addTask(subtaskEndProject);

        /** заполнение истории*/
        firstManager.getTaskById(epicWork.getId());
        firstManager.getTaskById(subtaskEndProject.getId());
        firstManager.getTaskById(epicWork.getId());
        firstManager.getTaskById(subtaskEndProject.getId());
        firstManager.getTaskById(subtaskEndProject.getId());
        firstManager.getTaskById(subtaskBuyMilk.getId());
        firstManager.getTaskById(subtaskBuyBred.getId());
        firstManager.getTaskById(epicShop.getId());
        System.out.println(firstManager.history());

        /** создание второго фаил-менеджера */
        TaskManager<Task> secondManager = Managers.getDefaultLoadFile(new File(filePath));

        /** сверка задач двух фаил-менеджеров*/
        for (int i = 0; i < secondManager.getTaskList().size(); i++) {
            if (!firstManager.getTaskList().contains(secondManager.getTaskList().get(i))) {
                System.out.println("Задача не найдена!");
                return;
            }
        }

        /** сверка итории двух фаил-менеджеров */
        if (!secondManager.history().equals(firstManager.history())) {
            System.out.println("История не совпадает!");
            return;
        }

        System.out.println("Данные совпадают");
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

    private void load() {

        if (!file.exists()) {
            return;
        }

        try {
            String fileContent = Files.readString(Path.of(file.getPath()));
            String[] lines = fileContent.split("\n");

            for (int i = 1; i < lines.length; i++) {

                if (!lines[i].isBlank()) {
                    addTask(fromString(lines[i]));
                } else {
                    String historyLine = lines[i + 1];
                    String[] ids = historyLine.split(",");

                    for (String id : ids) {
                        int taskId = Integer.parseInt(id);
                        historyManager.add(getTaskById(taskId));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new FileSaveException("Ошибка при загрузке данных из файла");
        }
    }

    private void save() {

        try (Writer fileWriter = new FileWriter(file.getPath(), StandardCharsets.UTF_8)) {

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

    private T fromString(String value) {

        String[] values = value.split(",");

        switch (TaskType.valueOf(values[1])) {
            case EPIC:
                return (T) new Epic(Integer.parseInt(values[0]), values[2], values[4]);
            case TASK:
                return (T) new Task(Integer.parseInt(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]));
            case SUBTASK:
                return (T) new Subtask(Integer.parseInt(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                        Integer.parseInt(values[5]));
            default:
                return null;
        }
    }

    private String toString(Task task) {

        String result = task.getId() + "," + task.getType() + "," +
                task.getName() + "," + task.getStatus() + "," + task.getDescription();

        if (task instanceof Subtask) {
            result += "," + ((Subtask) task).getParentId();
        }

        return result;
    }

}
