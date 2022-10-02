package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int parentId;

    public Subtask(String name, String description, TaskStatus taskStatus, int parentId) {
        super(name, description, taskStatus);
        this.type = TaskType.SUBTASK;
        this.parentId = parentId;
    }

    public Subtask(int id, String name, String description, LocalDateTime startTime, Integer duration, TaskStatus taskStatus, int parentId) {
        super(id, name, description, startTime, duration, taskStatus);
        this.type = TaskType.SUBTASK;
        this.parentId = parentId;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Integer duration, int parentId) {
        super(name, description, startTime, duration, TaskStatus.NEW);
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
                ", duration=" + duration +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

}
