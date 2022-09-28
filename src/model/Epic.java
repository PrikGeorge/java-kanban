package model;

import type.Status;
import type.Type;

import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private Map<Integer, Subtask> children = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        this.status = Status.NEW;
    }

    public List<Subtask> getChildren() {
        if (!children.isEmpty()) {
            return new ArrayList<>(children.values());
        }
        return Collections.emptyList();
    }

    public void updateChild(Subtask child) {
        if (child != null && children.containsKey(child.getId())) {
            children.put(child.getId(), child);
            updateStatus();
        }
    }

    public void addChild(Subtask child) {
        if (child != null) {
            this.children.put(child.getId(), child);
            updateStatus();
        }
    }

    public void deleteChild(Subtask child) {
        if (child != null && this.children.containsKey(child.getId())) {
            this.children.remove(child.getId());
            updateStatus();
        }
    }

    private void updateStatus() {
        Status newStatus = Status.NEW;

        if (children.size() > 0) {
            Map<Status, Long> map = children.values()
                    .stream()
                    .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

            newStatus = map.containsKey(Status.IN_PROGRESS) || (map.containsKey(Status.NEW) && map.containsKey(Status.DONE)) ?
                    Status.IN_PROGRESS : map.containsKey(Status.NEW) ?
                    Status.NEW : Status.DONE;
        }

        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + Type.getTypeName(type) + '\'' +
                ", status='" + Status.getStatusName(status) + '\'' +
                ", children.size()=" + ((children != null) ? children.size() : 0) +
                '}';
    }


}
