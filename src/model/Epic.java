package model;

import type.Status;
import type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return null;
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
            boolean isNew = false;
            boolean isDone = false;
            boolean isInProgress = false;

            for (Subtask task : children.values()) {
                switch (task.status) {
                    case NEW:
                        isNew = true;
                        break;
                    case DONE:
                        isDone = true;
                        break;
                    default:
                        isInProgress = true;
                }
            }

            if (isInProgress || (isNew && isDone)) {
                newStatus = Status.IN_PROGRESS;
            } else {
                newStatus = isNew ? Status.NEW : Status.DONE;
            }
        }

        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + Type.getTypeName(type) + '\'' +
                ", status='" + Status.getStatusName(status) + '\'' +
                ", children.size()=" + ((children != null) ? children.size() : 0) +
                '}';
    }


}
