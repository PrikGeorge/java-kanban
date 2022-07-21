package manager;

import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager<T extends Task> {

    // получение всех задач
    List<T> getTaskList();

    // удаление всех задач
    void removeTaskList();

    // получение задачи по идентификатору
    T getTaskById(int id);

    // добавление задачи в коллекцию
    void addTask(T task);

    // обновление задачи
    void updateTask(T task);

    // удаление задачи по идентификатору
    void removeTaskById(int id);

    // получение всех подзадач эпика по идентификатору
    List<Subtask> getSubtaskByEpicId(int epicId);

}
