package manager;

import model.Subtask;

import java.util.List;
import java.util.Set;

public interface TaskManager<T> {

    /**
     * получение всех задач
     */
    List<T> getTaskList();

    /**
     * удаление всех задач
     */
    void removeTaskList();

    /**
     * получение задачи по идентификатору
     */
    T getTaskById(int id);

    /**
     * добавление задачи в коллекцию
     */
    void addTask(T task);

    /**
     * обновление задачи
     */
    void updateTask(T task);

    /**
     * удаление задачи по идентификатору
     */
    void removeTaskById(int id);

    /**
     * получение всех подзадач эпика по идентификатору
     */
    List<Subtask> getSubtaskByEpicId(int epicId);

    /**
     * получение истории просмотров задач
     */
    List<Integer> history();

    Set<T> getPrioritizedTasks();
}
