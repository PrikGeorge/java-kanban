package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import type.TaskStatus;
import utils.EnumHelper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {

    protected final HistoryManager<Task> historyManager;

    private final Map<Integer, T> taskList;

    private final Set<T> prioritizedTasks = new TreeSet<T>((elem1, elem2) -> {

        if (elem1.getStartTime() == null && elem2.getStartTime() == null) {
            return elem1.getId() > elem2.getId() ? 1 : -1;

        } else if (elem1.getStartTime() == null) {
            return 1;

        } else {
            if (elem2.getStartTime() != null) {
                return elem1.getStartTime().compareTo(elem2.getStartTime());
            } else {
                return -1;
            }
        }
    });

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(T task) {
        checkTaskIsInFreeInterval(task);
        taskList.put(task.getId(), task);

        if (task instanceof Subtask) {
            Epic epic = (Epic) taskList.get(((Subtask) task).getParentId());
            epic.getSubtasksIds().add(task.getId());
            updateStatus(((Subtask) task).getParentId());
        }

        System.out.println(EnumHelper.getTypeName(task.getType()) + " '" + task.getName() + "' добавлен(а).");
    }

    @Override
    public List<T> getTaskList() {
        if (!taskList.isEmpty()) {
            return new ArrayList<>(taskList.values());
        }

        System.out.println("Список задач пуст.");
        return new ArrayList<>();
    }

    @Override
    public void removeTaskList() {
        if (!taskList.isEmpty()) {
            for (Map.Entry<Integer, T> entry : taskList.entrySet()) {
                historyManager.remove(entry.getValue().getId());
            }
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
        checkTaskIsInFreeInterval(task);

        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);

            if (task instanceof Subtask) {
                updateStatus(((Subtask) task).getParentId());
            }

            System.out.println(EnumHelper.getTypeName(task.getType()) + " '" + task.getName() + "' обновлен(а).");
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
                for (Integer subtaskId : subtasksIds) {
                    removeTaskById(subtaskId);
                }

            } else if (task instanceof Subtask) {
                Epic epic = (Epic) taskList.get(((Subtask) task).getParentId());
                epic.getSubtasksIds().remove((Integer) id);
                updateStatus(epic.getId());
            }

            taskList.remove(id);
            historyManager.remove(id);

            System.out.println(EnumHelper.getTypeName(task.getType()) + " '" + task.getName() + "' удален(а).");

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
        T task = taskList.get(epicId);

        if (Objects.nonNull(task) && task instanceof Epic) {
            Epic epic = (Epic) task;
            TaskStatus newTaskStatus = TaskStatus.NEW;
            List<Subtask> subtasks = getSubtaskByEpicId(epicId);

            int duration = 0;
            LocalDateTime firstDate = null;
            LocalDateTime lastDate = null;

            if (Objects.nonNull(subtasks) && subtasks.size() > 0) {
                Map<TaskStatus, Long> map = subtasks
                        .stream()
                        .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

                newTaskStatus = map.containsKey(TaskStatus.IN_PROGRESS) || (map.containsKey(TaskStatus.NEW) && map.containsKey(TaskStatus.DONE)) ?
                        TaskStatus.IN_PROGRESS : map.containsKey(TaskStatus.NEW) ?
                        TaskStatus.NEW : TaskStatus.DONE;

                for (Subtask subtask : subtasks) {
                    if (Objects.nonNull(subtask.getStartTime()) && Objects.nonNull(subtask.getDuration())) {

                        if (Objects.isNull(lastDate)) {
                            lastDate = subtask.getEndTime();
                        }
                        if (Objects.isNull(firstDate)) {
                            firstDate = subtask.getStartTime();
                        }

                        if (subtask.getEndTime().isAfter(lastDate)) {
                            lastDate = subtask.getEndTime();
                        }
                        if (subtask.getStartTime().isBefore(firstDate)) {
                            firstDate = subtask.getStartTime();
                        }

                        duration += subtask.getDuration();
                    }
                }
            }

            epic.setStartTime(firstDate);
            epic.setEndTime(lastDate);
            epic.setDuration(duration);

            epic.setStatus(newTaskStatus);
        }
    }

    public Set<T> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void checkTaskIsInFreeInterval(T task) {
        if (task.getStartTime() == null) {
            return;
        }

        for (Task savedTask : getPrioritizedTasks()) {

            if (savedTask.getStartTime() != null &&
                    ((savedTask.getStartTime().isBefore(task.getStartTime()) && savedTask.getEndTime().isAfter(task.getStartTime()))
                            ||
                            savedTask.getStartTime().isBefore(task.getEndTime()) && savedTask.getEndTime().isAfter(task.getEndTime()))) {

                throw new IllegalArgumentException("Дата задачи пересекается с датой задачи " + savedTask.getName());
            }
        }
    }
}
