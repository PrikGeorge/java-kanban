package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import type.Status;
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
        taskList.put(task.getId(), task);

        if (task instanceof Subtask) {
            Epic epic = (Epic) taskList.get(((Subtask) task).getParent().getId());
            epic.getSubtasksIds().add(task.getId());
            updateStatus(task.getId());
        }

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
            taskList.put(task.getId(), task);
            updateStatus(task.getId());

            System.out.println(Type.getTypeName(task.getType()) + " '" + task.getName() + "' обновлен(а).");
        } else {
            System.out.println("Задача с таким идентификатором не найдена.");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (taskList.containsKey(id)) {
            T task = taskList.get(id);
            if (task instanceof Epic && Objects.nonNull(((Epic) task).getSubtasksIds())) {

                List<Integer> subtasksIds = new ArrayList<>(((Epic) task).getSubtasksIds());
                for (Integer subtaskId : subtasksIds ) {
                    removeTaskById(subtaskId);
                }

            } else if (task instanceof Subtask) {
                Epic epic = (Epic) taskList.get(((Subtask) task).getParent().getId());
                epic.getSubtasksIds().remove((Integer) id);
            }

            taskList.remove(id);
            historyManager.remove(id);
            updateStatus(task.getId());
            System.out.println(Type.getTypeName(task.getType()) + " '" + task.getName() + "' удален(а).");

        } else {
            System.out.println("Задача с таким идентификатором не найдена.");
        }
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {

        if (taskList.containsKey(epicId) && taskList.get(epicId) instanceof Epic) {
            List<Subtask> subtasks = new ArrayList<>();

            for (Integer subtaskId : ((Epic) taskList.get(epicId)).getSubtasksIds()) {
                subtasks.add((Subtask) taskList.get(subtaskId));
            }

            return subtasks;
        }

        System.out.println("Задача с таким идентификатором не найдена.");
        return Collections.emptyList();
    }

    @Override
    public List<Integer> history() {
        return historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList());
    }

    private void updateStatus(int epicId) {
        T epic = taskList.get(epicId);

        if (Objects.nonNull(epic)) {

            Status newStatus = Status.NEW;
            List<Subtask> subtasks = epic instanceof Epic ? getSubtaskByEpicId(epicId) : new ArrayList<Subtask>(epic);

            if (subtasks.size() > 0) {
                Map<Status, Long> map = subtasks
                        .stream()
                        .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

                newStatus = map.containsKey(Status.IN_PROGRESS) || map.containsKey(Status.DONE) ?
                        Status.IN_PROGRESS : map.containsKey(Status.NEW) ?
                        Status.NEW : Status.DONE;
            }

            epic.setStatus(newStatus);
        }
    }
}
