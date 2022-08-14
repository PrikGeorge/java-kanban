package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import type.Type;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {

    private final HistoryManager<T> historyManager;
    private final Map<Integer, T> taskList;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(T task) {
        if (task instanceof Subtask) {
            ((Subtask) task).getParent().addChild((Subtask) task);
        }

        taskList.put(task.getId(), task);
        System.out.println(Type.getTypeName(task.getType()) + " '" + task.getName() + "' добавлен(а).");
    }

    @Override
    public List<T> getTaskList() {
        if (!taskList.isEmpty()) {
            return new ArrayList<>(taskList.values());
        }

        System.out.println("Список задач пуст.");
        return null;
    }

    @Override
    public void removeTaskList() {
        if (!taskList.isEmpty()) {
            taskList.clear();
        }

        System.out.println("Все задачи удалены.");
    }

    @Override
    public T getTaskById(int id) {
        if (taskList.containsKey(id)) {
            T task = taskList.get(id);
            historyManager.add(task);
            return task;
        }

        System.out.println("Задача с таким идентификатором не найдена.");
        return null;
    }

    @Override
    public void updateTask(T task) {
        if (taskList.containsKey(task.getId())) {

            if (task instanceof Subtask) {
                ((Subtask) task).getParent().updateChild((Subtask) task);
            }

            taskList.put(task.getId(), task);
            System.out.println(Type.getTypeName(task.getType()) + " '" + task.getName() + "' обновлен(а).");

        } else {
            System.out.println("Задача с таким идентификатором не найдена.");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (taskList.containsKey(id)) {
            T task = taskList.get(id);

            if (task instanceof Epic && ((Epic) task).getChildren() != null) {

                for (Subtask subtask : ((Epic) task).getChildren()) {
                    removeTaskById(subtask.getId());
                }

            } else if (task instanceof Subtask) {
                ((Subtask) task).getParent().deleteChild((Subtask) task);
            }

            taskList.remove(id);
            System.out.println(Type.getTypeName(task.getType()) + " '" + task.getName() + "' удален(а).");

        } else {
            System.out.println("Задача с таким идентификатором не найдена.");
        }
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {

        if (taskList.containsKey(epicId) && taskList.get(epicId) instanceof Epic) {
            return ((Epic) taskList.get(epicId)).getChildren();
        }

        System.out.println("Задача с таким идентификатором не найдена.");
        return Collections.emptyList();
    }

    @Override
    public List<Integer> history() {
        return historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList());
    }
}
