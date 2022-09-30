package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;

public class Subtask extends Task {
    private final Epic parent;

    public Subtask(String name, String description, TaskStatus taskStatus, Epic parent) {
        super(name, description, taskStatus);
        this.type = TaskType.SUBTASK;
        this.parent = parent;
    }

    public Epic getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", parent.id=" + parent.getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + EnumHelper.getTypeName(type) + '\'' +
                ", status='" + EnumHelper.getStatusName(status) + '\'' +
                '}';
    }
}
