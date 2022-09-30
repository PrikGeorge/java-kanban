package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;

public class Subtask extends Task {
    private final int parentId;

    public Subtask(String name, String description, TaskStatus taskStatus, int parentId) {
        super(name, description, taskStatus);
        this.type = TaskType.SUBTASK;
        this.parentId = parentId;
    }
    public Subtask(int id, String name, String description, TaskStatus taskStatus, int parentId) {
        super(id, name, description, taskStatus);
        this.type = TaskType.SUBTASK;
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", parent.id=" + parentId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + EnumHelper.getTypeName(type) + '\'' +
                ", status='" + EnumHelper.getStatusName(status) + '\'' +
                '}';
    }

}
