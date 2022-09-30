package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;

import java.util.*;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        this.status = TaskStatus.NEW;
    }
    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.type = TaskType.EPIC;
        this.status = TaskStatus.NEW;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + name +
                ", description=" + description +
                ", type=" + EnumHelper.getTypeName(type) +
                ", status=" + EnumHelper.getStatusName(status) +
                ", subtasksIds.size()=" + (Objects.nonNull(subtasksIds) ? subtasksIds.size() : 0) +
                '}';
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }
}
